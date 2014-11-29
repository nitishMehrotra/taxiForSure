package com.taxiforsure.ride;

import java.util.Random;

import com.taxiforsure.home.UserAction.TaxiSelectionAction;
import com.taxiforsure.util.ShowProgressDialog;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taxiforsure.R;
import com.example.taxiforsure.TouchableMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;
import com.taxiforsure.util.ShowProgressDialog;

public class RideNowFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = "RideNow";
	private int mLayout;
	private TextView mTaxiRideCancel;
	private TaxiSelectionAction mTaxiSelected;
	private ShowProgressDialog mShowDialog;
	private String mPickupDestination;
	private Double lat;
	private Double longi;
	TouchableMapFragment touchableMapFragment;

	private GoogleMap map;

	public RideNowFragment(int layout, TaxiSelectionAction taxiSelected,
			String pickupDestination, Double lat, Double longi) {
		this.mLayout = layout;
		this.mTaxiSelected = taxiSelected;
		this.mPickupDestination = pickupDestination;

		this.lat = lat;
		this.longi = longi;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mLayout == R.layout.fragment_ride_now)
			Log.e(TAG, "In RideNow Fragment");

		View rootView = inflater.inflate(mLayout, container, false);

		touchableMapFragment = (TouchableMapFragment) getActivity()
				.getFragmentManager().findFragmentById(R.id.map);
		map = touchableMapFragment.getMap();

		if (map != null) {
			UiSettings settings = map.getUiSettings();

			settings.setZoomControlsEnabled(false);
			settings.setCompassEnabled(false);
			settings.setMyLocationButtonEnabled(false);
			settings.setScrollGesturesEnabled(false);
			settings.setTiltGesturesEnabled(false);
			settings.setAllGesturesEnabled(false);

			/* Display a marker on the map on the required location */
			LatLng myLocation = new LatLng(lat, longi);
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myLocation).zoom(16).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			map.clear();
			map.addMarker(new MarkerOptions().position(myLocation)
					.icon(BitmapDescriptorFactory.defaultMarker())
					.draggable(true));

		}

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