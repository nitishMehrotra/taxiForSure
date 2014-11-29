package com.taxiforsure.util;

import java.util.Calendar;

import com.taxiforsure.ride.RideFragment.PickerDialogFragmentDestroyed;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

@SuppressLint("NewApi")
public class TimeDialogFragment extends DialogFragment {

	public static String TAG = "TimeDialogFragment";
	private static PickerDialogFragmentDestroyed mTimeDialogFragmentDestroyed;
	static Context sContext;
	static Calendar sDate;
	static DateDialogFragmentListener sListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new TimePickerDialog(getActivity(), timeSetListener,
				sDate.get(Calendar.HOUR_OF_DAY), sDate.get(Calendar.MINUTE),
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTimeDialogFragmentDestroyed.timePickerDestroyed();
	}

	public static TimeDialogFragment newInstance(Context context,
			PickerDialogFragmentDestroyed fragmentDestroyed) {
		TimeDialogFragment dialog = new TimeDialogFragment();
		mTimeDialogFragmentDestroyed = fragmentDestroyed;
		sContext = context;
		sDate = Calendar.getInstance();

		Bundle args = new Bundle();
		args.putString("title", "Set Time");

		dialog.setArguments(args);
		return dialog;
	}

	public void setDateDialogFragmentListener(
			DateDialogFragmentListener listener) {
		sListener = listener;
	}

	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar newDate = Calendar.getInstance();
			newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
			newDate.set(Calendar.MINUTE, minute);
			// call back to the DateDialogFragment listener
			sListener.dateDialogFragmentDateSet(newDate);
		}
	};
}