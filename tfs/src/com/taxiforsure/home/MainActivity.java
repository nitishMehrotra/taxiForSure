package com.taxiforsure.home;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.taxiforsure.R;
import com.taxiforsure.home.FragmentStatus.FragementVisibility;
import com.taxiforsure.home.FragmentStatus.FragmentName;
import com.taxiforsure.home.UserAction.TaxiRideTimeSelectionAction;
import com.taxiforsure.home.UserAction.TaxiSelectionAction;

public class MainActivity extends ActionBarActivity {
	private DrawerLayout drawerLayout;
	private ListView listView;
	private ActionBarDrawerToggle actionBarDrawerToggle;

	private Toolbar toolbar;
	private FragmentName mCurrentFragment;
	private String[] navigationDrawerItems;
	private FragementVisibility mFragementVisibility = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		navigationDrawerItems = getResources().getStringArray(
				R.array.navigation_drawer_items);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		listView = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		listView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, navigationDrawerItems));
		listView.setOnItemClickListener(new DrawerItemClickListener());

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				toolbar, R.string.app_name, R.string.app_name);
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		switch (position) {
		case 0:
			HomeFragment fragment = new HomeFragment();
			mFragementVisibility = fragment;
			Bundle args = new Bundle();
			args.putInt(HomeFragment.ARG_MENU_INDEX, position);
			fragment.setArguments(args);
			mCurrentFragment = FragmentName.HOME_FRAGMENT;

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.home_frame, fragment).commit();
			break;
		case 1:
			Intent intent = new Intent(getApplicationContext(), MyRidesActivity.class);
			startActivity(intent);
			
			break;
		default:
			Toast.makeText(getApplicationContext(), "UnderCostruction", Toast.LENGTH_SHORT).show();
		}
		// update selected item and title, then close the drawer
		listView.setItemChecked(position, true);
		setTitle(navigationDrawerItems[position]);
		drawerLayout.closeDrawer(listView);
	}

	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if (mCurrentFragment == FragmentName.HOME_FRAGMENT) {
			if (HomeFragment.getTaxiSelected() != TaxiSelectionAction.NONE
					&& HomeFragment.getRideTimeSelected() == TaxiRideTimeSelectionAction.NONE
					&& mFragementVisibility != null) {
				mFragementVisibility.toggleVisibility();
			} else {
				super.onBackPressed();
			}
		}
	}
}