package com.example.desk.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

        // create change listener
        Preference pref = findPreference(getString(R.string.pref_movie_list_key));
        pref.setOnPreferenceChangeListener(this);

        // set initial summary
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onPreferenceChange(pref, settings.getString(pref.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        ListPreference pref = (ListPreference) preference;
        String strValue = value.toString();
        int valIndex = pref.findIndexOfValue(strValue);

        pref.setSummary(pref.getEntries()[valIndex]);

        return true;
    }
}