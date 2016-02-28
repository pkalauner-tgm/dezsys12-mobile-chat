package at.kalauner.dezsys12.activities.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

/**
 * Responsible for reenabling the sign in or register button when text was changed
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160219.1
 */
public class TextWatcherImpl implements TextWatcher {

    private Button button;

    /**
     * Initializes the TextWatcher
     *
     * @param button the button which should be reenabled
     */
    public TextWatcherImpl(Button button) {
        this.button = button;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!this.button.isEnabled() && !s.toString().trim().isEmpty())
            this.button.setEnabled(true);
        else if (this.button.isEnabled() && s.toString().trim().isEmpty())
            this.button.setEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
