package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import fr.tvbarthel.apps.simplethermometer.R;


public class OpacityDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View opacityChooser = inflater.inflate(R.layout.dialog_opacity, null);
        final GradientDrawable previewBackground = (GradientDrawable) opacityChooser.findViewById(R.id.dialog_opacity_preview).getBackground();
        final SeekBar seekBar = (SeekBar) opacityChooser.findViewById(R.id.dialog_opacity_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                previewBackground.setAlpha(progress);
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
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setInverseBackgroundForced(true);
        return builder.create();
    }
}
