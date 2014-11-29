package com.taxiforsure.util;

import java.util.Calendar;

import com.taxiforsure.ride.RideFragment;
import com.taxiforsure.ride.RideFragment.PickerDialogFragmentDestroyed;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

@SuppressLint("NewApi")
public class DateDialogFragment extends DialogFragment {

	public static String TAG = "DateDialogFragment";
	private static PickerDialogFragmentDestroyed mDateDialogFragmentDestroyed; 
	static Context sContext;
	static Calendar sDate;
	static DateDialogFragmentListener sListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		DatePickerDialog datePickerDialog = new DatePickerDialog(sContext,
				dateSetListener, sDate.get(Calendar.YEAR),
				sDate.get(Calendar.MONTH), sDate.get(Calendar.DAY_OF_MONTH));

		return datePickerDialog;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mDateDialogFragmentDestroyed.datePickerDestroyed();
	}

	public static DateDialogFragment newInstance(Context context,
			PickerDialogFragmentDestroyed fragmentDestroyed) {
		DateDialogFragment dialog = new DateDialogFragment();

		sContext = context;
		mDateDialogFragmentDestroyed = fragmentDestroyed;
		sDate = Calendar.getInstance();

		Bundle args = new Bundle();
		args.putString("title", "Set Date");

		dialog.setArguments(args);
		return dialog;
	}

	public void setDateDialogFragmentListener(
			DateDialogFragmentListener listener) {
		sListener = listener;
	}

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			// create new Calendar object for date chosen
			// this is done simply combine the three args into one
			Calendar newDate = Calendar.getInstance();
			newDate.set(year, monthOfYear, dayOfMonth);
			// call back to the DateDialogFragment listener
			sListener.dateDialogFragmentDateSet(newDate);
		}
	};
}