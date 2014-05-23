package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import fr.tvbarthel.apps.simplethermometer.R;

/**
 * A dialog fragment used to select a color to change
 */
public class ChangeColorDialogFragment extends DialogFragment {

    private static final String BUNDLE_CHANGE_COLOR_OPTIONS = "BundleChangeColorOptions";
    private Listener mListener;

    public static ChangeColorDialogFragment newInstance(String[] changeColorOption) {
        ChangeColorDialogFragment fragment = new ChangeColorDialogFragment();

        //Put the different colors that can be changed in the fragment arguments
        Bundle arguments = new Bundle();
        arguments.putStringArray(BUNDLE_CHANGE_COLOR_OPTIONS, changeColorOption);
        fragment.setArguments(arguments);
        return fragment;
    }

    /*
        DialogFragment Overrides
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Try to cast the activity into a ChangeColorDialogFragment.Listener
        if (activity instanceof ChangeColorDialogFragment.Listener) {
            mListener = (ChangeColorDialogFragment.Listener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ChangeColorDialogFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Release the listening activity
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Retrieve information from the arguments
        final Bundle arguments = getArguments();
        final String[] changeColorOptions = arguments.getStringArray(BUNDLE_CHANGE_COLOR_OPTIONS);

        //Create an AlertDialog to display the different color that can be changed
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,
                changeColorOptions);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onChangeColorRequested(which);
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel_button, null);
        builder.setInverseBackgroundForced(true);
        return builder.create();
    }

    /**
     * A public Interface used to notify a color change.
     */
    public interface Listener {
        //Notify the color to change
        public void onChangeColorRequested(int which);
    }

}
