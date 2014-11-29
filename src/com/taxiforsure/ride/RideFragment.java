package com.taxiforsure.ride;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taxiforsure.R;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;
import com.taxiforsure.util.DateDialogFragment;
import com.taxiforsure.util.DateDialogFragmentListener;
import com.taxiforsure.util.TimeDialogFragment;

public class RideFragment extends Fragment implements View.OnClickListener {
	private static final String TIME_DIALOG_PICKER = "Time picker dialog fragment";
	private static final String DATE_DIALOG_PICKER = "Date picker dialog fragment";
	private int mLayout;
	DateDialogFragment mDateDialogFragment;
	TimeDialogFragment mTimeDialogFragment;

	private TextView mTvTaxiTimeDate, mTvTaxiChoice, mTaxiRideConfirm, mTaxiRideCancel;
	private EditText mEtTravellerName, meEtTravellerPhoneNumber,
			mEtTravellerEmail;
	private String mTaxiTime, mTaxiDate;
	private TaxiSelectionAction mTaxiSelected;

	public RideFragment(int layout, TaxiSelectionAction taxiSelected) {
		this.mLayout = layout;
		this.mTaxiSelected = taxiSelected;
	}

	public interface PickerDialogFragmentDestroyed {
		public void datePickerDestroyed();

		public void timePickerDestroyed();
	}

	PickerDialogFragmentDestroyed pickerDialogFragmentDestroyed = new PickerDialogFragmentDestroyed() {

		@Override
		public void datePickerDestroyed() {
			mTimeDialogFragment.show(getFragmentManager(), TIME_DIALOG_PICKER);
		}

		@Override
		public void timePickerDestroyed() {
			mTvTaxiTimeDate.setText(mTaxiTime + " | " + mTaxiDate);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(mLayout, container, false);
		initialize(rootView);
		return rootView;
	}

	private void initialize(View rootView) {
		mTvTaxiTimeDate = (TextView) rootView.findViewById(R.id.tvTaxiTimeDate);
		mTvTaxiTimeDate.setOnClickListener(this);
		mDateDialogFragment = DateDialogFragment.newInstance(getActivity(),
				pickerDialogFragmentDestroyed);
		mTimeDialogFragment = TimeDialogFragment.newInstance(getActivity(),
				pickerDialogFragmentDestroyed);
		mTimeDialogFragment
				.setDateDialogFragmentListener(new DateDialogFragmentListener() {

					@Override
					public void dateDialogFragmentDateSet(Calendar date) {
						// update the fragment

						NumberFormat f = new DecimalFormat("00");
						mTaxiTime = f.format(date.get(Calendar.HOUR_OF_DAY))
								+ ":" + f.format(date.get(Calendar.MINUTE));
					}
				});
		mDateDialogFragment
				.setDateDialogFragmentListener(new DateDialogFragmentListener() {

					@Override
					public void dateDialogFragmentDateSet(Calendar date) {
						// update the fragment
						SimpleDateFormat df = new SimpleDateFormat(
								"dd-MMM-yyyy");
						String formattedDate = df.format(date.getTime());
						mTaxiDate = formattedDate;
						Log.e("Nitish", mTaxiDate);

					}
				});

		mDateDialogFragment.show(getFragmentManager(), DATE_DIALOG_PICKER);

		mTvTaxiChoice = (TextView) rootView.findViewById(R.id.tvTaxiChoice);
		if (mTaxiSelected == TaxiSelectionAction.HATCHBACK) {
			mTvTaxiChoice.setText("Hatchback");
		} else if (mTaxiSelected == TaxiSelectionAction.SEDAN) {
			mTvTaxiChoice.setText("Sedan");
		} else {
			mTvTaxiChoice.setText("SUV");
		}

		mEtTravellerEmail = (EditText) rootView
				.findViewById(R.id.etTravellerEmail);
		mEtTravellerName = (EditText) rootView
				.findViewById(R.id.etTravellerName);
		meEtTravellerPhoneNumber = (EditText) rootView
				.findViewById(R.id.etTravellerPhoneNumber);
		
		mTaxiRideConfirm = (TextView) rootView.findViewById(R.id.taxiRideConfirm);
		mTaxiRideConfirm.setOnClickListener(this);
		mTaxiRideCancel = (TextView) rootView.findViewById(R.id.taxiRideCancel);
		mTaxiRideCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvTaxiTimeDate:
			mDateDialogFragment.show(getFragmentManager(), DATE_DIALOG_PICKER);
			break;

		case R.id.taxiRideCancel:
			getActivity().finish();
			break;
		case R.id.taxiRideConfirm:
			if (!mEtTravellerName.getText().toString().contentEquals("")) {
				if (!meEtTravellerPhoneNumber.getText().toString()
						.contentEquals("")) {
					if (!mEtTravellerEmail.getText().toString()
							.contentEquals("")) {
						Toast.makeText(getActivity(),
								"Your Trip has been confirmed",
								Toast.LENGTH_LONG).show();
						getActivity().finish();
					} else {
						Toast.makeText(getActivity(),
								"Enter Traveller's EmailID", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(getActivity(),
							"Enter Traveller's PhoneNumber", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getActivity(), "Enter Traveller's Name",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

}