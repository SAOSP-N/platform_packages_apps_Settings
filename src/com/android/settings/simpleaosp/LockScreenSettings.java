package com.android.settings.simpleaosp;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import com.android.settings.Utils;
import com.android.settings.cyanogenmod.SystemSettingSwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.List;
import java.util.ArrayList;

import com.android.internal.logging.MetricsProto.MetricsEvent;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener, Indexable {

    private static final String KEYGUARD_TORCH = "keyguard_toggle_torch";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String LS_SECURE_CAT = "fingerprint_category";
    private static final String FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";
    private static final String FP_MAX_FAILED_ATTEMPTS = "fp_max_failed_attempts";
    private ListPreference maxFailedAttempts;

    private SystemSettingSwitchPreference mLsTorch;
    private SystemSettingSwitchPreference mFingerprintVib;
    private SystemSettingSwitchPreference mFpKeystore;
    private FingerprintManager mFingerprintManager;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();

        PreferenceCategory lockscreenCategory = (PreferenceCategory) findPreference(LS_SECURE_CAT);

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SystemSettingSwitchPreference) findPreference(FINGERPRINT_VIB);
        mFpKeystore = (SystemSettingSwitchPreference) findPreference(FP_UNLOCK_KEYSTORE);
        if (!mFingerprintManager.isHardwareDetected()){
            prefScreen.removePreference(lockscreenCategory);
            lockscreenCategory.removePreference(mFingerprintVib);
            lockscreenCategory.removePreference(mFpKeystore);
        }

        mLsTorch = (SystemSettingSwitchPreference) findPreference(KEYGUARD_TORCH);
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            getPreferenceScreen().removePreference(mLsTorch);
        }

        // fp max failed attempts
        maxFailedAttempts = (ListPreference) findPreference(FP_MAX_FAILED_ATTEMPTS);
        int set = Settings.System.getIntForUser(resolver,
                Settings.System.FP_MAX_FAILED_ATTEMPTS, 5, UserHandle.USER_CURRENT);
        maxFailedAttempts.setValue(String.valueOf(set));
        maxFailedAttempts.setSummary(maxFailedAttempts.getEntry());
        maxFailedAttempts.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
         if (preference == maxFailedAttempts) {
            int set = Integer.valueOf((String) objValue);
            int index = maxFailedAttempts.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.FP_MAX_FAILED_ATTEMPTS, set, UserHandle.USER_CURRENT);
            maxFailedAttempts.setSummary(maxFailedAttempts.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SAOSP_TWEAKS;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.lock_screen_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };
}

