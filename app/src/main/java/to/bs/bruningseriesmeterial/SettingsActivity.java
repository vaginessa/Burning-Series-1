package to.bs.bruningseriesmeterial;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static String FOLDER_NAME = "bs";
    private static int downloadFolder_CODE = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            final SharedPreferences SP = getActivity().getPreferences(Context.MODE_PRIVATE);


            String folder = SP.getString("downloadFolder", "NA");
            final boolean hollyday = SP.getBoolean("holiday", false);
            Set<String> hoster = SP.getStringSet("holiday_hoster", new ArraySet<String>(Arrays.asList("vivo.sx")));

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SwitchPreference switchPreference = (SwitchPreference) findPreference("holiday");
            final Preference filePicker = (Preference) findPreference("downloadFolder");
            final MultiSelectListPreference selectListPreference = (MultiSelectListPreference) findPreference("holiday_hoster_list");

            switchPreference.setChecked(hollyday);
            filePicker.setEnabled(hollyday);
            selectListPreference.setEnabled(hollyday);
            selectListPreference.setValues(hoster);
            selectListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Set<String> hoster = (Set<String>) newValue;
                    SharedPreferences.Editor editor = SP.edit();
                    editor.putStringSet("holiday_hoster", hoster);
                    editor.commit();
                    return true;
                }
            });

            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    filePicker.setEnabled((Boolean) newValue);
                    selectListPreference.setEnabled((Boolean) newValue);
                    SharedPreferences.Editor editor = SP.edit();
                    editor.putBoolean("holiday", (Boolean) newValue);
                    editor.commit();
                    return true;
                }
            });

            filePicker.setSummary(folder);
            filePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory().toString());
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setDataAndType(selectedUri, "*/*");
                    startActivityForResult(intent, downloadFolder_CODE);
                    return true;
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == downloadFolder_CODE && resultCode == RESULT_OK) {
                String path = data.getData().getPath();
                path = path.substring(0, path.lastIndexOf(File.separator));

                Log.d(TAG, "File Path: " + path);
                SharedPreferences SP = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = SP.edit();
                editor.putString("downloadFolder", path);
                editor.commit();
                Preference filePicker = (Preference) findPreference("downloadFolder");
                filePicker.setSummary(path);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }


    }
}
