package at.kalauner.dezsys12.server.chat;

import javax.ws.rs.container.AsyncResponse;

/**
 * Handles the chatrooms
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160229.1
 */
public interface ChatroomHandler {

    /**
     * Receives all messages since the given message, or keeps the connection alive until a new message arrives
     *
     * @param chatroomid   id of the chatroom
     * @param uuid         sid
     * @param asyncResp    AsyncResponse
     * @param messageindex index of the last received message
     */
    void waitForMessage(String chatroomid, String uuid, AsyncResponse asyncResp, int messageindex);

    /**
     * Broadcasts the message
     *
     * @param message Message
     */
    void sendMessage(Message message);
}
