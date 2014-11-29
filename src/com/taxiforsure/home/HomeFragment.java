package com.taxiforsure.home;

import com.taxiforsure.home.FragmentStatus.FragementVisibility;
import com.taxiforsure.home.UserAction.TaxiRideTimeSelectionAction;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;
import com.taxiforsure.ride.RideActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment implements View.OnClickListener,
		FragementVisibility {
	public static final String ARG_MENU_INDEX = "HOME";
	private final String TAG = "HomeFragment";
	private TextView mTaxiChoiceHatchback, mTaxiChoiceSedan, mTaxiChoiceSUV,
			mTaxiTimeChoiceNow, mTaxiTimeChoiceLater;

	private LinearLayout mTaxiChoices, mTaxiTimeChoices;

	private static TaxiSelectionAction mTaxiSelected = TaxiSelectionAction.NONE;
	private static TaxiRideTimeSelectionAction mRideTimeSelected = TaxiRideTimeSelectionAction.NONE;

	public HomeFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
		initialize(rootView);
		getActivity().setTitle("");
		return rootView;
	}

	private void initialize(View rootView) {
		mTaxiChoiceHatchback = (TextView) rootView
				.findViewById(R.id.taxiChoiceHatchback);
		mTaxiChoiceHatchback.setOnClickListener(this);
		mTaxiChoiceSedan = (TextView) rootView
				.findViewById(R.id.taxiChoiceSedan);
		mTaxiChoiceSedan.setOnClickListener(this);
		mTaxiChoiceSUV = (TextView) rootView.findViewById(R.id.taxiChoiceSUV);
		mTaxiChoiceSUV.setOnClickListener(this);
		mTaxiTimeChoiceNow = (TextView) rootView
				.findViewById(R.id.taxiTimeChoiceNow);
		mTaxiTimeChoiceNow.setOnClickListener(this);
		mTaxiTimeChoiceLater = (TextView) rootView
				.findViewById(R.id.taxiTimeChoiceLater);
		mTaxiTimeChoiceLater.setOnClickListener(this);
		mTaxiChoices = (LinearLayout) rootView.findViewById(R.id.taxiChoices);
		mTaxiTimeChoices = (LinearLayout) rootView
				.findViewById(R.id.taxiTimeChoices);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.taxiChoiceHatchback:
			Log.e(TAG, "HatchBack Selected");
			mTaxiSelected = TaxiSelectionAction.HATCHBACK;
			mRideTimeSelected = TaxiRideTimeSelectionAction.NONE;
			mTaxiChoices.setVisibility(View.GONE);
			mTaxiTimeChoices.setVisibility(View.VISIBLE);
			break;
		case R.id.taxiChoiceSedan:
			Log.e(TAG, "Sedan Selected");
			mTaxiSelected = TaxiSelectionAction.SEDAN;
			mRideTimeSelected = TaxiRideTimeSelectionAction.NONE;
			mTaxiChoices.setVisibility(View.GONE);
			mTaxiTimeChoices.setVisibility(View.VISIBLE);
			break;
		case R.id.taxiChoiceSUV:
			Log.e(TAG, "SUV Selected");
			mTaxiSelected = TaxiSelectionAction.SUV;
			mRideTimeSelected = TaxiRideTimeSelectionAction.NONE;
			mTaxiChoices.setVisibility(View.GONE);
			mTaxiTimeChoices.setVisibility(View.VISIBLE);
			break;
			
		case R.id.taxiTimeChoiceLater:
			Log.e(TAG,"Ride Later Selected");
			Intent intent = new Intent(getActivity(), RideActivity.class);
			mRideTimeSelected = TaxiRideTimeSelectionAction.LATER;
			Bundle bundle = new Bundle();
			bundle.putSerializable("rideTimeSelected", mRideTimeSelected);
			bundle.putSerializable("taxiSelected", mTaxiSelected);
			bundle.putInt("fragment", R.layout.fragment_ride_later);
			intent.putExtras(bundle);
			toggleVisibility();
			startActivity(intent);
		}
	}

	static public TaxiSelectionAction getTaxiSelected() {
		return mTaxiSelected;
	}

	static public TaxiRideTimeSelectionAction getRideTimeSelected() {
		return mRideTimeSelected;
	}

	@Override
	public void toggleVisibility() {
		if (mTaxiChoices != null && mTaxiTimeChoices != null) {
			mTaxiTimeChoices.setVisibility(View.GONE);
			mTaxiChoices.setVisibility(View.VISIBLE);
			mTaxiSelected = TaxiSelectionAction.NONE;
			mRideTimeSelected = TaxiRideTimeSelectionAction.NONE;
		}

	}
}