package at.kalauner.dezsys12.server.chat;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.AsyncResponse;
import java.util.concurrent.TimeUnit;

/**
 * In-memory ChatroomHandler
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160229.1
 */
@Component
@Singleton
@Primary
public class DefaultChatroomHandler implements ChatroomHandler {
    private Cache<String, Chatroom> cache;

    @Inject
    private MessageRepository messageRepository;

    /**
     * Default constructor
     */
    public DefaultChatroomHandler() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    }


    @Override
    public void waitForMessage(String chatroomid, String uuid, AsyncResponse asyncResp, int messageindex) {
        this.getChatroom(chatroomid).waitForMessage(uuid, asyncResp, messageindex);
    }


    @Override
    public void sendMessage(Message message) {
        this.getChatroom(message.getChatRoomId()).sendMessage(message);
    }


    private Chatroom getChatroom(String chatroomid) {
        Chatroom chatroom = cache.getIfPresent(chatroomid);

        if (chatroom == null) {
            chatroom = new Chatroom(chatroomid, messageRepository);
            cache.put(chatroomid, chatroom);
        }
        return chatroom;
    }
}
