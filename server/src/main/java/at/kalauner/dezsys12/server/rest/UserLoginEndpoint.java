package at.kalauner.dezsys12.server.rest;

import at.kalauner.dezsys12.server.sessionmanager.SessionManager;
import at.kalauner.dezsys12.server.Util;
import at.kalauner.dezsys12.server.db.User;
import at.kalauner.dezsys12.server.db.UserRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Mapping for path {@code /login}
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
@Named
@Path("/login")
@Produces({MediaType.APPLICATION_JSON})
public class UserLoginEndpoint {

    @Inject
    private UserRepository userRepository;

    @Inject
    private SessionManager sessionManager;

    /**
     * Checks if login data is valid for the given user and creates the session
     *
     * @param user user
     * @return response
     */
    @POST
    public Response post(User user) {
        if (user.getEmail() == null)
            return Util.getResponse(Response.Status.BAD_REQUEST, "No email specified!");

        User userFromDb = userRepository.findOne(user.getEmail());
        if (userFromDb != null && userFromDb.getPassword().equals(user.getPassword())) {
            Map<String, String> response = new HashMap<>();
            // Create Session
            UUID uuid = sessionManager.newSession(userFromDb);
            response.put("sid", uuid.toString());
            response.put("code", String.valueOf(Response.Status.OK.getStatusCode()));
            response.put("message", "Welcome " + userFromDb.getName());
            return Response.status(Response.Status.OK).cookie(new NewCookie("sid", uuid.toString())).entity(response).build();
        } else {
            return Util.getResponse(Response.Status.FORBIDDEN, "Invalid account data!");
        }
    }
}