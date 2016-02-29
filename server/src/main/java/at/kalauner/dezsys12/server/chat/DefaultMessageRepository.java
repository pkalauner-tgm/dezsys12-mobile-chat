package at.kalauner.dezsys12.server.chat;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * In-memory ChatRepository
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160229.1
 */
@Repository
public class DefaultMessageRepository implements MessageRepository {

    private Cache<String, List<Message>> messagesMap;


    public DefaultMessageRepository() {
        messagesMap = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    }


    public List<Message> getMessages(String chatroomId, int index) {
        List<Message> messages = messagesMap.getIfPresent(chatroomId);
        cleanUp(messages);

        if (messages == null || messages.isEmpty() || index < 0 || index > messages.size()) {
            return Collections.<Message>emptyList();
        }
        return messages.subList(index, messages.size());
    }

    public void addMessage(Message message) {
        List<Message> messages = messagesMap.getIfPresent(message.getChatRoomId());
        if (messages == null) {
            List<Message> newList = new ArrayList<>();
            messagesMap.put(message.getChatRoomId(), newList);
            messages = newList;
        }

        messages.add(message);
        message.setId(messages.size() - 1);
    }

    private static void cleanUp(List<Message> messages) {
        if (messages != null && messages.size() > 200)
            messages.clear();
    }
}