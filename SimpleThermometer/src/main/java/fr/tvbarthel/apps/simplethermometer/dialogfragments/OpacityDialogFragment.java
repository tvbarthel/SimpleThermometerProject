package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.utils.ColorUtils;
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;


public class OpacityDialogFragment extends DialogFragment {

    private static final String ARG_PREFERENCE_ID = "OpacityDialogFragment.Args.PreferenceId";

    public static OpacityDialogFragment newInstance(PreferenceUtils.PreferenceId preferenceId) {
        final OpacityDialogFragment fragment = new OpacityDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_PREFERENCE_ID, preferenceId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final PreferenceUtils.PreferenceId preferenceId = (PreferenceUtils.PreferenceId) getArguments().getSerializable(ARG_PREFERENCE_ID);
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View opacityChooser = inflater.inflate(R.layout.dialog_opacity, null);
        final int prefColor = PreferenceUtils.getPreferedColor(getActivity(), preferenceId);
        final int prefAlpha = PreferenceUtils.getPreferedAlpha(getActivity(), preferenceId);
        final GradientDrawable previewBackground = (GradientDrawable) opacityChooser.findViewById(R.id.dialog_opacity_preview).getBackground();
        final SeekBar seekBar = (SeekBar) opacityChooser.findViewById(R.id.dialog_opacity_seek_bar);

        // initialize the seekbar and the preview
        previewBackground.setColor(ColorUtils.addAlphaToColor(prefColor, prefAlpha));
        seekBar.setProgress(prefAlpha);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                previewBackground.setColor(ColorUtils.addAlphaToColor(prefColor, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.opacity_picker_title));
        builder.setView(opacityChooser);
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.storePreferedAlpha(getActivity(), preferenceId, seekBar.getProgress());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setInverseBackgroundForced(true);
        return builder.create();
    }
}
