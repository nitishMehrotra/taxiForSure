package com.taxiforsure.ride;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taxiforsure.CustomAutoCompleteTextView;
import com.example.taxiforsure.PlaceJsonParser;
import com.example.taxiforsure.R;
import com.taxiforsure.database.TfsDatabaseHelper;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;
import com.taxiforsure.util.DateDialogFragment;
import com.taxiforsure.util.DateDialogFragmentListener;
import com.taxiforsure.util.TimeDialogFragment;

public class RideLaterFragment extends Fragment implements View.OnClickListener {
	private static final String TIME_DIALOG_PICKER = "Time picker dialog fragment";
	private static final String DATE_DIALOG_PICKER = "Date picker dialog fragment";
	private int mLayout;
	DateDialogFragment mDateDialogFragment;
	TimeDialogFragment mTimeDialogFragment;

	private TextView mTvTaxiTimeDate, mTvTaxiChoice, mTaxiRideConfirm,
			mTaxiRideCancel;
	private EditText mEtTravellerName, meEtTravellerPhoneNumber,
			mEtTravellerEmail, mEtPickUpDestination;
	AutoCompleteTextView etDropLocation;

	private String mTaxiTime, mTaxiDate;
	private TaxiSelectionAction mTaxiSelected;
	private String mPickupDestination;
	PlacesTask placesTask;

	ParserTask parserTask;

	private SQLiteDatabase m_database;
	private TfsDatabaseHelper m_databaseHelper;

	public RideLaterFragment(int layout, TaxiSelectionAction taxiSelected,
			String pickupDestination) {
		this.mLayout = layout;
		this.mTaxiSelected = taxiSelected;
		this.mPickupDestination = pickupDestination;
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
		/** Initialise database variables */
		m_databaseHelper = TfsDatabaseHelper.getHelper(getActivity()
				.getApplicationContext());
		m_database = m_databaseHelper.getWritableDatabase();

		return rootView;
	}

	private void initialize(View rootView) {
		mTvTaxiTimeDate = (TextView) rootView.findViewById(R.id.tvTaxiTimeDate);
		mTvTaxiTimeDate.setOnClickListener(this);
		mDateDialogFragment = DateDialogFragment.newInstance(getActivity(),
				pickerDialogFragmentDestroyed);
		mTimeDialogFragment = TimeDialogFragment.newInstance(getActivity(),
				pickerDialogFragmentDestroyed);

		etDropLocation = (CustomAutoCompleteTextView) rootView
				.findViewById(R.id.etDropDestination);
		etDropLocation.setThreshold(1);

		etDropLocation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

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

		mTaxiRideConfirm = (TextView) rootView
				.findViewById(R.id.taxiRideConfirm);
		mTaxiRideConfirm.setOnClickListener(this);
		mTaxiRideCancel = (TextView) rootView.findViewById(R.id.taxiRideCancel);
		mTaxiRideCancel.setOnClickListener(this);
		mEtPickUpDestination = (EditText) rootView
				.findViewById(R.id.etPickUpDestination);
		mEtPickUpDestination.setText(mPickupDestination);
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
						/* Save to the database */

						/* Save data in database */
						String sql = "Insert into user (pickup, date,time) VALUES('"
								+ mEtPickUpDestination
								+ "', '"
								+ mTaxiDate
								+ "', '" + mTaxiTime + "');";
						m_database.execSQL(sql);
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

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			String data = "";

			String key = "key=AIzaSyAHRsHUIoDwQEPtxzh_jJehUVE1D_M2jK4";

			String input = "";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			String types = "types=geocode";

			String sensor = "sensor=false";

			String parameters = input + "&" + types + "&" + sensor + "&" + key;
			String output = "json";
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			parserTask = new ParserTask();
			parserTask.execute(result);
		}
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.connect();

			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			PlaceJsonParser placeJsonParser = new PlaceJsonParser();
			try {
				jObject = new JSONObject(jsonData[0]);
				places = placeJsonParser.parse(jObject);
			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), result,
					android.R.layout.simple_list_item_1, from, to);
			etDropLocation.setAdapter(adapter);
		}
	}

}