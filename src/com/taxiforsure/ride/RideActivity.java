package com.taxiforsure.ride;

import com.taxiforsure.home.R;
import com.taxiforsure.home.UserAction.TaxiRideTimeSelectionAction;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class RideActivity extends FragmentActivity {
	private TaxiSelectionAction mTaxiSelected;
	private TaxiRideTimeSelectionAction mRideTimeSelected;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        Bundle bundle = getIntent().getExtras();
        mTaxiSelected = (TaxiSelectionAction) bundle.getSerializable("taxiSelected");
        mRideTimeSelected = (TaxiRideTimeSelectionAction) bundle.getSerializable("rideTimeSelected");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.ride_activity, new RideFragment(bundle.getInt("fragment"), mTaxiSelected))
                    .commit();
        }
    }


}
