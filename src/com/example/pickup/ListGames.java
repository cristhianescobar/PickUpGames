package com.example.pickup;

import java.util.Date;
import java.util.List;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class ListGames extends ListFragment {
	
	private View view;
	private static GameArrayAdapter mAdapter;
	private int mListType;
	private ProgressDialog mDialog;
	
	
	@Override 
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle bundle){
		view = inflator.inflate(R.layout.list_games,container, false);
		
		// Start Dialog
		mDialog = new ProgressDialog(MainActivity.mContext);
		mDialog.setMessage("Finding Games...");
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.show();
		
		mAdapter = new GameArrayAdapter(MainActivity.mContext.getApplicationContext());
        
        mListType = getArguments().getInt("listType");
		new getEvents().execute();
		
		return view;
	}

	 @Override
	    public void onListItemClick(ListView l, View v, int position, long id)
	    {
		 	Game event = (Game) mAdapter.getItem(position);
	    	
	    	Intent intent = new Intent(MainActivity.mContext, GameDetail.class);
			intent.putExtra("id", event.getId());
			
			startActivity(intent);
	    }
	    
	    private class getEvents extends AsyncTask<Void, Void, Void>
	    {
	    	String msg = "";
			@Override
			protected Void doInBackground(Void... params)
			{
		        try
		        {
					ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
			        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
			        
					switch (mListType)
					{
						case MainActivity.NEARBY:
							
							LocationManager locMgr = ((LocationManager) MainActivity.mContext.getSystemService(MainActivity.mContext.LOCATION_SERVICE));
					        Location location = locMgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
							ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
					        
							query.whereWithinMiles("location", point, 10.0);
					        break;
						case MainActivity.UPCOMING:
							Date date = new Date();
							query.whereGreaterThanOrEqualTo("date", date);
							query.orderByAscending("date");
							break;
						case MainActivity.MYGAMES:
							query.whereEqualTo("host", ParseUser.getCurrentUser());
							break;
					}
					
					List<Game> results = query.find(); 
					for (Game event : results)
		            {
		                Log.d("ListEvent", "Found event: id = " + event.getId());
		            	mAdapter.add(event);
		            }
				}
		        catch (Exception e)
		        {
		        	Log.e("ListEvents", "Get Events: " + e.toString());
		        	msg = e.getMessage();
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
					setListAdapter(mAdapter);
				}
				else
				{
					Toast.makeText(MainActivity.mContext, msg, Toast.LENGTH_SHORT).show();
				}
			}
	    }
	} 



