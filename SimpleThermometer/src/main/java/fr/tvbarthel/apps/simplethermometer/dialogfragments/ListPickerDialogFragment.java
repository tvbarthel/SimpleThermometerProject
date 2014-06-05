package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

/**
 * A dialog fragment used to select an item in a list.
 */
public class ListPickerDialogFragment extends DialogFragment {

    private static final String ARGS_CHOICE_ID = "ListPickerDialogFragment.Args.ChoiceId";
    private static final String ARGS_CHOICES = "ListPickerDialogFragment.Args.Choices";
    private Listener mListener;

    public static ListPickerDialogFragment newInstance(int choiceId, String[] choices) {
        ListPickerDialogFragment fragment = new ListPickerDialogFragment();

        //Put the different colors that can be changed in the fragment arguments
        Bundle arguments = new Bundle();
        arguments.putInt(ARGS_CHOICE_ID, choiceId);
        arguments.putStringArray(ARGS_CHOICES, choices);
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
        if (activity instanceof ListPickerDialogFragment.Listener) {
            mListener = (ListPickerDialogFragment.Listener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ListPickerDialogFragment.Listener");
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
        final String[] choices = arguments.getStringArray(ARGS_CHOICES);
        final int choiceId = arguments.getInt(ARGS_CHOICE_ID);

        //Create an AlertDialog to display the different color that can be changed
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,
                choices);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onChoiceSelected(choiceId, which);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setInverseBackgroundForced(true);
        return builder.create();
    }

    /**
     * A public Interface used to notify a color change.
     */
    public interface Listener {
        //Notify the color to change
        public void onChoiceSelected(int choiceId, int which);
    }

}
