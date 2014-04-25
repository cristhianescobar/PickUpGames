package com.example.pickup;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddGame extends Fragment {

	private static ParseUser mUser;
	private static Game mEvent;
	private static LatLng mLocation;
	private static ProgressDialog mDialog;
	private View view;
	private GoogleMap map;
	
	@Override 
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle bundle){
		view = inflator.inflate(R.layout.add_game,container, false);
		view.findViewById(R.id.submit_button).setOnClickListener((OnClickListener) submit);
	
		mUser = ParseUser.getCurrentUser();
		if (mUser == null)
		{
			Log.wtf("AddEvent", "Must be logged in to add event!");
			assert(false);
		}
		
		mEvent = new Game();
		
		// Initial map setup
		try
		{
			// Get current location
			LocationManager locMgr = ((LocationManager) MainActivity.mContext.getSystemService(MainActivity.mContext.LOCATION_SERVICE));
	        Location location = locMgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	        LatLng address = new LatLng(location.getLatitude(), location.getLongitude());
	        
	        // Setup map
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.location_map)).getMap();
			map.getUiSettings().setScrollGesturesEnabled(false);
			map.addMarker(new MarkerOptions().position(address));
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 16));
	        
	        mLocation = address;
		}
		catch (Exception e)
		{
			Log.e("AddEvent", "Display Map: " + e.toString());
			Toast.makeText(MainActivity.mContext, "Error loading maps.", Toast.LENGTH_SHORT).show();
			
			mLocation = new LatLng(0, 0);
		}
		
		// Set min date and time
		DatePicker date = (DatePicker) view.findViewById(R.id.date_value);
		date.setMinDate(System.currentTimeMillis() - 60000);
		
		// Create an TextChangedListener and OnFocusChange to update map
		EditText location = (EditText) view.findViewById(R.id.location_value);
		location.addTextChangedListener(new TextWatcher()
		{
			// Calls Update Map After Each Space
			@Override
			public void afterTextChanged(Editable s)
			{
				if (s.length() > 0 && s.charAt(s.length() - 1) == ' ')
	        	{
					updateMap();
	        	}
	        }

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		location.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			// Calls Update Map on Focus Loss
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					updateMap();
				}
			}
		});
		
		// Hide keyboard
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		return view;
	}


	

	// Updates the map
	private void updateMap()
	{
		try
		{
    		EditText location = (EditText) view.findViewById(R.id.location_value);
    		Geocoder geocoder = new Geocoder(MainActivity.mContext, Locale.US);
    		Address address = geocoder.getFromLocationName(location.getText().toString(), 1).get(0);
    		mLocation = new LatLng(address.getLatitude(), address.getLongitude());

    		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.location_map)).getMap();
    		map.clear();
    		map.addMarker(new MarkerOptions().position(mLocation));
    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 16));
        }
		catch (IndexOutOfBoundsException e)
		{
			Log.e("AddEvent", "No Valid Address Found");
		}
		catch (IOException e)
		{
        	Log.e("AddEvent", "TextChangeListener: " + e.toString());
			Toast.makeText(MainActivity.mContext, "Error updating maps.", Toast.LENGTH_SHORT).show();
		}
	}
	

	View.OnClickListener submit = new View.OnClickListener()
	{	
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v)
		{
			TextView name_label = (TextView) view.findViewById(R.id.name_label);
			TextView sport_label = (TextView) view.findViewById(R.id.sport_label);
			EditText name_value = (EditText) view.findViewById(R.id.name_value);
			EditText sport_value = (EditText) view.findViewById(R.id.sport_value);
			EditText details_value = (EditText) view.findViewById(R.id.details_value);
			DatePicker date = (DatePicker) view.findViewById(R.id.date_value);
			TimePicker time = (TimePicker) view.findViewById(R.id.time_value);
			Button submit = ((Button) view.findViewById(R.id.submit_button));
			
			try
			{	
				// Hide keyboard
				InputMethodManager imm = (InputMethodManager) MainActivity.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
				
				// Set label to black
				name_label.setTextColor(Color.BLACK);
				sport_label.setTextColor(Color.BLACK);
				
				// Name
				mEvent.setName(name_value.getText().toString());
				
				// Date/Time
				mEvent.setDate(new Date(date.getYear() - 1900,
									   date.getMonth(),
									   date.getDayOfMonth(), 
						               time.getCurrentHour(),
						               time.getCurrentMinute()));
				
				// Sport
				mEvent.setSport(sport_value.getText().toString());
				
				// Location
				mEvent.setLocation(mLocation);
				
				// Details
				mEvent.setDetails(details_value.getText().toString());
				
				// Host
				mEvent.setHost(mUser);
				
				// Set Submit Button to unclickable
				submit.setClickable(false);
				
				// Start Dialog
				mDialog = new ProgressDialog(MainActivity.mContext);
				mDialog.setMessage("Creating Event...");
				mDialog.setIndeterminate(true);
				mDialog.setCancelable(false);
				mDialog.show();
				
				// Run Save in AsyncTask
				new saveEvent().execute();
			} 
			catch (IllegalArgumentException e)
			{
				if (sport_value.getText().toString().isEmpty())
				{
					sport_label.setTextColor(Color.RED);
					sport_value.requestFocus();
				}
				
				if (name_value.getText().toString().isEmpty())
				{
					name_label.setTextColor(Color.RED);
					name_value.requestFocus();
				}
				
				Log.e("AddEvent", "Submit: " + e.toString());
				Toast.makeText(MainActivity.mContext, "Fill all required fields.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	

	
	// Saves Event in AsyncTask
	private class saveEvent extends AsyncTask<Void, Void, Void>
    {
		String msg = "";
		@Override
		protected Void doInBackground(Void... params)
		{
	        try
	        {
				mEvent.save();
			}
	        catch (ParseException e)
	        {
	        	Log.e("SignIn", "SignIn: " + e.toString());
	        	switch (e.getCode())
	        	{
	        		case 100:
	        			msg = "Check network connection.";
	        			break;
	        		default:
	        			msg = "An unknown problem has occured (" + e.getCode() + ").";
	        			break;
	        	}
			}
			return null;
		}
		
		protected void onPostExecute (Void result)
		{
			if (mDialog != null)
			{
				mDialog.dismiss();
			}
			
			if (msg.isEmpty())
			{
				Toast.makeText(MainActivity.mContext, "Event created.", Toast.LENGTH_SHORT).show();
				

				Bundle bundle = new Bundle();
				bundle.putString("id", mEvent.getId());
				Fragment gameFragment = new GameDetail();
				gameFragment.setArguments(bundle);
				
				FragmentManager fragmentManager = getFragmentManager();
		        fragmentManager.beginTransaction()
		        		.addToBackStack(null)
		                .replace(R.id.container, gameFragment)
		                .commit();
		        
		        getActivity().getFragmentManager().popBackStack();
				
				
			}
			else
			{
				Toast.makeText(MainActivity.mContext, msg, Toast.LENGTH_SHORT).show();
				
				// Set button clickable
	        	Button submit = ((Button) view.findViewById(R.id.submit_button));
				submit.setClickable(true);
			}
			
		}
    }
}

