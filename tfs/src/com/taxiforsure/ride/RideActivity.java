package com.taxiforsure.ride;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.taxiforsure.R;
import com.taxiforsure.home.UserAction.TaxiRideTimeSelectionAction;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;

public class RideActivity extends FragmentActivity {
	private TaxiSelectionAction mTaxiSelected;
	private TaxiRideTimeSelectionAction mRideTimeSelected;
	private String mPickupDestination;
	private int mLayout;

	Double lat, longi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride);
		Bundle bundle = getIntent().getExtras();
		mTaxiSelected = (TaxiSelectionAction) bundle
				.getSerializable("taxiSelected");
		mRideTimeSelected = (TaxiRideTimeSelectionAction) bundle
				.getSerializable("rideTimeSelected");
		mPickupDestination = (String) bundle.getSerializable("pickup");
		lat = (Double) bundle.getSerializable("lat");
		longi = (Double) bundle.getSerializable("longi");
		mLayout = bundle.getInt("fragment");
		if (savedInstanceState == null) {
			if (mLayout == R.layout.fragment_ride_later) {
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.ride_activity,
								new RideLaterFragment(mLayout, mTaxiSelected,
										mPickupDestination)).commit();
			} else if (mLayout == R.layout.fragment_ride_now) {
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.ride_activity,
								new RideNowFragment(mLayout, mTaxiSelected,
										mPickupDestination, lat, longi))
						.commit();
			} else {

			}
		}
	}

}
