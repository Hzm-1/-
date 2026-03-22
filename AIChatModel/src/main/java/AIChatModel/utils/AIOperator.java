package AIChatModel.utils;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.Constants;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

@Component
public class AIOperator {
    private static final String APIKEY = "sk-eff664cf88dd4697bca94adc003c4eaa";

    static {
        Constants.baseHttpApiUrl="https://dashscope.aliyuncs.com/api/v1";
    }

    /**
     * 单纯进行文本对话调用方法
     * @param question
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public static String simpleMultiModalConversationCall(String question)
            throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("text", question))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(APIKEY)
                .model("qwen3.5-plus")
                .messages(Arrays.asList(userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
        return (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
    }

    /**
     * 进行图片+对话的形式
     * @param question
     * @param imageAddress
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public static String simpleMultiModalConversationCall(String question,String imageAddress)
            throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", imageAddress),
                        Collections.singletonMap("text", question))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(APIKEY)
                .model("qwen3.5-plus")
                .messages(Arrays.asList(userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
        return (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
    }

    /**
     * 单纯进行文本对话调用方法（流式输出）
     * @param question 用户问题
     * @param responseHandler 响应处理器，用于处理每个流式返回的数据块
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public static void streamMultiModalConversationCall(String question, Consumer<String> responseHandler)
            throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("text", question))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(APIKEY)
                .model("qwen3.5-plus")
                .messages(Arrays.asList(userMessage))
                .incrementalOutput(true) // 开启增量输出
                .build();

        Flowable<MultiModalConversationResult> flowable = conv.streamCall(param);
        flowable.blockingForEach(result -> {
            // 处理每个流式返回的数据块
            String content = (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
            responseHandler.accept(content);
        });
    }

    /**
     * 通过SseEmitter进行流式输出
     * @param question 用户问题
     */
    public static Flowable<MultiModalConversationResult> streamWithSseEmitter(String question) throws Exception {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("text", question))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(APIKEY)
                .model("qwen3.5-plus")
                .messages(Arrays.asList(userMessage))
                .incrementalOutput(true) // 开启增量输出
                .build();

        return conv.streamCall(param);
    }

    public static void main(String[] args) throws Exception {
        Flowable<MultiModalConversationResult> flowable = streamWithSseEmitter("你是谁");
        flowable.subscribe(
                result -> {
                    try{
                        String content = (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
                        System.out.println( content);
                    }catch (Exception e){
                        System.out.println("流式调用异常");
                    }
                }
        );
    }
}
