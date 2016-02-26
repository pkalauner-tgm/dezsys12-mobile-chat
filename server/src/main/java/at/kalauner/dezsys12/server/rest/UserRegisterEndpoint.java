package at.kalauner.dezsys12.server.rest;

import at.kalauner.dezsys12.server.Util;
import at.kalauner.dezsys12.server.db.User;
import at.kalauner.dezsys12.server.db.UserRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.TransactionSystemException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Mapping for path {@code /register}
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
@Named
@Path("/register")
@Produces({MediaType.APPLICATION_JSON})
public class UserRegisterEndpoint {

    @Inject
    private UserRepository userRepository;

    /**
     * Registers a new user
     *
     * @param user the user which should be registered
     * @return response
     */
    @POST
    public Response post(User user) {
        try {
            if (!userRepository.exists(user.getEmail())) {
                User savedUser = this.userRepository.save(user);
                return Util.getResponse(Response.Status.CREATED, "User " + savedUser.getEmail() + " saved!");
            } else {
                return Util.getResponse(Response.Status.BAD_REQUEST, "User " + user.getEmail() + " already exists!");
            }
        } catch (TransactionSystemException | InvalidDataAccessApiUsageException ex) {
            return Util.getResponse(Response.Status.BAD_REQUEST, "Missing parameters!");
        }
    }
}