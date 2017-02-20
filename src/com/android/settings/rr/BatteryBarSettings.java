/*Copyright (C) 2015 The ResurrectionRemix Project
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/
package com.android.settings.rr;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import java.util.ArrayList;
import java.util.List;


import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;

public class BatteryBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {


    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";
    private static final String PREF_BATT_AMBIENT = "show_batterybar_ambient";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String PREF_BATT_BAR_CHARGING_COLOR = "battery_bar_charging_color";
    private static final String STATUS_BAR_BATTERY_LOW_COLOR = "battery_bar_battery_low_color";
    private static final String PREF_BATT_USE_CHARGING_COLOR = "battery_bar_enable_charging_color";
    private static final String PREF_BATT_BLEND_COLORS = "battery_bar_blend_color";
    private static final String PREF_BATT_BLEND_COLORS_REVERSE = "battery_bar_blend_color_reverse";

    private ListPreference mBatteryBar;
    private ListPreference mBatteryBarStyle;
    private SwitchPreference mBatteryBarAmbient;
    private SeekBarPreference mBatteryBarThickness;
    private SwitchPreference mBatteryBarChargingAnimation;
    private ColorPickerPreference mBatteryBarColor;
    private ColorPickerPreference mBatteryBarChargingColor;
    private ColorPickerPreference mBatteryBarBatteryLowColor;
    private SwitchPreference mBatteryBarUseChargingColor;
    private SwitchPreference mBatteryBarBlendColors;
    private SwitchPreference mBatteryBarBlendColorsReverse;

    static final int DEFAULT = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rr_battery_bar);

        final ContentResolver resolver = getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();
        int intColor;
        String hexColor;

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        mBatteryBar.setOnPreferenceChangeListener(this);
        mBatteryBar.setValue((Settings.System.getInt(resolver,
                        Settings.System.STATUSBAR_BATTERY_BAR, 0)) + "");
        mBatteryBar.setSummary(mBatteryBar.getEntry());

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarStyle.setOnPreferenceChangeListener(this);
        mBatteryBarStyle.setValue((Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0)) + "");
        mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntry());

        mBatteryBarChargingAnimation = (SwitchPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarAmbient = (SwitchPreference) findPreference(PREF_BATT_AMBIENT);
        mBatteryBarAmbient.setChecked(Settings.System.getInt(resolver,
                Settings.System.SHOW_BATTERYBAR_AMBIENT, 0) == 1);

        mBatteryBarThickness = (SeekBarPreference) findPreference(PREF_BATT_BAR_WIDTH);
        int thick = (Settings.System.getInt(resolver,
		Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1));
        mBatteryBarThickness.setValue(thick/1);
        mBatteryBarThickness.setOnPreferenceChangeListener(this);

        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        mBatteryBarColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarColor.setNewPreviewColor(intColor);
        mBatteryBarColor.setSummary(hexColor);

        mBatteryBarChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_CHARGING_COLOR);
        mBatteryBarChargingColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarChargingColor.setNewPreviewColor(intColor);
        mBatteryBarChargingColor.setSummary(hexColor);

        mBatteryBarBatteryLowColor =
                (ColorPickerPreference) findPreference(STATUS_BAR_BATTERY_LOW_COLOR);
        mBatteryBarBatteryLowColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarBatteryLowColor.setNewPreviewColor(intColor);
        mBatteryBarBatteryLowColor.setSummary(hexColor);

        mBatteryBarUseChargingColor = (SwitchPreference) findPreference(PREF_BATT_USE_CHARGING_COLOR);
        mBatteryBarBlendColors = (SwitchPreference) findPreference(PREF_BATT_BLEND_COLORS);
        mBatteryBarBlendColorsReverse = (SwitchPreference) findPreference(PREF_BATT_BLEND_COLORS_REVERSE);

       updateBatteryBarOptions();

    }


   public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;
        int intValue;
        int index;
        String hex;
        int intHex;

        if (preference == mBatteryBar) {
            intValue = Integer.valueOf((String) newValue);
            index = mBatteryBar.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR, intValue);
            mBatteryBar.setSummary(mBatteryBar.getEntries()[index]);
            updateBatteryBarOptions();
            return true;
        } else if (preference == mBatteryBarStyle) {
            intValue = Integer.valueOf((String) newValue);
            index = mBatteryBarStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, intValue);
            mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarThickness) {
            int val = (Integer) newValue;
            return Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);
       }  else if (preference == mBatteryBarChargingColor) {
            hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, intHex);
            return true;
        } else if (preference == mBatteryBarBatteryLowColor) {
            hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR, intHex);
            return true;
       } else if (preference == mBatteryBarColor) {
            hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
            return true;
         }
	return false;
	}

   @Override
   public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;

        if (preference == mBatteryBarChargingAnimation) {
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE,
                    ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarAmbient) {
            Settings.System.putInt(resolver,
                    Settings.System.SHOW_BATTERYBAR_AMBIENT,
                    ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        }
        return false;
    }

        private void updateBatteryBarOptions() {
            if (Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR, 0) == 0) {
                mBatteryBarStyle.setEnabled(false);
                mBatteryBarThickness.setEnabled(false);
                mBatteryBarChargingAnimation.setEnabled(false);
                mBatteryBarAmbient.setEnabled(false);
                mBatteryBarColor.setEnabled(false);
                mBatteryBarChargingColor.setEnabled(false);
                mBatteryBarBatteryLowColor.setEnabled(false);
                mBatteryBarUseChargingColor.setEnabled(false);
                mBatteryBarBlendColors.setEnabled(false);
                mBatteryBarBlendColorsReverse.setEnabled(false);
            } else {
                mBatteryBarStyle.setEnabled(true);
                mBatteryBarThickness.setEnabled(true);
                mBatteryBarChargingAnimation.setEnabled(true);
                mBatteryBarAmbient.setEnabled(true);
                mBatteryBarColor.setEnabled(true);
                mBatteryBarChargingColor.setEnabled(true);
                mBatteryBarBatteryLowColor.setEnabled(true);
                mBatteryBarUseChargingColor.setEnabled(true);
                mBatteryBarBlendColors.setEnabled(true);
                mBatteryBarBlendColorsReverse.setEnabled(true);
            }
     }

     @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset_alpha)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.battery_colors_reset_title);
        alertDialog.setMessage(R.string.battery_colors_reset_message);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetValues();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    private void resetValues() {
	ContentResolver resolver = getActivity().getContentResolver();
        Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_COLOR, DEFAULT);
        mBatteryBarColor.setNewPreviewColor(DEFAULT);
        mBatteryBarColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, DEFAULT);
        mBatteryBarChargingColor.setNewPreviewColor(DEFAULT);
        mBatteryBarChargingColor.setSummary(R.string.default_string);
        Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR, DEFAULT);
        mBatteryBarBatteryLowColor.setNewPreviewColor(DEFAULT);
        mBatteryBarBatteryLowColor.setSummary(R.string.default_string);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.APPLICATION;
    }
}
