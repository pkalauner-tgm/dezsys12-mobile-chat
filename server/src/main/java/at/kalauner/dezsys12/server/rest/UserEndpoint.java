package at.kalauner.dezsys12.server.rest;

import at.kalauner.dezsys12.server.Util;
import at.kalauner.dezsys12.server.db.User;
import at.kalauner.dezsys12.server.db.UserRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Mapping for path {@code /user}
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
@Named
@Path("/user")
@Produces({MediaType.APPLICATION_JSON})
public class UserEndpoint {

    @Inject
    private UserRepository userRepository;

    /**
     * Gets the user with the given id (email)
     *
     * @param id email of the user
     * @return response
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        User user = this.userRepository.findOne(id);

        if (user == null) {
            return Util.getResponse(Response.Status.BAD_REQUEST, "User not found for id: " + id);
        }
        return Response.ok(user, MediaType.APPLICATION_JSON).build();
    }
}