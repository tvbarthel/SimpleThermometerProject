package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import fr.tvbarthel.apps.simplethermometer.R;

/**
 * A dialog fragment used to change and store a color
 */
public class SharedPreferenceColorPickerDialogFragment extends DialogFragment {

	private static final String BUNDLE_COLOR_NAMES = "BundleColorNames";
	private static final String BUNDLE_COLOR_RESOURCE_IDS = "BundleColorResourceIds";
	private static final String BUNLDE_PREFERENCE_KEY = "BundlePreferenceKey";

	public static SharedPreferenceColorPickerDialogFragment newInstance(String preferenceKey, String[] colorNames, int[] colorResourcesIds) {
		SharedPreferenceColorPickerDialogFragment fragment = new SharedPreferenceColorPickerDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putString(BUNLDE_PREFERENCE_KEY, preferenceKey);
		arguments.putStringArray(BUNDLE_COLOR_NAMES, colorNames);
		arguments.putIntArray(BUNDLE_COLOR_RESOURCE_IDS, colorResourcesIds);
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle arguments = getArguments();
		final String preferenceKey = arguments.getString(BUNLDE_PREFERENCE_KEY);
		final String[] colorNames = arguments.getStringArray(BUNDLE_COLOR_NAMES);
		final int[] colorResourceIds = arguments.getIntArray(BUNDLE_COLOR_RESOURCE_IDS);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.shared_preference_color_picker_dialog_fragment_title);
		final ArrayAdapter<String> arrayAdapterColorNames = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_expandable_list_item_1,
				colorNames);
		builder.setAdapter(arrayAdapterColorNames, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = defaultPreferences.edit();
				editor.putInt(preferenceKey, colorResourceIds[which]);
				editor.commit();
			}
		});
		builder.setCancelable(true);
		builder.setNegativeButton(R.string.alert_dialog_cancel_button, null);

		return builder.create();
	}
}
