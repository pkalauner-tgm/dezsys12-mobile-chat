package at.kalauner.dezsys12.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import at.kalauner.dezsys12.R;
import at.kalauner.dezsys12.activities.listener.MessageListener;
import at.kalauner.dezsys12.activities.listener.TextWatcherImpl;
import at.kalauner.dezsys12.connection.CustomRestClient;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * ChatActivity
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160228.1
 */
public class ChatActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "ChatActivity";
    private EditText mUserMessage;
    private TextView mMessages;
    private Context context;
    private SimpleDateFormat sdf1;
    private SimpleDateFormat sdf2;
    private MessageListener messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.context = this;

        Bundle b = getIntent().getExtras();
        String response = b.getString("response");

        if (response != null)
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();

        Button bSendMessage = (Button) findViewById(R.id.buttonSend);
        this.mUserMessage = (EditText) findViewById(R.id.userMessage);
        this.mMessages = (TextView) findViewById(R.id.labelMessages);

        mMessages.setMovementMethod(new ScrollingMovementMethod());
        this.mUserMessage.addTextChangedListener(new TextWatcherImpl(bSendMessage));

        // Used for parsing timestamps of messages
        this.sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.sdf2 = new SimpleDateFormat("HH:mm");

        // Init MessageListener
        this.messageListener = new MessageListener();
        this.messageListener.addObserver(this);
        this.messageListener.startLongPoll();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                this.messageListener.stopLongPoll();
                CustomRestClient.post("logout", null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(context, "Good bye!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(context, "Logging out failed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                    }
                });

                break;
        }
        return true;
    }

    public void bSendClicked(View v) {
        String message = String.valueOf(mUserMessage.getText());
        mUserMessage.setText("");

        this.receivedMessage(getString(R.string.me), new Timestamp(System.currentTimeMillis()).toString(), message);

        JSONObject params = new JSONObject();
        StringEntity entity;
        try {
            params.put("content", message);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            Log.e(TAG, "Exception occurred", e);
            return;
        }

        CustomRestClient.postJson(this, "message", entity, new JsonHttpResponseHandler() {
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

    private void receivedMessage(String sender, String timestamp, String content) {
        try {
            Date date = sdf1.parse(timestamp);
            if (!this.mMessages.getText().toString().isEmpty())
                this.mMessages.append("\n");
            this.mMessages.append(sender + " <" + sdf2.format(date) + "> " + content.trim());
        } catch (ParseException e) {
            Log.e(TAG, "Error while parsing date", e);
        }
    }

    private void receivedMessage(JSONObject object) {
        try {
            this.receivedMessage(object.getString("sender"), object.getString("timestamp"), object.getString("content"));
        } catch (JSONException e) {
            Log.e(TAG, "Could not parse message");
        }
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof JSONObject) {
            receivedMessage((JSONObject) data);
        } else {
            Log.e(TAG, "Not a JSONObject");
        }
    }
}
