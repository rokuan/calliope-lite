package apps.rokuan.com.calliope_helper_lite.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import apps.rokuan.com.calliope_helper_lite.R;

/**
 * Created by LEBEAU Christophe on 02/04/2016.
 */
public class ConfigurationActivity extends PreferenceActivity {
    public static class SpeechPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.speech_preference_screen);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SpeechPreferenceFragment())
                .commit();
    }
}
