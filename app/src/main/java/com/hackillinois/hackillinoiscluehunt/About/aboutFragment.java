package com.hackillinois.hackillinoiscluehunt.About;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.hackillinois.hackillinoiscluehunt.R;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class aboutFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference email = findPreference("email");
        email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                queryEmail();
                return false;
            }
        });
    }

    private void queryEmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + Uri.encode("x@hackillinois.org")));
        startActivity(Intent.createChooser(emailIntent, "Send email via..."));
    }
}
