package com.sahil.gupte.sleepyboi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.sahil.gupte.sleepyboi.R;
import com.sahil.gupte.sleepyboi.Utils.ThemeUtils;

import java.util.Objects;

import static java.security.AccessController.getContext;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this, getApplicationContext());
        setContentView(R.layout.settings_activity);
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, settingsFragment)
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        final Boolean[] dark = new Boolean[1];
        Object accent;
        static SwitchPreference dark_mode;
        static ListPreference red;

        private final String TAG = "SettingsFragment";

        private void setTheme() {
            if (accent.equals("Blue")) {
                if (dark[0]) {
                    ThemeUtils.changeToTheme(ThemeUtils.BLUEDARK, Objects.requireNonNull(getContext()));
                } else {
                    ThemeUtils.changeToTheme(ThemeUtils.BLUE, Objects.requireNonNull(getContext()));
                }

            } else if (accent.equals("Orange")) {
                if (dark[0]) {
                    ThemeUtils.changeToTheme(ThemeUtils.ORANGEDARK, Objects.requireNonNull(getContext()));
                } else {
                    ThemeUtils.changeToTheme(ThemeUtils.ORANGE, Objects.requireNonNull(getContext()));
                }

            } else if (accent.equals("Yellow")) {
                if (dark[0]) {
                    ThemeUtils.changeToTheme(ThemeUtils.YELLOWDARK, Objects.requireNonNull(getContext()));
                } else {
                    ThemeUtils.changeToTheme(ThemeUtils.YELLOW, Objects.requireNonNull(getContext()));
                }

            } else if (accent.equals("Green")) {
                if (dark[0]) {
                    ThemeUtils.changeToTheme(ThemeUtils.GREENDARK, Objects.requireNonNull(getContext()));
                } else {
                    ThemeUtils.changeToTheme(ThemeUtils.GREEN, Objects.requireNonNull(getContext()));
                }

            } else if (accent.equals("Cyan")) {
                if (dark[0]) {
                    ThemeUtils.changeToTheme(ThemeUtils.CYANDARK, Objects.requireNonNull(getContext()));
                } else {
                    ThemeUtils.changeToTheme(ThemeUtils.CYAN, Objects.requireNonNull(getContext()));
                }

            } else if (accent.equals("Pink")) {
                if (dark[0]) {
                    ThemeUtils.changeToTheme(ThemeUtils.PINKDARK, Objects.requireNonNull(getContext()));
                } else {
                    ThemeUtils.changeToTheme(ThemeUtils.PINK, Objects.requireNonNull(getContext()));
                }
            } else {
                if (dark[0]) {
                    ThemeUtils.changeToTheme(ThemeUtils.CYANDARK, Objects.requireNonNull(getContext()));
                } else {
                    ThemeUtils.changeToTheme(ThemeUtils.CYAN, Objects.requireNonNull(getContext()));
                }
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            dark_mode = findPreference("dark_mode");
            red = findPreference("accent");
            assert dark_mode != null;
            dark[0] = dark_mode.isChecked();
            ThemeUtils.PutDark(dark[0], Objects.requireNonNull(getContext()));
            assert red != null;
            accent = red.getValue();
            dark_mode.setOnPreferenceChangeListener((preference, newValue) -> {
                dark[0] = Boolean.parseBoolean(newValue.toString());
                ThemeUtils.PutDark(dark[0], Objects.requireNonNull(getContext()));
                setTheme();
                return true;
            });

            red.setOnPreferenceChangeListener((preference, newValue) -> {
                accent = newValue;
                setTheme();
                return true;
            });
        }
    }
}