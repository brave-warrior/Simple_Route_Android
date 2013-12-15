package com.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.khmelenko.lab.simpleroute.R;
import com.store.DbEngine;
import com.store.Route;
import com.store.RouteStep;
import com.utils.DateTimeUtils;
import com.utils.MetricUtils;

public class RouteDetailsScreen extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter iSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager iViewPager;

	private List<Route> iRoutes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_details_screen);

		DbEngine dbEngine = new DbEngine(getApplicationContext());
		iRoutes = dbEngine.getAllRoutes();
		dbEngine.close();

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		iSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		iViewPager = (ViewPager) findViewById(R.id.pager);
		iViewPager.setAdapter(iSectionsPagerAdapter);
	}

	/**
	 * Returns a one fragment from the sections
	 * 
	 * @author Dmytro Khmelenko
	 * 
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		/**
		 * Constructor
		 * 
		 * @param aFm
		 *            Fragment manager
		 */
		public SectionsPagerAdapter(FragmentManager aFm) {
			super(aFm);
		}

		@Override
		public Fragment getItem(int aPosition) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, aPosition);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return iRoutes.size();
		}

		@Override
		public CharSequence getPageTitle(int aPosition) {
			Route route = iRoutes.get(aPosition);
			String routeName = route.iDetails.getSummary();
			if (routeName == null || routeName.isEmpty()) {
				routeName = getResources().getString(R.string.route_no_name);
			}

			String title = getResources().getString(R.string.route_name,
					routeName);
			return title;
		}
	}

	/**
	 * Represents a section of the fragment
	 * 
	 * @author Dmytro Khmelenko
	 * 
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Constructor
		 */
		public DummySectionFragment() {
		}

		/*
		 * @see
		 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater
		 * , android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater aInflater,
				ViewGroup aContainer, Bundle aSavedInstanceState) {
			View rootView = aInflater.inflate(
					R.layout.route_details_screen_fragment, aContainer, false);

			int position = getArguments().getInt(ARG_SECTION_NUMBER);

			// retrieving routes from DB
			DbEngine dbEngine = new DbEngine(getActivity()
					.getApplicationContext());
			List<Route> routes = dbEngine.getAllRoutes();
			dbEngine.close();

			Route activeRoute = routes.get(position);

			// update route info
			TextView routeInfo = (TextView) rootView
					.findViewById(R.id.route_info);
			String dist = MetricUtils.toReadableDist(getActivity(),
					activeRoute.iDistance);
			String duration = DateTimeUtils.toReadableTime(getActivity(),
					activeRoute.iDuration, false);
			String format = getString(R.string.route_info, dist, duration);
			routeInfo.setText(format);

			// getting steps for the route
			List<String> items = prepareList(activeRoute.iSteps);

			// prepare list control
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity(), R.layout.route_list_item,
					R.id.text_list_item, items);

			ListView list = (ListView) rootView.findViewById(R.id.list);
			list.setAdapter(adapter);

			return rootView;
		}

		/**
		 * Prepares the list of steps
		 * 
		 * @param aItems
		 *            List of steps
		 * @return Readable list of steps
		 */
		private List<String> prepareList(List<RouteStep> aItems) {
			List<String> list = new ArrayList<String>();

			for (RouteStep step : aItems) {
				StringBuilder item = new StringBuilder();

				// route instructions
				String instructions = Html.fromHtml(step.getInstructions())
						.toString();
				item.append(instructions);
				item.append('\n');

				// route distance
				String distance = MetricUtils.toReadableDist(getActivity(),
						step.getDistance());
				distance = getString(R.string.route_distance, distance);
				item.append(distance);
				item.append('\n');

				// route duration
				String duration = DateTimeUtils.toReadableTime(getActivity(),
						step.getDuration(), false);
				duration = getString(R.string.route_duration, duration);
				item.append(duration);

				list.add(item.toString());
			}

			return list;
		}
	}

}
