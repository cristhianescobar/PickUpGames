package com.example.pickup;

import java.util.List;
import com.parse.ParseQuery;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
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
	
	
	@Override 
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle bundle){
		view = inflator.inflate(R.layout.list_games,container, false);
		
		mAdapter = new GameArrayAdapter(MainActivity.mContext.getApplicationContext());
        
        mListType = getArguments().getInt("listType");
		new getEvents().execute();
		
		return view;
	}

	 @Override
	    public void onListItemClick(ListView l, View v, int position, long id)
	    {
	    	Game event = (Game) mAdapter.getItem(position);
	    	
	    	Bundle bundle = new Bundle();
			bundle.putString("id", event.getId());
			Fragment gameFragment = new GameDetail();
			gameFragment.setArguments(bundle);
			
			FragmentManager fragmentManager = getFragmentManager();
	        fragmentManager.beginTransaction()
	                .replace(R.id.container, gameFragment)
	                .commit();
	    }
	    
	    private class getEvents extends AsyncTask<Void, Void, Void>
	    {
			@Override
			protected Void doInBackground(Void... params)
			{
		        try
		        {
					ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
			        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
			        
					List<Game> results;
					switch (mListType)
					{
						case MainActivity.NEARBY:
							results = query.find(); 
					        break;
						case MainActivity.UPCOMING:
							results = query.find(); 
							break;
						case MainActivity.MYGAMES:
							results = query.find(); 
							break;
						default:
							results = query.find(); 
							break;
					}
					
					for (Game event : results)
		            {
		                Log.d("ListEvent", "Found event: id = " + event.getId());
		            	mAdapter.add(event);
		            }
				}
		        catch (Exception e)
		        {
		        	Log.e("ListEvents", "Get Events: " + e.toString());
					Toast.makeText(MainActivity.mContext.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				return null;
			}
			
			protected void onPostExecute (Void result)
			{
				setListAdapter(mAdapter);
			}
	    }
	} 



