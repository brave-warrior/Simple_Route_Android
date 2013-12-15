package com.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.khmelenko.lab.simpleroute.R;
import com.location.LocationEngine;
import com.location.LocationObserver;
import com.network.NetworkEngine;
import com.network.PacketParser;
import com.network.ResponseStatus;
import com.store.AppSettings;
import com.store.City;
import com.store.DbEngine;
import com.store.Route;

/**
 * Application main screen
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class MainScreen extends Activity implements LocationObserver {

	// editors
	private AutoCompleteTextView iDepartureEditor;
	private AutoCompleteTextView iArrivalEditor;
	private Button iSearchBtn;
	private RadioGroup iTravelMode;

	private SearchDirectionAsyncTask iSearchTask;

	private ProgressDialog iProgressDialog;
	
	private static final String MODE_DRIVING = "driving";
	private static final String MODE_WALKING = "walking";
	private static final String MODE_BICYCLING = "bicycling";

	/** Location retrieval */
	private LocationEngine iLocationEngine;
	
	/*
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		// init departure editor
		iDepartureEditor = (AutoCompleteTextView) findViewById(R.id.main_departure_edit);
		iDepartureEditor.setAdapter(new CustomCompleteAdapter(this,
				android.R.layout.simple_dropdown_item_1line));

		// init arrival editor
		iArrivalEditor = (AutoCompleteTextView) findViewById(R.id.main_arrival_edit);
		iArrivalEditor.setAdapter(new CustomCompleteAdapter(this,
				android.R.layout.simple_dropdown_item_1line));
		
		// init travel mode selector
		iTravelMode = (RadioGroup) findViewById(R.id.main_travel_mode);

		// init search button
		initSearchBtn();

		iProgressDialog = new ProgressDialog(this);
		iProgressDialog.setMessage(getString(R.string.main_searching_progress));
		iProgressDialog.setOnCancelListener(new Dialog.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface aDialog) {
				if (iSearchTask != null) {
					iSearchTask.cancel(true);
				}
			}
		});

		iLocationEngine = new LocationEngine(getApplicationContext(), this);
	}
	
	/**
	 * Initializes button Search
	 */
	private void initSearchBtn() {
		iSearchBtn = (Button) findViewById(R.id.search_btn);
		iSearchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View aV) {
				String departure = prepareAddress(iDepartureEditor);
				String arrival = prepareAddress(iArrivalEditor);

				// getting travel mode
				int travelModeId = iTravelMode.getCheckedRadioButtonId();
				String travelMode = travelModeToString(travelModeId);
				
				if (!departure.isEmpty() && !arrival.isEmpty()) {
					// show progress dialog
					if (!iProgressDialog.isShowing()) {
						iProgressDialog.show();
					}

					// cancel previous task if exists
					if (iSearchTask != null) {
						iSearchTask.cancel(true);
					}
					
					// start new search task
					iSearchTask = new SearchDirectionAsyncTask(departure,
							arrival, travelMode);
					iSearchTask.execute();
				} else {
					notifyAddressNotSet();
				}
			}
		});
	}
	
	/**
	 * Prepares address for request
	 * 
	 * @param aControl
	 *            Control with address
	 * @return Prepared address
	 */
	private String prepareAddress(AutoCompleteTextView aControl) {
		String address = aControl.getText().toString();
		// if address is empty, use current location
		if (address.isEmpty()) {
			address = getFormattedLocation(iLocationEngine.getLastLocation());
		}

		return address;
	}
	
	/**
	 * Formats the location to the request
	 * 
	 * @param aLocation
	 *            Location object
	 * @return Formatted string with location
	 */
	private String getFormattedLocation(Location aLocation) {
		StringBuilder result = new StringBuilder();

		if (aLocation != null) {
			result.append(aLocation.getLatitude());
			result.append(','); // comma is separator
			result.append(aLocation.getLongitude());
		}

		return result.length() > 0 ? result.toString() : "";
	}
	
	/**
	 * Notifies that departure address or arrival address is not set
	 */
	private void notifyAddressNotSet() {
		String msg = getResources().getString(R.string.error_address_not_set);
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	/*
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		iLocationEngine.retrieveLocation();
	}
	
	/*
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		iLocationEngine.stopRetrieving();
	}

	/*
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem aItem) {
		switch (aItem.getItemId()) {
		case R.id.action_about:
			startActivity(new Intent(MainScreen.this, AboutScreen.class));
			return true;
		case R.id.action_terms: {
			Uri uri = Uri.parse("http://www.google.com/intl/en/policies/terms/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
			return true;
		case R.id.action_policy: {
			Uri uri = Uri.parse("http://www.google.com/policies/privacy/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
			return true;
		}

		return super.onOptionsItemSelected(aItem);
	}

	/**
	 * Converts travel mode to the String representation
	 * 
	 * @param aTravelModeId
	 *            Travel mode
	 * @return String travel mode
	 */
	private String travelModeToString(int aTravelModeId) {
		String mode = MODE_DRIVING;
		switch (aTravelModeId) {
		case R.id.main_mode_driving:
			mode = MODE_DRIVING;
			break;
		case R.id.main_mode_walking:
			mode = MODE_WALKING;
			break;
		case R.id.main_mode_bicycling:
			mode = MODE_BICYCLING;
			break;
		}
		return mode;
	}

	/**
	 * Custom adapter for the {@link AutoCompleteTextView}
	 * 
	 * @author Dmytro Khmelenko
	 * 
	 */
	private class CustomCompleteAdapter extends ArrayAdapter<String> implements
			Filterable {

		/** List of results */
		private List<String> iResultList;

		/**
		 * Constructor
		 * 
		 * @param aContext
		 *            Context
		 * @param aTextViewResourceId
		 */
		public CustomCompleteAdapter(Context aContext, int aTextViewResourceId) {
			super(aContext, aTextViewResourceId);
		}

		@Override
		public int getCount() {
			return iResultList.size();
		}

		@Override
		public String getItem(int aIndex) {
			return iResultList.get(aIndex);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(
						CharSequence aConstraint) {
					FilterResults filterResults = new FilterResults();
					boolean networkAvailable = NetworkEngine
							.isNetworkAvailable(getApplicationContext());
					if (aConstraint != null && networkAvailable) {

						// retrieve API key
						String apiKey = AppSettings
								.getApiKey(getApplicationContext());

						// make request and receive the response
						NetworkEngine network = new NetworkEngine();
						String response = network.requestCities(
								aConstraint.toString(), apiKey);

						// response status is ignored. Empty list will be,
						// if an error occurred

						// parse the response
						List<City> cities = PacketParser.parseCities(response);

						// create the list of the names of the positions
						List<String> list = new ArrayList<String>();
						for (City city : cities) {
							list.add(city.getDescription());
						}

						iResultList = list;
						// Assign the data to the FilterResults
						filterResults.values = iResultList;
						filterResults.count = iResultList.size();
					}

					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence aConstraint,
						FilterResults aResults) {
					if (aResults != null && aResults.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}

				}
			};
			return filter;
		}
	}

	/**
	 * Web async task for making HTTP requests in background
	 * 
	 * @author Dmytro Khmelenko
	 * 
	 */
	private class SearchDirectionAsyncTask extends
			AsyncTask<URL, Void, ResponseStatus> {

		/** Origin place */
		private String iOrigin;
		/** Destination place */
		private String iDest;
		/** Travel mode: e.g. driving, walking etc. */
		private String iTravelMode;

		/**
		 * Constructor
		 * 
		 * @param aOrigin
		 *            Origin place
		 * @param aDest
		 *            Destination place
		 */
		public SearchDirectionAsyncTask(String aOrigin, String aDest,
				String aTravelMode) {
			iOrigin = aOrigin;
			iDest = aDest;
			iTravelMode = aTravelMode;
		}

		/*
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ResponseStatus doInBackground(URL... aUrl) {
			ResponseStatus status = null;
			if (NetworkEngine.isNetworkAvailable(getApplicationContext())) {
				NetworkEngine network = new NetworkEngine();
				String result = network.requestDirections(iOrigin, iDest,
						iTravelMode);

				// parse the response
				if (result != null) {
					status = handleResponse(result);
				}
			}
			return status;
		}

		/*
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ResponseStatus aResponse) {
			super.onPostExecute(aResponse);

			// if response is empty, connection error occurred
			if (aResponse == null) {
				notifyConnectionFailed();
			} else if (!aResponse.isSuccess()) {
				notifyRequestFailed(aResponse);
			} else {
				showRoutesOnMap();
			}

			// hide progress dialog
			iProgressDialog.dismiss();
		}
	}

	/**
	 * Notifies that the connection is failed
	 */
	private void notifyConnectionFailed() {
		String notification = getResources().getString(
				R.string.error_connection_failed);
		Toast.makeText(this, notification, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Handles server response
	 * 
	 * @param aResponse
	 *            Response
	 * @return Response status
	 */
	private ResponseStatus handleResponse(String aResponse) {
		ResponseStatus status = PacketParser.parseStatus(aResponse);
		if (status.isSuccess()) {
			List<Route> routes = PacketParser.parseRoutes(aResponse);
			DbEngine db = new DbEngine(MainScreen.this);

			// clear previous routes
			db.deleteAll();

			// Store the routes to the DB
			for (Route route : routes) {
				db.insertRoute(route);
			}
			db.close();
		}

		return status;
	}

	/**
	 * Notifies about failed request
	 * 
	 * @param aStatus
	 *            Response status
	 */
	private void notifyRequestFailed(ResponseStatus aStatus) {
		String errorMsg = getResources().getString(aStatus.getStatus());
		Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
	}

	/**
	 * Shows the routes on the map
	 */
	private void showRoutesOnMap() {
		startActivity(new Intent(MainScreen.this, MapScreen.class));
	}

	/*
	 * @see com.location.LocationObserver#locationUpdated(android.location.Location)
	 */
	@Override
	public void locationUpdated(Location aLocation) {
		if (aLocation != null) {
			iLocationEngine.stopRetrieving();
		}
	}

}
