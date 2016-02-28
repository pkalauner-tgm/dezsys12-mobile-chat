package at.kalauner.dezsys12.activities.listener;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Observable;

import at.kalauner.dezsys12.connection.CustomRestClient;
import cz.msebera.android.httpclient.Header;

/**
 * Listens for new messages and notifies the observers
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160228.1
 */
public class MessageListener extends Observable {
    private volatile boolean polling;

    public void startLongPoll() {
        this.polling = true;
        this.checkForUpdates();
    }

    public void stopLongPoll() {
        this.polling = false;
        CustomRestClient.cancelRequests();
    }

    private void checkForUpdates() {
        CustomRestClient.get("message", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setChanged();
                notifyObservers(response);
                if (polling)
                    checkForUpdates();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (polling)
                    checkForUpdates();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (polling)
                    checkForUpdates();
            }
        });
    }
}