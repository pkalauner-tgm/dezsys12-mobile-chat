package at.kalauner.dezsys12.server.sessionmanager;

import at.kalauner.dezsys12.server.Util;
import at.kalauner.dezsys12.server.db.User;
import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * DefaultSessionManager
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160226.1
 */
@Singleton
@Primary
@Component
public class DefaultSessionManager implements SessionManager {
    private final Cache<UUID, User> cache;

    /**
     * Default constructor
     */
    public DefaultSessionManager() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    }

    @Override
    public UUID newSession(User user) {
        UUID uuid = UUID.randomUUID();
        cache.put(uuid, user);
        return uuid;
    }

    @Override
    public void logout(UUID uuid) {
        cache.invalidate(uuid);
    }

    @Override
    public User getUser(UUID uuid) {
        return cache.getIfPresent(uuid);
    }

    @Override
    public User getUser(String uuid) {
        try {
            return cache.getIfPresent(UUID.fromString(uuid));
        } catch (NullPointerException | IllegalArgumentException ex) {
            return null;
        }
    }
}
