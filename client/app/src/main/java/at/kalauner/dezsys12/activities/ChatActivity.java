package at.kalauner.dezsys12.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import at.kalauner.dezsys12.R;
import at.kalauner.dezsys12.activities.listener.MessageHandler;
import at.kalauner.dezsys12.activities.listener.TextWatcherImpl;
import at.kalauner.dezsys12.connection.CustomRestClient;
import cz.msebera.android.httpclient.Header;

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
    private MessageHandler messageHandler;

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
        this.messageHandler = new MessageHandler(this);
        this.messageHandler.addObserver(this);
        this.messageHandler.startLongPoll();
        this.setActionBarTitle(this.messageHandler.getChatroomId());
        mMessages.setText(getString(R.string.welcome_to_chatroom) + " " + messageHandler.getChatroomId() + "!");
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
            case R.id.switch_chatroom:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.switch_chatroom)).setMessage(R.string.switch_chatroom_message);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = input.getText().toString().toLowerCase();
                        if (str.isEmpty())
                            str = MessageHandler.DEFAULT_CHATROOM;

                        mMessages.setText("");
                        messageHandler.stopLongPoll();
                        messageHandler.setChatroomId(str.substring(0, 1).toUpperCase() + str.substring(1));
                        setActionBarTitle(messageHandler.getChatroomId());
                        messageHandler.startLongPoll();
                        mMessages.setText(getString(R.string.welcome_to_chatroom) + " " + messageHandler.getChatroomId() + "!");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;

            case R.id.logout:
                item.setEnabled(false);
                this.messageHandler.stopLongPoll();
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
        this.messageHandler.sendMessage(message);
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

    private void receivedMessages(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                this.receivedMessage(object.getString("sender"), object.getString("timestamp"), object.getString("content"));
            } catch (JSONException e) {
                Log.e(TAG, "Could not parse message");
            }
        }
    }

    private void setActionBarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof JSONObject) {
            receivedMessage((JSONObject) data);
        } else if (data instanceof JSONArray) {
            receivedMessages((JSONArray) data);
        } else
            Log.e(TAG, "Not a JSONObject or JSONArray");
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }
}
