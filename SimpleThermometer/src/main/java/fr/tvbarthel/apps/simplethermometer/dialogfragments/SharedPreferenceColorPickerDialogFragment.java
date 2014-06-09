package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.models.ColorPick;
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;

/**
 * A dialog fragment used to change and store a color
 */
public class SharedPreferenceColorPickerDialogFragment extends DialogFragment {

    private static final String ARG_PREFERENCE_ID = "SharedPreferenceColorPickerDialogFragment.Args.PreferenceId";
    private static final String ARG_COLOR_PICKS = "SharedPreferenceColorPickerDialogFragment.Args.ColorPicks";

    public static SharedPreferenceColorPickerDialogFragment newInstance(PreferenceUtils.PreferenceId preferenceId, ArrayList<ColorPick> colorPicks) {
        SharedPreferenceColorPickerDialogFragment fragment = new SharedPreferenceColorPickerDialogFragment();

        //Put the preferenceKey, the color names and the color resource Ids in the fragment arguments
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_PREFERENCE_ID, preferenceId);
        arguments.putParcelableArrayList(ARG_COLOR_PICKS, colorPicks);
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
        final PreferenceUtils.PreferenceId preferenceId = (PreferenceUtils.PreferenceId) arguments.getSerializable(ARG_PREFERENCE_ID);
        final ArrayList<ColorPick> colorPicks = arguments.getParcelableArrayList(ARG_COLOR_PICKS);

        //Create an AlertDialog to display the different colors that can be chosen
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.shared_preference_color_picker_dialog_fragment_title);
        final ArrayAdapter<ColorPick> arrayAdapter = new ColorPickAdapter(getActivity().getApplicationContext(), colorPicks);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.storePreferedColor(getActivity(), preferenceId, colorPicks.get(which).getColor());
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setInverseBackgroundForced(true);

        return builder.create();
    }

    /**
     * A simple {@link android.widget.ArrayAdapter} that is used to adapt {@link fr.tvbarthel.apps.simplethermometer.models.ColorPick}
     */
    private static class ColorPickAdapter extends ArrayAdapter<ColorPick> {

        public ColorPickAdapter(Context context, ArrayList<ColorPick> objects) {
            super(context, R.layout.row_color_pick, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ColorPick colorPick = getItem(position);
            View colorPickView = convertView;
            if (colorPickView == null) {
                final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                colorPickView = inflater.inflate(R.layout.row_color_pick, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) colorPickView.findViewById(R.id.row_color_pick_name);
                viewHolder.previewBackground = (GradientDrawable) colorPickView.findViewById(R.id.row_color_pick_preview).getBackground();
                colorPickView.setTag(viewHolder);
            }

            final ViewHolder viewHolder = (ViewHolder) colorPickView.getTag();
            viewHolder.name.setText(colorPick.getName());
            viewHolder.previewBackground.setColor(colorPick.getColor());

            return colorPickView;
        }

        /**
         * A simple ViewHolder
         */
        private static class ViewHolder {
            TextView name;
            GradientDrawable previewBackground;
        }
    }
}
