package com.example.pickup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameDetail extends Activity
{
	private static Game mEvent;
	private static ParseUser mUser;
	private static List<ParseObject> mGuests;
	private static Context mContext;
	private static ProgressDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_detail);

		mContext = this;
		
    	mUser = ParseUser.getCurrentUser();
    	
    	Button delete = ((Button) findViewById(R.id.delete_button));
		Button attend = ((Button) findViewById(R.id.attend_button));
		attend.setVisibility(View.GONE);
		delete.setVisibility(View.GONE);
		
		// Start Dialog
		mDialog = new ProgressDialog(mContext);
		mDialog.setMessage("Loading data...");
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.show();
		
		// Load Data in AsyncTask
		new loadData().execute();
	}
	
	private void displayData()
	{
		try
		{
			ParseUser host = mEvent.getHost();
			Date date = mEvent.getDate();
			LatLng location = mEvent.getLocation();
			
			// Display Data
			((TextView) findViewById(R.id.name_value)).setText(mEvent.getName());
			((TextView) findViewById(R.id.date_value)).setText(DateTimeParser.date(date));
			((TextView) findViewById(R.id.time_value)).setText(DateTimeParser.time(date));
			((TextView) findViewById(R.id.sport_value)).setText(mEvent.getSport());
			((TextView) findViewById(R.id.location_value)).setText(parseAddress(location));
			String details = mEvent.getDetails();
			if (details.isEmpty())
			{
				((TextView) findViewById(R.id.details_value)).setText("None");
			}
			else
			{
				((TextView) findViewById(R.id.details_value)).setText(details);
			}
			((TextView) findViewById(R.id.host_value)).setText(host.fetchIfNeeded().getString("name"));
			((TextView) findViewById(R.id.phone_value)).setText(PhoneNumberUtils.formatNumber(host.fetchIfNeeded().getNumber("phone").toString()));
			((TextView) findViewById(R.id.attending_value)).setText(getAttending());
			
			// Setup Map
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.location_map)).getMap();
			map.getUiSettings().setScrollGesturesEnabled(false);
			map.addMarker(new MarkerOptions().position(location));
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
	        
			// Choose which button(s) to show
			Button delete = ((Button) findViewById(R.id.delete_button));
			Button attend = ((Button) findViewById(R.id.attend_button));
			if (mUser != null)
			{
				if (mUser.getObjectId().equals(host.getObjectId()))
				{
					delete.setVisibility(View.VISIBLE);
				}
				else
				{
					boolean attending = false;
					for (ParseObject guest : mGuests)
					{
						if (mUser.getObjectId().equals(guest.getObjectId()))
							attending = true;
					}
					if (!attending)
					{
						attend.setVisibility(View.VISIBLE);
					}
				}
			}
		}
		catch (Exception e)
		{
			Log.e("EventDetail", "Display Data: " + e.toString());
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	// Gets a list of the guests' names
	private String getAttending()
	{
		if (mGuests.isEmpty())
		{
			return "None";
		}
		else
		{
			String o = "";
			for (ParseObject user : mGuests)
			{
				o += user.getString("name") + "\n";
			}
			return o.substring(0, o.length() - 1);
		}
	}

	// Gets string address from LatLng
	private String parseAddress(LatLng location)
	{
		try
		{
			Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
			Address address = geocoder.getFromLocation(location.latitude, location.longitude,1).get(0);
			
			String output = address.getAddressLine(0);
			for(int i = 1; i < address.getMaxAddressLineIndex(); ++i)
			{
				output += "\n" + address.getAddressLine(i);
			}
			return output;
		}
		catch (Exception e)
		{
			Log.e("EventDetail", "Parse Address: " + e.toString());
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			return "Address not found\nLat: " + location.latitude + ", Log: " + location.longitude;
		}
	}
	
	// Called by Attend Button
	public void attendEvent(View v)
	{
		if ((mUser == null) || (mUser.getObjectId().equals(mEvent.getHost().getObjectId())))
		{
			Log.wtf("EventDetail", "attendEvent: User is host or null");
			assert(false);
		}
		
		ParseRelation<ParseUser> relation = mEvent.getRelation("guests");
		relation.add(mUser);
		
		// Start Dialog
		mDialog = new ProgressDialog(mContext);
		mDialog.setMessage("Loading data...");
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.show();
		
		Button attend = ((Button) findViewById(R.id.attend_button));
		try
		{
			attend.setVisibility(View.GONE);
			
			mEvent.save();
			
			// Load Data in AsyncTask
			new loadData().execute();
		}
		catch (ParseException e)
		{
			if (mDialog != null)
			{
				mDialog.dismiss();
			}
			
			attend.setVisibility(View.VISIBLE);
			
			Log.e("EventDetail", "loadData: " + e.toString());
			String msg = "";
			switch (e.getCode())
        	{
        		case 100:
        		case 120:
        			msg = "Check network connection.";
        			break;
        		default:
        			msg = "An unknown problem has occured (" + e.getCode() + ").";
        			break;
        	}
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	// Called by Delete Button
	public void deleteEvent(View v)
	{
		mEvent.deleteInBackground();
		Toast.makeText(getApplicationContext(), "Event deleted.", Toast.LENGTH_SHORT).show();
		finish();
	}
	
	// Saves Event in AsyncTask
	private class loadData extends AsyncTask<Void, Void, Void>
    {
		String msg = "";
		@Override
		protected Void doInBackground(Void... params)
		{
	        try
	        {	        	
	        	Intent intent = getIntent();
	    		String objectId = intent.getStringExtra("id");
	    		Log.d("EventDetail", "Details for event " + objectId);
	    		
	        	ParseQuery<Game> eventQuery = ParseQuery.getQuery(Game.class);
	            eventQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
	    		mEvent = (Game) eventQuery.get(objectId);
	    		
	    		ParseQuery<ParseObject> guestQuery = mEvent.getRelation("guests").getQuery();
	    		guestQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
	    		mGuests = guestQuery.find();
			}
	        catch (ParseException e)
	        {
	        	Log.e("EventDetail", "loadData: " + e.toString());
	        	switch (e.getCode())
	        	{
	        		case 100:
	        		case 120:
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
				displayData();
			}
			else
			{
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			}
			
		}
    }
}