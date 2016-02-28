package at.kalauner.dezsys12;

import android.app.Application;

/**
 * Created by Paul on 28.02.2016.
 */
public class Dezsys12Application extends Application {
    private String sessionID;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
