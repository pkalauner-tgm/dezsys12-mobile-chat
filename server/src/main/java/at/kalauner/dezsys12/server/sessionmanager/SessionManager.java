package at.kalauner.dezsys12.server.sessionmanager;

import at.kalauner.dezsys12.server.db.User;

import java.util.UUID;

/**
 * Manages the sessions
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160226.1
 */
public interface SessionManager {
    /**
     * Creates a new session for the given user
     *
     * @param user User
     * @return the session ID
     */
    UUID newSession(User user);

    /**
     * Destroys the given session
     *
     * @param uuid the session id of the session which should be destroyed
     */
    void logout(UUID uuid);

    /**
     * Returns the user with the given session ID
     *
     * @param uuid session ID
     * @return user with the given session ID
     */
    User getUser(UUID uuid);
}
