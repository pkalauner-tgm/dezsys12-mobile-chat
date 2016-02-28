package at.kalauner.dezsys12.activities.listener;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
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
    private int lastMessageId;

    public void startLongPoll() {
        this.polling = true;
        this.lastMessageId = -1;
        this.checkForUpdates();
    }

    public void stopLongPoll() {
        this.polling = false;
        CustomRestClient.cancelRequests();
    }

    private void checkForUpdates() {
        CustomRestClient.get("message/" + (lastMessageId >= 0 ? lastMessageId : ""), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    lastMessageId = response.getInt("id");
                } catch (JSONException e) {
                    Log.e("JSONException", "Could not get message id", e);
                }
                setChanged();
                notifyObservers(response);
                if (polling)
                    checkForUpdates();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    lastMessageId = response.getJSONObject(response.length() - 1).getInt("id");
                } catch (JSONException e) {
                    Log.e("JSONException", "Could not get message id", e);
                }
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