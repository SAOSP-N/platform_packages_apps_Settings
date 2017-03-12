package com.android.settings.simpleaosp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
 import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.List;
import java.util.ArrayList;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener, Indexable {

    private static final String KEY_HEADS_UP_SETTINGS = "heads_up_settings";
    private static final String KEY_CLOCK_SETTINGS = "statusbarclock";
    private static final String STATUS_BAR_SHOW_TICKER = "status_bar_show_ticker";

    private PreferenceScreen mHeadsUp;
    private PreferenceScreen mClock;
    private SwitchPreference mShowTicker;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();

        mHeadsUp = (PreferenceScreen) findPreference(KEY_HEADS_UP_SETTINGS);
        mClock = (PreferenceScreen) findPreference(KEY_CLOCK_SETTINGS);

        mShowTicker = (SwitchPreference) findPreference(STATUS_BAR_SHOW_TICKER);
        mShowTicker.setOnPreferenceChangeListener(this);
        int ShowTicker = Settings.System.getInt(getContentResolver(),
                STATUS_BAR_SHOW_TICKER, 0);
        mShowTicker.setChecked(ShowTicker != 0);
    }

    private boolean getUserHeadsUpState() {
         return Settings.System.getInt(getContentResolver(),
                Settings.System.HEADS_UP_USER_ENABLED,
                Settings.System.HEADS_UP_USER_ON) != 0;
    }

    private boolean getUserClockState() {
         return Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
		if (preference == mShowTicker) {
            boolean value = (Boolean) objValue;
            Settings.Global.putInt(getContentResolver(), STATUS_BAR_SHOW_TICKER,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SAOSP_TWEAKS;
    }

    @Override
    public void onResume() {
        super.onResume();

        mHeadsUp.setSummary(getUserHeadsUpState()
                ? R.string.summary_heads_up_enabled : R.string.summary_heads_up_disabled);
        mClock.setSummary(getUserClockState()
                ? R.string.summary_clock_enabled : R.string.summary_clock_disabled);
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.status_bar_settings;
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
