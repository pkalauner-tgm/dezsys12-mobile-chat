package at.kalauner.dezsys12.server.rest;

import at.kalauner.dezsys12.server.Util;
import at.kalauner.dezsys12.server.chat.ChatroomHandler;
import at.kalauner.dezsys12.server.chat.Message;
import at.kalauner.dezsys12.server.db.User;
import at.kalauner.dezsys12.server.sessionmanager.SessionManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mapping for path {@code /message}
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160228.1
 */
@Named
@Path("/chat")
@Produces({MediaType.APPLICATION_JSON})
public class MessageEndpoint {
    private static final String DEFAULT_CHATROOM = "Default";

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ChatroomHandler chatroomHandler;

    /**
     * Receives all messages since the given message, or keeps the connection alive until a new message arrives
     *
     * @param asyncResp    AsyncResponse
     * @param headers      HttpHeaders
     * @param messageindex last message id
     * @return Response
     */
    @GET
    @Path("/{chatroomid}")
    public Response waitForMessage(@Suspended AsyncResponse asyncResp, @Context HttpHeaders headers, @DefaultValue("-2") @QueryParam("messageindex") int messageindex, @PathParam("chatroomid") String chatroomid) {
        String uuid = headers.getCookies().get("sid").getValue();
        User user = sessionManager.getUser(uuid);

        if (user == null)
            return Util.getResponse(Response.Status.BAD_REQUEST, "No open session with this ID. Try reconnecting to the chat.");

        this.chatroomHandler.waitForMessage(chatroomid != null ? chatroomid : DEFAULT_CHATROOM, uuid, asyncResp, messageindex);
        return Response.ok().build();
    }


    /**
     * A new message by the client
     *
     * @param headers     HttpHeaders
     * @param sentMessage received Message
     * @return Response
     */
    @POST
    @Path("/{chatroomid}")
    public Response sendMessage(@Context HttpHeaders headers, @PathParam("chatroomid") String chatroomid, Message sentMessage) {
        String uuid = headers.getCookies().get("sid").getValue();
        User user = sessionManager.getUser(uuid);

        if (user == null)
            return Util.getResponse(Response.Status.BAD_REQUEST, "No open session with this ID. Try reconnecting to the chat.");

        String messageContent = sentMessage.getContent().trim();
        if (messageContent.isEmpty())
            return Util.getResponse(Response.Status.BAD_REQUEST, "Empty message");

        Message message = new Message(chatroomid != null ? chatroomid : DEFAULT_CHATROOM, user.getName(), messageContent);
        this.chatroomHandler.sendMessage(message);
        return Util.getResponse(Response.Status.OK, "Message sent");
    }
}