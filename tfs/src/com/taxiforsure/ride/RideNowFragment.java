package com.taxiforsure.ride;

import java.util.Random;

import com.example.taxiforsure.R;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;
import com.taxiforsure.util.ShowProgressDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RideNowFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = "RideNow";
	private int mLayout;
	private TextView mTaxiRideCancel;
	private TaxiSelectionAction mTaxiSelected;
	private ShowProgressDialog mShowDialog;
	private String mPickupDestination;

	public RideNowFragment(int layout, TaxiSelectionAction taxiSelected, String pickupDestination) {
		this.mLayout = layout;
		this.mTaxiSelected = taxiSelected;
		this.mPickupDestination = pickupDestination;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mLayout == R.layout.fragment_ride_now)
			Log.e(TAG, "In RideNow Fragment");

		View rootView = inflater.inflate(mLayout, container, false);
		initialize(rootView);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mShowDialog.dismissProgressBar();
				Intent intent = new Intent();
				Random random = new Random();
				int randomNumber = random.nextInt(2);
				if (randomNumber == 0)
					Toast.makeText(
							getActivity(),
							"Due to some technical problem taxi couldn't be booked",
							Toast.LENGTH_SHORT).show();
				if (randomNumber == 1)
					Toast.makeText(
							getActivity(),
							"Your taxi would reach soon to the pickup location",
							Toast.LENGTH_SHORT).show();
				getActivity().setResult(Activity.RESULT_OK, intent);
				getActivity().finish();

			}
		}, 1000 * 2);
		return rootView;
	}

	private void initialize(View rootView) {
		mShowDialog = new ShowProgressDialog();
		mShowDialog.showProgressBar(getActivity(), "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}

}