package com.hoardingsinc.phoneticskeyboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.hoardingsinc.phoneticskeyboard.pronounceationdictionary.PronunciationDB;
import com.hoardingsinc.phoneticskeyboard.pronounceationdictionary.PronunciationDao;
import com.hoardingsinc.phoneticskeyboard.pronounceationdictionary.RoomPronunciationDictionary;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.ArpabetToIpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.CmuPronouncingDictionary;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.MobyPronunciator;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.MobyToIpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.RawDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Thread dictionaryBuilderThread;
    public ProgressBar progressBar;
    public TextView progressBarText;
    private boolean rebuild = false;

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
            case R.id.build_room_dict:
                Log.d("PhoneticsKeyboard", "Nuke Pronunciation Dictionary");


                this.rebuild = true;
                dictionaryBuilderThread = new Thread(this::buildDictionary);
                dictionaryBuilderThread.start();

                break;
        }

    }

    private int preCountDictSize(int resource) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        this.getResources().openRawResource(resource),
                        "UTF8"
                )
        );
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }


    private void buildDictionary() {
        PronunciationDB db = Room.databaseBuilder(this.getApplicationContext(), PronunciationDB.class,
                "pronunciation.db").build();
        PronunciationDao pronunciationDao = db.pronunciationDao();
        if (pronunciationDao.numEntries() == 0 || this.rebuild == true) {
            db.clearAllTables();
            runOnUiThread(() -> this.progressBar.setVisibility(View.VISIBLE));
            Log.d("PhoneticsKeyboard", "Nuked Pronunciation Dictionary");
            RoomPronunciationDictionary dictionary = new RoomPronunciationDictionary(this);
            try {

                int dictSize = this.preCountDictSize(R.raw.mpron) + this.preCountDictSize(R.raw.cmudict);


                List<RawDictionary> rawDictionaries = new ArrayList<>();
                rawDictionaries.add(
                        new MobyPronunciator(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.getResources().openRawResource(R.raw.mpron),
                                                "UTF8"
                                        )
                                ),
                                new MobyToIpaConverter(
                                        new BufferedReader(
                                                new InputStreamReader(
                                                        this.getResources().openRawResource(R.raw.mpront_to_ipa),
                                                        "UTF8"
                                                )

                                        )
                                )
                        )
                );
                rawDictionaries.add(
                        new CmuPronouncingDictionary(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.getResources().openRawResource(R.raw.cmudict),
                                                "UTF8"
                                        )
                                ),
                                new ArpabetToIpaConverter(
                                        new BufferedReader(
                                                new InputStreamReader(
                                                        this.getResources().openRawResource(R.raw.arpabet_to_ipa),
                                                        "UTF8"
                                                )

                                        )
                                )
                        )
                );
                dictionary.loadDictionary(rawDictionaries, this, dictSize);
            } catch (
                    UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }

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
            /*case R.id.radio_extended_layout:
                keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_DIPHTHONGS);
                break;*/
            case R.id.radio_extended2_layout:
                keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_LEGACY);
                break;
            case R.id.radio_compact_layout:
                keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_SHAVIAN);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.progressBar = findViewById(R.id.progress_bar);
        this.progressBar.setMax(100);
        progressBar.setVisibility(View.INVISIBLE);
        this.progressBarText = findViewById(R.id.progress_bar_text);


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
            /*case KeyboardPreferences.LAYOUT_DIPHTHONGS:
                radioGroup.check(R.id.radio_extended_layout);
                break;*/
            case KeyboardPreferences.LAYOUT_LEGACY:
                radioGroup.check(R.id.radio_extended2_layout);
                break;
            case KeyboardPreferences.LAYOUT_SHAVIAN:
                radioGroup.check(R.id.radio_compact_layout);
                break;
        }

        Button b = findViewById(R.id.button_open_settings);
        b.setOnClickListener(this);
        b = findViewById(R.id.button_open_keyboard_selection);
        b.setOnClickListener(this);
        b = findViewById(R.id.button_pronunroid_ad);
        b.setOnClickListener(this);
        b = findViewById(R.id.build_room_dict);
        b.setOnClickListener(this);
        this.rebuild = false;
        dictionaryBuilderThread = new Thread(this::buildDictionary);
        dictionaryBuilderThread.start();
    }
}
