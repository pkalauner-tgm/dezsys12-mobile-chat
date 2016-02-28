package at.kalauner.dezsys12.server.chat;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.*;

@Repository
public class DefaultChatRepository implements ChatRepository {

    private final Map<String, List<Message>> messagesMap = new HashMap<>();

    public List<Message> getMessages(String chatroomId, int index) {
        List<Message> messages = messagesMap.get(chatroomId);
        cleanUp(messages);

        if (messages == null || messages.isEmpty()) {
            return Collections.<Message>emptyList();
        }
        Assert.isTrue((index >= 0) && (index <= messages.size()), "Invalid message index");
        return messages.subList(index, messages.size());
    }

    public void addMessage(Message message) {
        List<Message> messages = messagesMap.get(message.getChatRoomId());
        if (messages == null) {
            List<Message> newList = new ArrayList<>();
            messagesMap.put(message.getChatRoomId(), newList);
            messages = newList;
        }

        messages.add(message);
        message.setId(messages.size() - 1);
    }

    private static void cleanUp(List<Message> messages) {
        if (messages.size() > 200)
            messages.clear();
    }

    @Override
    public int getLatestId() {
        return this.messagesMap.size() - 1;
    }
}