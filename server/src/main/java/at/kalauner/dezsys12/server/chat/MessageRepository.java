package at.kalauner.dezsys12.server.chat;


import java.util.List;

/**
 * Manages stored messages
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160229.1
 */
public interface MessageRepository {

    /**
     * Gets messages for the given chatroom, starting at the given messageindex
     *
     * @param chatroomid id of chatroom
     * @param messageIndex index of starting message
     * @return List of Messages
     */
    List<Message> getMessages(String chatroomid, int messageIndex);

    /**
     * Saves a message
     *
     * @param message the message which should be saved
     */
    void addMessage(Message message);
}
