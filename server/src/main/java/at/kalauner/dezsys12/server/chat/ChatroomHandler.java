package at.kalauner.dezsys12.server.chat;

import javax.ws.rs.container.AsyncResponse;

/**
 * Handles the chatrooms
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160229.1
 */
public interface ChatroomHandler {
    void waitForMessage(String chatroomid, String uuid, AsyncResponse asyncResp, int messageindex);

    void sendMessage(Message message);
}
