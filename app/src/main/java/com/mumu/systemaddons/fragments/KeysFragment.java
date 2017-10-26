package com.mumu.systemaddons.fragments;

import android.app.LauncherActivity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

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

public class KeysFragment extends PreferenceFragment implements PreferenceManager.OnPreferenceTreeClickListener{
    private String PREF_KEY_HIDE_ICON;
    private String PREF_KEY_ENABLE_SERVICE;
    private String PREF_KEY_ENABLE_SCREEN_ON;
    private String PREF_KEY_ENABLE_MEDIA_NOT_PLAYING;
    private String PREF_KEY_ENABLE_DEBUG;

    private boolean mEnableService, mEnableScreenOn, mEnableMediaNotPlaying, mEnableDebug;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.keys_setting);

        PREF_KEY_HIDE_ICON = getString(R.string.pref_hideIcon);
        PREF_KEY_ENABLE_SERVICE = getString(R.string.pref_service);
        PREF_KEY_ENABLE_SCREEN_ON = getString(R.string.pref_enable_screen_on);
        PREF_KEY_ENABLE_MEDIA_NOT_PLAYING = getString(R.string.pref_enable_media_not_playing);
        PREF_KEY_ENABLE_DEBUG = getString(R.string.pref_enable_debug);

        init();
    }

    @Override // Set for a SwitchPreference so value is always a boolean
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (PREF_KEY_HIDE_ICON.equals(preference.getKey())) {
            boolean checked = ((SwitchPreference) preference).isChecked();
            PackageManager manager = getContext().getPackageManager();
            ComponentName componentName = new ComponentName(getContext(), getActivity().getClass());
            int invisible = checked ? COMPONENT_ENABLED_STATE_DISABLED_USER : COMPONENT_ENABLED_STATE_DEFAULT;
            manager.setComponentEnabledSetting(componentName, invisible, DONT_KILL_APP);
            return true;
        } else if (PREF_KEY_ENABLE_SERVICE.equals(preference.getKey())) {
            mEnableService = ((SwitchPreference) preference).isChecked();
            findPreference(PREF_KEY_ENABLE_SCREEN_ON).setEnabled(mEnableService);
            findPreference(PREF_KEY_ENABLE_MEDIA_NOT_PLAYING).setEnabled(mEnableService);
            if (mEnableService) {
                android.provider.Settings.System.putInt(getContext().getContentResolver(),"volume_long_press_enable",mEnableService?1:0);
            }
        } else if (PREF_KEY_ENABLE_SCREEN_ON.equals(preference.getKey())) {
            mEnableScreenOn = ((SwitchPreference) preference).isChecked();
        } else if (PREF_KEY_ENABLE_MEDIA_NOT_PLAYING.equals(preference.getKey())) {
            mEnableMediaNotPlaying = ((SwitchPreference) preference).isChecked();
        } else if (PREF_KEY_ENABLE_DEBUG.equals(preference.getKey())) {
            mEnableDebug = ((SwitchPreference) preference).isChecked();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void init() {
        mEnableService = ((SwitchPreference) findPreference(PREF_KEY_ENABLE_SERVICE)).isChecked();
        SwitchPreference screen = (SwitchPreference) findPreference(PREF_KEY_ENABLE_SCREEN_ON);
        mEnableScreenOn = screen.isChecked();
        screen.setEnabled(mEnableService);
        SwitchPreference media = (SwitchPreference) findPreference(PREF_KEY_ENABLE_MEDIA_NOT_PLAYING);
        mEnableMediaNotPlaying = media.isChecked();
        media.setEnabled(mEnableService);
    }

}
