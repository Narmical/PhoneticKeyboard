package com.hoardingsinc.phoneticskeyboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_open_settings:
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                break;
            case R.id.button_open_keyboard_selection:
                InputMethodManager inputManager = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                inputManager.showInputMethodPicker();
                break;
            case R.id.button_pronunroid_ad:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.hoardingsinc.pronunroid")));
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        if (!checked) {
            return;
        }

        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);

        switch (view.getId()) {
            case R.id.radio_normal_layout:
                keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_NORMAL);
                break;
            case R.id.radio_extended_layout:
                keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_EXTENDED);
                break;
            case R.id.radio_extended2_layout:
                keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_NUM_SYMB);
                break;
            case R.id.radio_compact_layout:
                keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_COMPACT);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView presentation = findViewById(R.id.presentation);
        presentation.setText(Html.fromHtml(getString(R.string.presentation)));
        presentation.setMovementMethod(LinkMovementMethod.getInstance());

        TextView pronunroidAdd = findViewById(R.id.pronunroid_ad);
        pronunroidAdd.setText(Html.fromHtml(getString(R.string.pronunroid_ad)));
        pronunroidAdd.setMovementMethod(LinkMovementMethod.getInstance());

        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);

        RadioGroup radioGroup = findViewById(R.id.radio_group_layout);
        switch (keyboardPreferences.getLayout()) {
            case KeyboardPreferences.LAYOUT_NORMAL:
                radioGroup.check(R.id.radio_normal_layout);
                break;
            case KeyboardPreferences.LAYOUT_EXTENDED:
                radioGroup.check(R.id.radio_extended_layout);
                break;
            case KeyboardPreferences.LAYOUT_NUM_SYMB:
                radioGroup.check(R.id.radio_extended2_layout);
                break;
            case KeyboardPreferences.LAYOUT_COMPACT:
                radioGroup.check(R.id.radio_compact_layout);
                break;
        }

        Button b = findViewById(R.id.button_open_settings);
        b.setOnClickListener(this);
        b = findViewById(R.id.button_open_keyboard_selection);
        b.setOnClickListener(this);
        b = findViewById(R.id.button_pronunroid_ad);
        b.setOnClickListener(this);
    }
}
