package com.taxiforsure.home;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.taxiforsure.CustomAutoCompleteTextView;
import com.example.taxiforsure.MapStateListener;
import com.example.taxiforsure.PlaceJsonParser;
import com.example.taxiforsure.R;
import com.example.taxiforsure.TouchableMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taxiforsure.home.FragmentStatus.FragementVisibility;
import com.taxiforsure.home.UserAction.TaxiRideTimeSelectionAction;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;
import com.taxiforsure.ride.RideActivity;

public class HomeFragment extends Fragment implements View.OnClickListener,
		FragementVisibility {
	public static final String ARG_MENU_INDEX = "HOME";
	private final String TAG = "HomeFragment";
	private TextView mTaxiChoiceHatchback, mTaxiChoiceSedan, mTaxiChoiceSUV,
			mTaxiTimeChoiceNow, mTaxiTimeChoiceLater;

	private LinearLayout mTaxiChoices, mTaxiTimeChoices;

	private static TaxiSelectionAction mTaxiSelected = TaxiSelectionAction.NONE;
	private static TaxiRideTimeSelectionAction mRideTimeSelected = TaxiRideTimeSelectionAction.NONE;

	private GoogleMap map;
	private LatLng myLocation;
	Boolean firstTime = true;

	TouchableMapFragment touchableMapFragment;

	ImageView ivMyLocation;
	/* Objects to be used for auto complete textview */
	CustomAutoCompleteTextView auto_places;
	PlacesTask placesTask;
	ParserTask parserTask;

	private Double currentLat, currentLong;

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

		/* Maps integration to code */
		touchableMapFragment = (TouchableMapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		map = touchableMapFragment.getMap();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		map.setMyLocationEnabled(true);
		/* Set auto complete for autocomplete edit text custom */
		auto_places = (CustomAutoCompleteTextView) rootView
				.findViewById(R.id.auto_places);
		auto_places.setThreshold(1);

		ivMyLocation = (ImageView) rootView.findViewById(R.id.ivMyLocation);
		ivMyLocation.setOnClickListener(this);

		auto_places.addTextChangedListener(new TextWatcher() {

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

		if (map != null) {
			UiSettings settings = map.getUiSettings();

			settings.setZoomControlsEnabled(false);
			settings.setCompassEnabled(false);
			settings.setMyLocationButtonEnabled(false);

			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

				@Override
				public void onMyLocationChange(Location location) {
					// TODO Auto-generated method stub
					if (firstTime) {
						myLocation = new LatLng(location.getLatitude(),
								location.getLongitude());
						CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(myLocation).zoom(16).build();
						map.animateCamera(CameraUpdateFactory
								.newCameraPosition(cameraPosition));
						firstTime = false;

						currentLat = location.getLatitude();
						currentLong = location.getLongitude();
						new StringFromLocation().execute();
					}
				}
			});

			/*
			 * Using MapStateListener from github, to check when the map is
			 * settled
			 */
			new MapStateListener(map, touchableMapFragment, getActivity()) {
				@Override
				public void onMapTouched() {
				}

				@Override
				public void onMapReleased() {
				}

				@Override
				public void onMapUnsettled() {
				}

				@Override
				public void onMapSettled(LatLng location) {
					map.clear();
					map.addMarker(new MarkerOptions().position(location));

					currentLat = location.latitude;
					currentLong = location.longitude;
					new StringFromLocation().execute();
				}
			};

			/*
			 * Set on item click listener to be called when the user clicks on
			 * any item from the list
			 */
			auto_places.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> view, View arg1,
						int position, long arg3) {
					HashMap<String, String> place = new HashMap<String, String>();
					place = (HashMap<String, String>) view
							.getItemAtPosition(position);
					String display = place.get("description");
					if (!display.contentEquals("")) {
						hideKeyboard();
						try {
							if (getLatLong(getLocationInfo(java.net.URLEncoder
									.encode(display, "UTF-8").replace("+",
											"%20")))) {
								/* Location is available */
								myLocation = new LatLng(currentLat, currentLong);
								CameraPosition cameraPosition = new CameraPosition.Builder()
										.target(myLocation).zoom(16).build();
								map.animateCamera(CameraUpdateFactory
										.newCameraPosition(cameraPosition));
								firstTime = false;

								new StringFromLocation().execute();

							} else {
								/* Location entered not available */
								Toast.makeText(getActivity(),
										"Location not found!",
										Toast.LENGTH_SHORT).show();
							}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});

			/*
			 * Add a listener to point to a location entered by the user when
			 * the done of auto complete textview is pressed
			 */
			auto_places.setOnEditorActionListener(new OnEditorActionListener() {
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						hideKeyboard();
						if (!auto_places.getText().toString().contentEquals("")) {
							try {
								if (getLatLong(getLocationInfo(java.net.URLEncoder
										.encode(auto_places.getText()
												.toString(), "UTF-8").replace(
												"+", "%20")))) {
									/* Location is available */
									myLocation = new LatLng(currentLat,
											currentLong);
									CameraPosition cameraPosition = new CameraPosition.Builder()
											.target(myLocation).zoom(16)
											.build();
									map.animateCamera(CameraUpdateFactory
											.newCameraPosition(cameraPosition));
									firstTime = false;

									new StringFromLocation().execute();

								} else {
									/* Location entered not available */
									Toast.makeText(getActivity(),
											"Location not found!",
											Toast.LENGTH_SHORT).show();
								}
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						}
					}
					return false;
				}
			});
		}
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
			Log.e(TAG, "Ride Later Selected");
			Intent intent = new Intent(getActivity(), RideActivity.class);
			mRideTimeSelected = TaxiRideTimeSelectionAction.LATER;
			Bundle bundle = new Bundle();
			bundle.putSerializable("rideTimeSelected", mRideTimeSelected);
			bundle.putSerializable("taxiSelected", mTaxiSelected);
			bundle.putInt("fragment", R.layout.fragment_ride_later);
			intent.putExtras(bundle);
			toggleVisibility();
			startActivity(intent);
			break;
		case R.id.ivMyLocation:
			/* Animate the camera to point to the current location */
			Location currentLocation = map.getMyLocation();
			myLocation = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myLocation).zoom(16).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			currentLat = currentLocation.getLatitude();
			currentLong = currentLocation.getLongitude();
			new StringFromLocation().execute();
			break;
		case R.id.taxiTimeChoiceNow:
			Log.e(TAG, "Ride Now Selected");
			intent = new Intent(getActivity(), RideActivity.class);
			mRideTimeSelected = TaxiRideTimeSelectionAction.NOW;
			bundle = new Bundle();
			bundle.putSerializable("rideTimeSelected", mRideTimeSelected);
			bundle.putSerializable("taxiSelected", mTaxiSelected);
			bundle.putInt("fragment", R.layout.fragment_ride_now);
			intent.putExtras(bundle);
			toggleVisibility();
			startActivity(intent);
			break;
			
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
			auto_places.setAdapter(adapter);
		}
	}

	/** A class to get the name of the location from latitude and longitude */
	private class StringFromLocation extends AsyncTask<String, String, String> {

		String display = "";

		protected String doInBackground(String... LatLong) {

			String address = String
					.format(Locale.ENGLISH,
							"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
									+ Locale.getDefault().getCountry(),
							currentLat, currentLong);
			HttpGet httpGet = new HttpGet(address);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			StringBuilder stringBuilder = new StringBuilder();

			List<Address> retList = null;

			try {
				response = client.execute(httpGet);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			HttpEntity entity = response.getEntity();
			InputStream stream = null;
			try {
				stream = entity.getContent();
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}

			int b;
			try {
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = new JSONObject(stringBuilder.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			retList = new ArrayList<Address>();

			try {
				if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
					JSONArray results = jsonObject.getJSONArray("results");
					for (int i = 0; i < results.length(); i++) {
						JSONObject result = results.getJSONObject(i);
						String indiStr = result.getString("formatted_address");
						Address addr = new Address(Locale.getDefault());
						addr.setAddressLine(0, indiStr);
						retList.add(addr);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (retList.size() > 0) {
				for (int i = 0; i <= retList.get(0).getMaxAddressLineIndex(); i++) {
					display += retList.get(0).getAddressLine(i) + "\n";
				}
				if (display.contentEquals("")) {
					display += retList.get(0).getSubAdminArea() + " "
							+ retList.get(0).getLocality() + ", "
							+ retList.get(0).getAdminArea();
				}
			}
			return display;
		}

		@Override
		protected void onPostExecute(String display) {
			auto_places.setText(display);

			/* Drag the marker to current location */
			myLocation = new LatLng(currentLat, currentLong);
			map.clear();
			map.addMarker(new MarkerOptions().position(myLocation)
					.icon(BitmapDescriptorFactory.defaultMarker())
					.draggable(true));
		}
	}

	/* API to get Latitude and longitude from address */
	public JSONObject getLocationInfo(String address) {
		StringBuilder stringBuilder = new StringBuilder();
		try {

			address = address.replaceAll(" ", "%20");

			HttpPost httppost = new HttpPost(
					"http://maps.google.com/maps/api/geocode/json?address="
							+ address + "&sensor=false");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();

			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	public boolean getLatLong(JSONObject jsonObject) {
		try {
			currentLong = ((JSONArray) jsonObject.get("results"))
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lng");

			currentLat = ((JSONArray) jsonObject.get("results"))
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lat");

		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}