package AIChatModel.service.Impl;

import AIChatModel.mapper.AIChatMapper;
import AIChatModel.service.QwenChatService;
import AIChatModel.utils.AIOperator;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import model.AIChatModel.AIChatMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class QwenChatServiceImpl implements QwenChatService {

    @Resource
    private AIChatMapper aiChatMapper;

    // 使用固定大小的线程池，避免无限创建线程
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Transactional
    public SseEmitter streamChat(String question, Integer userId, String chatSessionId) {
        // 设置较长的超时时间（5分钟），给AI足够的时间生成回复
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(5));

        StringBuilder content = new StringBuilder();
        AIChatMessage aiChatMessage = new AIChatMessage();
        aiChatMessage.setMessageStatus(1);

        AIChatMessage userChatMessage = new AIChatMessage();
        userChatMessage.setChatSessionId(chatSessionId);
        userChatMessage.setUserId(userId);
        userChatMessage.setMessageRole("USER");
        userChatMessage.setMessageContent(question);
        userChatMessage.setMessageType("TEXT");
        userChatMessage.setSendTime(LocalDateTime.now());
        userChatMessage.setMessageStatus(1);

        aiChatMapper.createChatRecord(userChatMessage);

        executor.execute(() -> {
            try {
                Flowable<MultiModalConversationResult> flowable = AIOperator.streamWithSseEmitter(question);

                flowable
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .subscribe(
                                result -> {
                                    try {
                                        processResult(result, emitter, content);
                                    } catch (Exception e) {
                                        aiChatMessage.setErrorMsg(e.getMessage());
                                        log.error("处理流式数据时发生异常: {}" , e.getMessage());
                                    }
                                },
                                error -> {
                                    log.error("流式调用发生错误: {}", error.getMessage());
                                    aiChatMessage.setErrorMsg(error.getMessage());
                                    try {
                                        emitter.send(SseEmitter.event().data("[ERROR]"));
                                    } catch (Exception e) {
                                        log.error("发送错误信息时异常: {}", e.getMessage());
                                    }
                                    // 错误时保存
                                    saveAiChatMessage(aiChatMessage, content, chatSessionId, userId, false);
                                    emitter.completeWithError(error);
                                },
                                () -> {
                                    try {
                                        emitter.send(SseEmitter.event().data("{\"type\":\"complete\",\"content\":\"\"}"));
                                        emitter.complete();
                                        log.info("流式调用完成");
                                    } catch (Exception e) {
                                        aiChatMessage.setErrorMsg(e.getMessage());
                                        log.error("发送完成标记时发生异常: {}" , e.getMessage());
                                        emitter.complete();
                                    }
                                    // 成功完成时保存
                                    saveAiChatMessage(aiChatMessage, content, chatSessionId, userId, true);
                                }
                        );
            } catch (Exception e) {
                log.error("启动流式调用时发生异常: {}" , e.getMessage());
                emitter.completeWithError(e);
            }
            // 移除finally块，不在这里保存
        });

        // 处理连接超时/异常
        emitter.onTimeout(() -> {
            log.error("SSE连接超时");
            emitter.completeWithError(new RuntimeException());
        });
        
        emitter.onError(error -> {
            log.error("SSE连接错误: {}", error.getMessage());
            // 不要立即completeWithError，让前端有机会处理
            try {
                emitter.send(SseEmitter.event().data("[CONNECTION_ERROR]"));
            } catch (Exception e) {
                // 忽略发送错误
            }
            emitter.completeWithError(error);
        });

        // 连接建立时的回调
        emitter.onCompletion(() -> {
            log.info("SSE连接正常完成");
        });

        return emitter;
    }

    // 提取公共保存方法
    private void saveAiChatMessage(AIChatMessage aiChatMessage, StringBuilder content,
                                   String chatSessionId, Integer userId , boolean success) {
        // 如果还没有设置状态，默认为成功
        if (aiChatMessage.getMessageStatus() == null) {
            aiChatMessage.setMessageStatus(1);
        }
        if (!success){
            aiChatMessage.setMessageStatus(0);
        }

        aiChatMessage.setChatSessionId(chatSessionId);
        aiChatMessage.setUserId(userId);
        aiChatMessage.setMessageRole("AI");
        aiChatMessage.setMessageContent(content.toString());
        aiChatMessage.setMessageType("TEXT");
        aiChatMessage.setSendTime(LocalDateTime.now());

        aiChatMapper.createChatRecord(aiChatMessage);
    }

    /**
     * 处理AI返回的结果
     */
    private void processResult(MultiModalConversationResult result, SseEmitter emitter,StringBuilder aiChatResponse) {
        if (result == null) return;
        
        try {
            // 添加安全检查，防止IndexOutOfBoundsException
            if (result.getOutput() != null && 
                result.getOutput().getChoices() != null && 
                !result.getOutput().getChoices().isEmpty()) {
                
                var choice = result.getOutput().getChoices().get(0);
                if (choice != null && 
                    choice.getMessage() != null && 
                    choice.getMessage().getContent() != null && 
                    !choice.getMessage().getContent().isEmpty()) {
                    
                    Map<String, Object> contentObj = choice.getMessage().getContent().get(0);
                    if (contentObj != null) {
                        Object textObj = contentObj.get("text");
                        if (textObj != null && !textObj.toString().isEmpty()) {
                            String content = textObj.toString();

                            aiChatResponse.append(content);

                            // 发送JSON格式数据
                            String jsonData = String.format("{\"type\":\"chunk\",\"content\":\"%s\"}", 
                                                          content.replace("\"", "\\\""));
                            emitter.send(SseEmitter.event().data(jsonData));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理结果数据时发生异常: {}", e.getMessage());
        }
    }
}
