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
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;

/**
 * A dialog fragment used to change and store the temperature unit
 */
public class TemperatureUnitPickerDialogFragment extends DialogFragment {

    private static final String BUNDLE_TEMPERATURE_UNIT_NAMES = "BundleTemperatureUnitNames";
    private static final String BUNDLE_TEMPERATURE_UNIT_SYMBOLS = "BundleTemperatureUnitSymbols";


    public static TemperatureUnitPickerDialogFragment newInstance(String[] temperatureUnitNames, String[] temperatureUnitSymbols) {
        TemperatureUnitPickerDialogFragment fragment = new TemperatureUnitPickerDialogFragment();

        //Put the temperature unit names and symbols in the fragment arguments
        Bundle arguments = new Bundle();
        arguments.putStringArray(BUNDLE_TEMPERATURE_UNIT_NAMES, temperatureUnitNames);
        arguments.putStringArray(BUNDLE_TEMPERATURE_UNIT_SYMBOLS, temperatureUnitSymbols);
        fragment.setArguments(arguments);
        return fragment;
    }

	/*
        DialogFragment Overrides
	 */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Retrieve information from the arguments
        Bundle arguments = getArguments();
        final String[] temperatureUnitNames = arguments.getStringArray(BUNDLE_TEMPERATURE_UNIT_NAMES);
        final String[] temperatureUnitSymbols = arguments.getStringArray(BUNDLE_TEMPERATURE_UNIT_SYMBOLS);

        //Create an AlertDialog to display the different temperature unit that can be chosen
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.temperature_unit_picker_dialog_fragment_title);
        final ArrayAdapter<String> arrayAdapterColorNames = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,
                temperatureUnitNames);
        builder.setAdapter(arrayAdapterColorNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = defaultPreferences.edit();
                editor.putString(PreferenceUtils.PREF_KEY_TEMPERATURE_UNIT_STRING, temperatureUnitSymbols[which]);
                editor.commit();
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setInverseBackgroundForced(true);
        return builder.create();
    }
}
