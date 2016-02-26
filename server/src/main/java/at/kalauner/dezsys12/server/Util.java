package at.kalauner.dezsys12.server;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 26.02.2016.
 */
public final class Util {
    public static Response getResponse(Response.Status status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("code", String.valueOf(status.getStatusCode()));
        map.put("message", message);
        return Response.status(status).entity(map).build();
    }
}
