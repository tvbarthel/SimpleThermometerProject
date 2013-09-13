package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import fr.tvbarthel.apps.simplethermometer.R;

public class ChangeColorDialogFragment extends DialogFragment {

	private static final String BUNDLE_CHANGE_COLOR_OPTIONS = "BundleChangeColorOptions";
	private Listener mListener;

	public static ChangeColorDialogFragment newInstance(String[] changeColorOption) {
		ChangeColorDialogFragment fragment = new ChangeColorDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putStringArray(BUNDLE_CHANGE_COLOR_OPTIONS, changeColorOption);
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
		mListener = null;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final Bundle arguments = getArguments();
		final String[] changeColorOptions = arguments.getStringArray(BUNDLE_CHANGE_COLOR_OPTIONS);
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
		return builder.create();
	}

	public interface Listener {
		public void onChangeColorRequested(int which);
	}

}
