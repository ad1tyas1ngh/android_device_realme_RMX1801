/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.realme.realmeparts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;

import com.realme.realmeparts.kcal.KCalSettingsActivity;
import com.realme.realmeparts.Constants;
import com.realme.realmeparts.preferences.SecureSettingCustomSeekBarPreference;

public class RealmeParts extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String PREF_DEVICE_KCAL = "kcal";

    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_OTG_SWITCH = "otg";

    final static String PREF_HEADPHONE_GAIN = "headphone_gain";
    private static final String HEADPHONE_GAIN_PATH = "/sys/kernel/sound_control/headphone_gain";
    final static String PREF_MICROPHONE_GAIN = "microphone_gain";
    private static final String MICROPHONE_GAIN_PATH = "/sys/kernel/sound_control/mic_gain";

    private Preference mKcal;
    private VibratorStrengthPreference mVibratorStrength;
    private static Context mContext;
    private SecureSettingCustomSeekBarPreference mHeadphoneGain;
    private SecureSettingCustomSeekBarPreference mMicrophoneGain;

    private static TwoStatePreference mOTGModeSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null)
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());

        mKcal = findPreference(PREF_DEVICE_KCAL);

        mKcal.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KCalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mHeadphoneGain = (SecureSettingCustomSeekBarPreference) findPreference(PREF_HEADPHONE_GAIN);
        mHeadphoneGain.setOnPreferenceChangeListener(this);

        mMicrophoneGain = (SecureSettingCustomSeekBarPreference) findPreference(PREF_MICROPHONE_GAIN);
        mMicrophoneGain.setOnPreferenceChangeListener(this);

        mOTGModeSwitch = (TwoStatePreference) findPreference(KEY_OTG_SWITCH);
        mOTGModeSwitch.setEnabled(OTGModeSwitch.isSupported());
        mOTGModeSwitch.setChecked(OTGModeSwitch.isCurrentlyEnabled(this.getContext()));
        mOTGModeSwitch.setOnPreferenceChangeListener(new OTGModeSwitch());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHeadphoneGain) {
            FileUtils.setValue(HEADPHONE_GAIN_PATH, newValue + " " + newValue);
        } else if(preference == mMicrophoneGain){
            FileUtils.setValue(MICROPHONE_GAIN_PATH, (int) newValue);
        } else {
            Constants.setPreferenceInt(getContext(), preference.getKey(), Integer.parseInt((String) newValue));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
