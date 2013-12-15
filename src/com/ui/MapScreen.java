/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.khmelenko.lab.simpleroute.R;
import com.network.PacketParser;
import com.store.DbEngine;
import com.store.Route;
import com.store.RouteBounds;
import com.store.RouteDetails;

/**
 * Used for showing maps
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class MapScreen extends Activity {

	private GoogleMap iMap;

	/** Shows route copyrights */
	private TextView iCopyrights;
	/** Shows route warnings */
	private TextView iWarnings;

	/*
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_screen);

		iCopyrights = (TextView) findViewById(R.id.route_copyrights);
		iWarnings = (TextView) findViewById(R.id.route_warnings);

		initMapControl();

		// parse the route from the DB
		DbEngine db = new DbEngine(MapScreen.this);
		final List<Route> routes = db.getAllRoutes();
		db.close();

		List<RouteBounds> bounds = new ArrayList<RouteBounds>();
		// show the routes on the map
		for (Route route : routes) {
			drawRoute(route);
			showRouteMarkers(route);
			showRouteDetails(route.iDetails);
			bounds.add(route.iBounds);
		}

		// prepare maps bounds for zoom
		final CameraUpdate update = prepareMapBounds(bounds);

		// zoom the map when it's already loaded
		iMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				iMap.animateCamera(update);
			}
		});

	}
	
	/*
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_menu, menu);
		return true;
	}
	
	/*
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem aItem) {
		switch (aItem.getItemId()) {
		case R.id.route_details:
			startActivity(new Intent(MapScreen.this, RouteDetailsScreen.class));
			return true;
		}
		return super.onOptionsItemSelected(aItem);
	}

	/**
	 * Initializes map control
	 */
	private void initMapControl() {
		MapFragment fragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		iMap = fragment.getMap();

		iMap.setMyLocationEnabled(true);

		UiSettings settings = iMap.getUiSettings();
		settings.setMyLocationButtonEnabled(true);
		settings.setCompassEnabled(true);
	}

	/**
	 * Draws the route on the map
	 * 
	 * @param aRoute
	 *            Route for drawing
	 */
	private void drawRoute(Route aRoute) {

		// decode polyline
		String encodedPolyline = aRoute.iEncodedPolyline;
		List<LatLng> points = PacketParser.decodePoly(encodedPolyline);

		// adding all points to the line
		PolylineOptions rectOptions = new PolylineOptions();
		for (LatLng point : points) {
			rectOptions.add(point);
		}

		// draw the line on the map
		Polyline polyline = iMap.addPolyline(rectOptions);
		polyline.setWidth(4.0f);
		polyline.setColor(Color.RED);
	}

	/**
	 * Shows the route markers
	 * 
	 * @param aRoute
	 *            Route
	 */
	private void showRouteMarkers(Route aRoute) {
		Location start = aRoute.iStartLocation;

		// create start marker
		MarkerOptions startMarker = new MarkerOptions();
		startMarker.position(new LatLng(start.getLatitude(), start
				.getLongitude()));
		startMarker.title(aRoute.iStartAddress);

		// adding marker
		iMap.addMarker(startMarker);

		Location end = aRoute.iEndLocation;

		MarkerOptions endMarker = new MarkerOptions();
		endMarker.position(new LatLng(end.getLatitude(), end.getLongitude()));
		endMarker.title(aRoute.iEndAddress);

		// end marker has another icon
		endMarker.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

		// adding marker
		iMap.addMarker(endMarker);
	}

	/**
	 * Prepares the bounds for scaling the map
	 * 
	 * @param aBounds
	 *            List of bounds
	 * @return Camera update
	 */
	private CameraUpdate prepareMapBounds(List<RouteBounds> aBounds) {
		LatLngBounds.Builder bounds = new LatLngBounds.Builder();

		// add each bound to the bounds builder
		for (RouteBounds bound : aBounds) {
			Location northEast = bound.getNorthEast();
			Location southWest = bound.getSouthWest();

			LatLng tl = new LatLng(northEast.getLatitude(),
					northEast.getLongitude());
			LatLng br = new LatLng(southWest.getLatitude(),
					southWest.getLongitude());

			bounds.include(tl).include(br);
		}

		int padding = 35; // offset from edges of the map in pixels
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(
				bounds.build(), padding);

		return cameraUpdate;
	}

	/**
	 * Shows route details
	 * 
	 * @param aDetails
	 *            Route details
	 */
	private void showRouteDetails(RouteDetails aDetails) {
		iCopyrights.setText(aDetails.getCopyrights());
		iWarnings.setText(aDetails.getWarnings());
	}
}
