package at.kalauner.dezsys12.activities;

import android.content.Context;
import android.content.Intent;
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

import at.kalauner.dezsys12.R;
import at.kalauner.dezsys12.activities.listener.TextWatcherImpl;
import at.kalauner.dezsys12.connection.CustomRestClient;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private EditText mUserMessage;
    private TextView mMessages;
    private Button bSendMessage;
    private Context context;
    private SimpleDateFormat sdf1;
    private SimpleDateFormat sdf2;
    private volatile boolean polling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.context = this;

        Bundle b = getIntent().getExtras();
        String response = b.getString("response");

        if (response != null)
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();

        this.bSendMessage = (Button) findViewById(R.id.buttonSend);
        this.mUserMessage = (EditText) findViewById(R.id.userMessage);
        this.mMessages = (TextView) findViewById(R.id.labelMessages);

        mMessages.setMovementMethod(new ScrollingMovementMethod());
        this.mUserMessage.addTextChangedListener(new TextWatcherImpl(this.bSendMessage));

        this.sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.sdf2 = new SimpleDateFormat("HH:mm");
        this.startLongPoll();
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
                this.stopLongPoll();
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

    private void startLongPoll() {
        this.polling = true;
        this.checkForUpdates();
    }

    private void stopLongPoll() {
        this.polling = false;
        CustomRestClient.cancelRequests();
    }

    private void checkForUpdates() {
        CustomRestClient.get("message", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    receivedMessage(response.getString("sender"), response.getString("timestamp"), response.getString("content"));
                    if (polling)
                        checkForUpdates();
                } catch (JSONException e) {
                    Log.e(TAG, "Error while getting new message");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "Error: " + responseString, Toast.LENGTH_SHORT).show();
                if (polling)
                    checkForUpdates();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    Toast.makeText(context, "Error: " + errorResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    if (polling)
                        checkForUpdates();
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

    @Override
    public void onBackPressed() {
        // Disable back button
    }
}
