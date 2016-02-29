package at.kalauner.dezsys12.activities.listener;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Observable;

import at.kalauner.dezsys12.connection.CustomRestClient;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Listens for new messages and notifies the observers
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160228.1
 */
public class MessageHandler extends Observable {
    private static final String TAG = "MessageHandler";
    private static final String DEFAULT_CHATROOM = "Default";
    private Context context;
    private volatile boolean polling;
    private int lastMessageId;
    private String chatroomId;


    public MessageHandler(Context context) {
        this(context, DEFAULT_CHATROOM);
    }

    public MessageHandler(Context context, String chatroomId) {
        this.context = context;
        this.chatroomId = chatroomId;
    }

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

        RequestParams params = null;

        if (lastMessageId >= 0) {
            params = new RequestParams();
            params.put("messageindex", lastMessageId);
        }

        CustomRestClient.get("chat/" + chatroomId, params, new JsonHttpResponseHandler() {
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

    /**
     * Sends a message
     *
     * @param message the message which should be sent
     */
    public void sendMessage(String message) {
        JSONObject params = new JSONObject();
        StringEntity entity;
        try {
            params.put("content", message);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            Log.e(TAG, "Exception occurred", e);
            return;
        }

        CustomRestClient.postJson(context, "chat/" + this.chatroomId, entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Do nothing
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "Error: " + responseString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    Toast.makeText(context, "Error: " + errorResponse.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e(TAG, "Failed getting error message", e);
                }
            }
        });
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }
}