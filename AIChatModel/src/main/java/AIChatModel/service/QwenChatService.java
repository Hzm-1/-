package AIChatModel.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface QwenChatService {

    SseEmitter streamChat(String question, Integer userId, String chatSessionId);

}
