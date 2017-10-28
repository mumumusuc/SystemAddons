package com.mumu.systemaddons.fragments;

import android.app.LauncherActivity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.mumu.systemaddons.R;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

/**
 * Created by leonardo on 17-10-26.
 */

public class KeysFragment extends PreferenceFragment implements PreferenceManager.OnPreferenceTreeClickListener {

    private static final int ADVANCE_REBOOT_ENABLE = 0x80;
    private static final int ADVANCE_REBOOT_BOOTLOADER = 0x01;
    private static final int ADVANCE_REBOOT_RECOVERY = 0x02;

    private String PREF_KEY_ENABLE_SERVICE;
    private String PREF_KEY_ENABLE_SCREEN_ON;
    private String PREF_KEY_ENABLE_MEDIA_NOT_PLAYING;
    private String PREF_KEY_ENABLE_DEBUG;
    private String PREF_KEY_ENABLE_ADVANCE_REBOOT;

    private boolean mEnableService, mEnableScreenOn, mEnableMediaNotPlaying, mEnableDebug;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.keys_setting);

        PREF_KEY_ENABLE_SERVICE = getString(R.string.pref_service);
        PREF_KEY_ENABLE_SCREEN_ON = getString(R.string.pref_enable_screen_on);
        PREF_KEY_ENABLE_MEDIA_NOT_PLAYING = getString(R.string.pref_enable_media_not_playing);
        PREF_KEY_ENABLE_DEBUG = getString(R.string.pref_enable_debug);
        PREF_KEY_ENABLE_ADVANCE_REBOOT = getString(R.string.pref_enable_advance_reboot);

        init();
    }

    @Override // Set for a SwitchPreference so value is always a boolean
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (PREF_KEY_ENABLE_SERVICE.equals(preference.getKey())) {
            mEnableService = ((SwitchPreference) preference).isChecked();
            findPreference(PREF_KEY_ENABLE_SCREEN_ON).setEnabled(mEnableService);
            findPreference(PREF_KEY_ENABLE_MEDIA_NOT_PLAYING).setEnabled(mEnableService);
            updateVolumeKeyState();
        } else if (PREF_KEY_ENABLE_SCREEN_ON.equals(preference.getKey())) {
            mEnableScreenOn = ((CheckBoxPreference) preference).isChecked();
            updateVolumeKeyState();
        } else if (PREF_KEY_ENABLE_MEDIA_NOT_PLAYING.equals(preference.getKey())) {
            mEnableMediaNotPlaying = ((CheckBoxPreference) preference).isChecked();
            updateVolumeKeyState();
        } else if (PREF_KEY_ENABLE_ADVANCE_REBOOT.equals(preference.getKey())) {
            boolean checked = ((SwitchPreference) preference).isChecked();
            Settings.System.putInt(
                    getContext().getContentResolver(),
                    "system_addons_keys_power",
                    checked ? (ADVANCE_REBOOT_ENABLE | ADVANCE_REBOOT_BOOTLOADER | ADVANCE_REBOOT_RECOVERY) : 0);
        } else if (PREF_KEY_ENABLE_DEBUG.equals(preference.getKey())) {
            mEnableDebug = ((SwitchPreference) preference).isChecked();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void init() {
        mEnableService = ((SwitchPreference) findPreference(PREF_KEY_ENABLE_SERVICE)).isChecked();
        CheckBoxPreference screen = (CheckBoxPreference) findPreference(PREF_KEY_ENABLE_SCREEN_ON);
        mEnableScreenOn = screen.isChecked();
        screen.setEnabled(mEnableService);
        CheckBoxPreference media = (CheckBoxPreference) findPreference(PREF_KEY_ENABLE_MEDIA_NOT_PLAYING);
        mEnableMediaNotPlaying = media.isChecked();
        media.setEnabled(mEnableService);
    }

    private void updateVolumeKeyState() {
        int config = android.provider.Settings.System.getInt(getContext().getContentResolver(), "system_addons_keys_volume", 0);
        synchronized (KeysFragment.class) {
            config = (config & 0x7f) | (mEnableService ? 0x80 : 0);
            config = (config & 0xfe) | (mEnableScreenOn ? 0x01 : 0);
            config = (config & 0xfd) | (mEnableMediaNotPlaying ? 0x02 : 0);
            android.provider.Settings.System.putInt(
                    getContext().getContentResolver(),
                    "system_addons_keys_volume",
                    config);
        }
    }

}
