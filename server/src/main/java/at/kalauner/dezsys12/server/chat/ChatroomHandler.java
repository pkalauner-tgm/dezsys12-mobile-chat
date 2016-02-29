package at.kalauner.dezsys12.server.chat;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.container.AsyncResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Handles the chatrooms
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160229.1
 */
@Named
@Component
@Singleton
public class ChatroomHandler {

    private Cache<String, Map<String, AsyncResponse>> cache;

    private final ExecutorService ex = Executors.newSingleThreadExecutor();

    private static final String DEFAULT_CHATROOM = "Default";
    @Inject
    private ChatRepository chatRepository;


    public ChatroomHandler() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    }


    public void waitForMessage(String chatroomid, String uuid, AsyncResponse asyncResp, int messageindex) {
        if (chatroomid == null)
            chatroomid = DEFAULT_CHATROOM;

        this.getMapForChatRoom(chatroomid).put(uuid, asyncResp);

        if (messageindex != -2) {
            List<Message> messages = this.chatRepository.getMessages(chatroomid, messageindex + 1);

            if (!messages.isEmpty()) {
                asyncResp.resume(messages);
            }
        }
    }


    public void sendMessage(Message message) {
        if (message.getChatRoomId() == null)
            message.setChatRoomId(DEFAULT_CHATROOM);

        this.chatRepository.addMessage(message);
        Map<String, AsyncResponse> waiters = getMapForChatRoom(message.getChatRoomId());

        ex.submit((Runnable) () -> {
            Set<String> sids = waiters.keySet();
            sids.forEach(cur -> waiters.get(cur).resume(message));
        });
    }


    private Map<String, AsyncResponse> getMapForChatRoom(String chatroomid) {
        Map<String, AsyncResponse> map = cache.getIfPresent(chatroomid);

        if (map == null) {
            map = new ConcurrentHashMap<>();
            cache.put(chatroomid, map);
        }
        return map;
    }
}
