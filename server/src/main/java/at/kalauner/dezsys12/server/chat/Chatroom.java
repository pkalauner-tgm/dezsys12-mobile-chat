package at.kalauner.dezsys12.server.chat;

import javax.ws.rs.container.AsyncResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a chatroom
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160229.1
 */
public class Chatroom {
    private String id;
    private Map<String, AsyncResponse> waiters;
    private ExecutorService ex;

    private MessageRepository messageRepository;

    public Chatroom(MessageRepository messageRepository) {
        this("Default", messageRepository);
    }

    public Chatroom(String id, MessageRepository messageRepository) {
        this.id = id;
        this.messageRepository = messageRepository;
        this.waiters = new ConcurrentHashMap<>();
        this.ex = Executors.newSingleThreadExecutor();
    }


    /**
     * Receives all messages since the given message, or keeps the connection alive until a new message arrives
     *
     * @param uuid session id
     * @param asyncResp AsyncResponse
     * @param messageindex index of last message
     */
    public void waitForMessage(String uuid, AsyncResponse asyncResp, int messageindex) {
        this.waiters.put(uuid, asyncResp);

        if (messageindex != -2) {
            List<Message> messages = this.messageRepository.getMessages(id, messageindex + 1);
            if (!messages.isEmpty()) {
                asyncResp.resume(messages);
            }
        }
    }

    /**
     * Broadcasts a message
     *
     * @param message the message which should be broadcasted
     */
    public void sendMessage(Message message) {
        this.messageRepository.addMessage(message);
        ex.submit((Runnable) () -> {
            Set<String> sids = waiters.keySet();
            sids.forEach(cur -> waiters.get(cur).resume(message));
        });
    }
}
