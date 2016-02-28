package at.kalauner.dezsys12.server.chat;


import java.util.List;

public interface ChatRepository {

    List<Message> getMessages(String chatroomid, int messageIndex);

    void addMessage(Message message);

    int getLatestId();

}
