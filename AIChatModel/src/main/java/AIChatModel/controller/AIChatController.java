package AIChatModel.controller;

import AIChatModel.service.QwenChatService;
import AIChatModel.utils.AIOperator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/AI")
public class AIChatController {
    @Resource
    private QwenChatService qwenChatService;

    /**
     * AI 聊天流式接口
     * @param question 用户提问内容
     * @return SseEmitter 流式响应对象
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam("question") String question,@RequestParam("userId") Integer userId,@RequestParam("chatSessionId") String chatSessionId) {
        log.info("用户提问：{}", question);
        return qwenChatService.streamChat(question,userId,chatSessionId);
    }

    /**
     * 测试同步调用接口
     */
    @GetMapping("/chat/sync")
    public String syncChat(@RequestParam("question") String question) {
        try {
            return AIOperator.simpleMultiModalConversationCall(question);
        } catch (Exception e) {
            return "调用失败: " + e.getMessage();
        }
    }

    /**
     * 测试流式调用接口（控制台输出）
     */
    @GetMapping("/chat/test-stream")
    public String testStream(@RequestParam("question") String question) {
        try {
            System.out.println("开始流式调用...");
            AIOperator.streamMultiModalConversationCall(question, content -> {
                System.out.print(content);
            });
            System.out.println("\n流式调用完成");
            return "流式调用已完成，请查看控制台输出";
        } catch (Exception e) {
            return "流式调用失败: " + e.getMessage();
        }
    }

}
