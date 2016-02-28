package at.kalauner.dezsys12.server.rest;

import at.kalauner.dezsys12.server.Util;
import at.kalauner.dezsys12.server.sessionmanager.SessionManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;


/**
 * Mapping for path {@code /logout}
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
@Named
@Path("/logout")
@Produces({MediaType.APPLICATION_JSON})
public class UserLogoutEndpoint {
    @Inject
    private SessionManager sessionManager;

    /**
     * Logout user
     *
     * @param headers HttpHeaders
     * @return response
     */
    @POST
    public Response post(@Context HttpHeaders headers) {
        UUID uuid;
        try {
            uuid = UUID.fromString(headers.getCookies().get("sid").getValue());
        } catch (NullPointerException | IllegalArgumentException ex) {
            return Util.getResponse(Response.Status.BAD_REQUEST, "Invalid session ID");
        }
        sessionManager.logout(uuid);
        return Response.ok().build();
    }
}