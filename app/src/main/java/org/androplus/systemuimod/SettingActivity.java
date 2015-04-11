package org.androplus.systemuimod;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;


public class SettingActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
	}

	
	public static class SettingFragment extends PreferenceFragment{
		
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			if(Build.VERSION.SDK_INT < 21) {
				PreferenceScreen prefScreen = getPreferenceScreen();
				CheckBoxPreference checkboxPreference3 = 
				(CheckBoxPreference)prefScreen.findPreference("key_closeall");
				checkboxPreference3.setEnabled(false);
			}
			
		}
	}
	
}
