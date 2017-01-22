package com.android.settings.simpleaosp;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.settings.Utils;
import com.android.settings.cyanogenmod.SystemSettingSwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.MetricsProto.MetricsEvent;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEYGUARD_TORCH = "keyguard_toggle_torch";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String LS_SECURE_CAT = "lockscreen_settings_category";

    private SystemSettingSwitchPreference mLsTorch;
    private SystemSettingSwitchPreference mFingerprintVib;
    private FingerprintManager mFingerprintManager;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen_settings);

        PreferenceCategory lockscreenCategory = (PreferenceCategory) findPreference(LS_SECURE_CAT);

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SystemSettingSwitchPreference) findPreference(FINGERPRINT_VIB);
        if (!mFingerprintManager.isHardwareDetected()){
            lockscreenCategory.removePreference(mFingerprintVib);
        }

        mLsTorch = (SystemSettingSwitchPreference) findPreference(KEYGUARD_TORCH);
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            getPreferenceScreen().removePreference(mLsTorch);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SAOSP_TWEAKS;
    }
}

