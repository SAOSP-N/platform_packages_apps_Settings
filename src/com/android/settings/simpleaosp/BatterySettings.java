/*
* Copyright (C) 2014 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.settings.simpleaosp;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import com.android.settings.cyanogenmod.SystemSettingSwitchPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.android.internal.logging.MetricsProto.MetricsEvent;

public class BatterySettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String TAG = "BatterySettings";

    private static final String STATUSBAR_BATTERY_STYLE = "statusbar_battery_style";
    private static final String STATUSBAR_BATTERY_PERCENT = "statusbar_battery_percent";
    private static final String STATUSBAR_CHARGING_COLOR = "statusbar_battery_charging_color";
    private static final String STATUSBAR_BATTERY_PERCENT_INSIDE = "statusbar_battery_percent_inside";
    private static final String STATUSBAR_BATTERY_SHOW_BOLT = "statusbar_battery_charging_image";
    private static final String STATUSBAR_BATTERY_ENABLE = "statusbar_battery_enable";
    private static final String STATUSBAR_SHOW_CHARGING = "statusbar_battery_charging_color_enable";
    private static final String STATUSBAR_CATEGORY_CHARGING = "statusbar_category_charging";

    private ListPreference mBatteryStyle;
    private ListPreference mBatteryPercent;
    private ColorPickerPreference mChargingColor;
    private SwitchPreference mPercentInside;
    private SwitchPreference mChargingShow;
    private SwitchPreference mShowBolt;
    private int mShowPercent;
    private int mBatteryStyleValue;
    private ListPreference mBatteryEnable;
    private int mShowBattery = 1;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.battery_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mBatteryStyle = (ListPreference) findPreference(STATUSBAR_BATTERY_STYLE);
        mBatteryStyleValue = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_STYLE, 0);

        mBatteryStyle.setValue(Integer.toString(mBatteryStyleValue));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercent = (ListPreference) findPreference(STATUSBAR_BATTERY_PERCENT);
        mShowPercent = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_PERCENT, 2);

        mBatteryPercent.setValue(Integer.toString(mShowPercent));
        mBatteryPercent.setSummary(mBatteryPercent.getEntry());
        mBatteryPercent.setOnPreferenceChangeListener(this);

        mChargingColor = (ColorPickerPreference) findPreference(STATUSBAR_CHARGING_COLOR);
        mChargingColor.setOnPreferenceChangeListener(this);

        mPercentInside = (SwitchPreference) findPreference(STATUSBAR_BATTERY_PERCENT_INSIDE);
        mChargingShow = (SwitchPreference) findPreference(STATUSBAR_SHOW_CHARGING);
        mShowBolt = (SwitchPreference) findPreference(STATUSBAR_BATTERY_SHOW_BOLT);

        mBatteryEnable = (ListPreference) findPreference(STATUSBAR_BATTERY_ENABLE);
        mShowBattery = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_ENABLE, 1);

        mBatteryEnable.setValue(Integer.toString(mShowBattery));
        mBatteryEnable.setSummary(mBatteryEnable.getEntry());
        mBatteryEnable.setOnPreferenceChangeListener(this);
        updateEnablement();
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SAOSP_TWEAKS;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryStyle) {
            mBatteryStyleValue = Integer.valueOf((String) newValue);
            int index = mBatteryStyle.findIndexOfValue((String) newValue);
            mBatteryStyle.setSummary(
                    mBatteryStyle.getEntries()[index]);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_STYLE, mBatteryStyleValue);
            updateEnablement();
            return true;
        } else if (preference == mBatteryPercent) {
            mShowPercent = Integer.valueOf((String) newValue);
            int index = mBatteryPercent.findIndexOfValue((String) newValue);
            mBatteryPercent.setSummary(
                    mBatteryPercent.getEntries()[index]);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_PERCENT, mShowPercent);
            updateEnablement();
            return true;
        } else if (preference == mChargingColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            mChargingColor.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_CHARGING_COLOR, intHex);
            return true;
        } else if (preference == mBatteryEnable) {
            mShowBattery = Integer.valueOf((String) newValue);
            int index = mBatteryEnable.findIndexOfValue((String) newValue);
            mBatteryEnable.setSummary(
                    mBatteryEnable.getEntries()[index]);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_ENABLE, mShowBattery);
            updateEnablement();
            return true;
        }
        return false;
    }

     private void updateEnablement() {
        mPercentInside.setEnabled(mShowBattery != 0 && mBatteryStyleValue < 3 && mShowPercent != 0);
        mShowBolt.setEnabled(mBatteryStyleValue < 3);
        mBatteryStyle.setEnabled(mShowBattery != 0);
        mBatteryPercent.setEnabled(mShowBattery != 0 && mBatteryStyleValue != 3);
        mChargingShow.setEnabled(mShowBattery != 0);
        //mChargingCategory.setEnabled(mShowBattery != 0);
    }
}

