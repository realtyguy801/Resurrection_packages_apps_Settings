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

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;


import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import cyanogenmod.providers.CMSettings;

public class QsAdvanced extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "QsAdvanced";
    private static final String BATTERY_TILE_STYLE = "battery_tile_style";
    
	private ListPreference mBatteryTileStyle;
    private int mBatteryTileStyleValue;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.RESURRECTED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.rr_qs_advanced);

             mBatteryTileStyle = (ListPreference) findPreference(BATTERY_TILE_STYLE);
             mBatteryTileStyleValue = Settings.Secure.getInt(getActivity().getContentResolver(),
                     Settings.Secure.BATTERY_TILE_STYLE, 0);
             mBatteryTileStyle.setValue(Integer.toString(mBatteryTileStyleValue));
             mBatteryTileStyle.setSummary(mBatteryTileStyle.getEntry());
             mBatteryTileStyle.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) 		{
      if (preference == mBatteryTileStyle) {
            mBatteryTileStyleValue = Integer.valueOf((String) newValue);
            int index = mBatteryTileStyle.findIndexOfValue((String) newValue);
            mBatteryTileStyle.setSummary(
                    mBatteryTileStyle.getEntries()[index]);
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.BATTERY_TILE_STYLE, mBatteryTileStyleValue);
            return true;
           }
      return false;

    }
}
