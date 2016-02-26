package at.kalauner.dezsys12.server.rest;

import at.kalauner.dezsys12.server.Util;
import at.kalauner.dezsys12.server.db.User;
import at.kalauner.dezsys12.server.sessionmanager.SessionManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;


/**
 * Mapping for path {@code /status}
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
@Named
@Path("/status")
@Produces({MediaType.APPLICATION_JSON})
public class StatusEndpoint {

    @Inject
    private SessionManager sessionManager;

    /**
     * Gets the current status of the given session
     *
     * @param sid session id
     * @return Response
     */
    @GET
    @Path("/{sid}")
    public Response get(@PathParam("sid") String sid) {
        User user = this.sessionManager.getUser(UUID.fromString(sid));

        if (user == null)
            return Util.getResponse(Response.Status.OK, "No active session with this sid");
        else
            return Util.getResponse(Response.Status.OK, "Active session found");
    }
}