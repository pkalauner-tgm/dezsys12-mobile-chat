package at.kalauner.dezsys12.server.chat;

import at.kalauner.dezsys12.server.Util;
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
import java.util.UUID;
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
@Path("/message")
@Produces({MediaType.APPLICATION_JSON})
public class MessageEndpoint {

    final static Map<String, AsyncResponse> waiters = new ConcurrentHashMap<>();
    final static ExecutorService ex = Executors.newSingleThreadExecutor();

    private String chatroomid = "Defaut"; //TODO

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ChatRepository chatRepository;

    /**
     * Request by the client, the connection is kept alive until a new message was posted
     *
     * @param asyncResp AsyncResponse
     * @param headers   HttpHeaders
     * @return Response
     */
    @GET
    public Response hangUp(@Suspended AsyncResponse asyncResp, @Context HttpHeaders headers) {
        String uuid = headers.getCookies().get("sid").getValue();
        User user = sessionManager.getUser(uuid);

        if (user == null)
            return Util.getResponse(Response.Status.BAD_REQUEST, "No open session with this ID. Try reconnecting to the chat.");

        waiters.put(uuid, asyncResp);
        return Response.ok().build();
    }

    /**
     * Receives all messages since the given message
     *
     * @param asyncResp AsyncResponse
     * @param headers   HttpHeaders
     * @param id        last message id
     * @return Response
     */
    @GET
    @Path("/{id}")
    public Response hangUp(@Suspended AsyncResponse asyncResp, @Context HttpHeaders headers, @PathParam("id") int id) {
        String uuid = headers.getCookies().get("sid").getValue();
        User user = sessionManager.getUser(uuid);

        if (user == null)
            return Util.getResponse(Response.Status.BAD_REQUEST, "No open session with this ID. Try reconnecting to the chat.");

        waiters.put(uuid, asyncResp);

        List<Message> messages = this.chatRepository.getMessages(chatroomid, id + 1);

        if (!messages.isEmpty()) {
            asyncResp.resume(messages);
        }
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
    public Response sendMessage(@Context HttpHeaders headers, Message sentMessage) {
        String uuid = headers.getCookies().get("sid").getValue();
        User user = sessionManager.getUser(uuid);

        if (user == null)
            return Util.getResponse(Response.Status.BAD_REQUEST, "No open session with this ID. Try reconnecting to the chat.");

        String messageContent = sentMessage.getContent().trim();
        if (messageContent.isEmpty())
            return Util.getResponse(Response.Status.BAD_REQUEST, "Empty message");

        Message message = new Message(user.getName(), messageContent);
        message.setChatRoomId(chatroomid);
        this.chatRepository.addMessage(message);

        ex.submit((Runnable) () -> {
            Set<String> sids = waiters.keySet();
            sids.forEach(cur -> waiters.get(cur).resume(message));
        });

        return Util.getResponse(Response.Status.OK, "Message sent");
    }
}