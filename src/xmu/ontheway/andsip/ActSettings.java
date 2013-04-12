package xmu.ontheway.andsip;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ActSettings extends PreferenceActivity {
	// @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
