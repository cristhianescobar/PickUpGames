package com.example.pickup;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private String CLASS = "MainActivity";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static boolean parseInit = false;
    public static Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        
        Log.d("MainActivity", "Parse init ="+parseInit);
        if (!parseInit)
		{
			Log.d("MainActivity", "Initializing Parse");
	        //Application ID AaxBmFVakIxhS7XajgFg8CveAlMxyX5zifrU00If
	        //Client Key N9lPXcTEmixoJOkVNpxKb6CRpNoNkqP8LUetMvFv

			ParseObject.registerSubclass(Game.class);
	        Parse.initialize(this, "eTNbNXDOXv9MHQ5xMetUjpxMuhrGrF9BkzSPNU9y", "BYuBErNamafUXCMThGNIGr8lzpLS9mALJqp05Q1L");
	        parseInit = true;
		}
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        
        
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new Home())
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
    	Fragment frag = null;
    	switch(position){
    		case 0:
    			frag = new NearByGames();
    			break;
    		case 1:
    			frag = new UpComingGames();
    			break;
    		case 2:
    			frag = new MyGames();
    			break;
    		case 3:
    			frag = new AddGame();
    			break;
    		case 4:
    			frag = new SignIn();
    			break;
    		case 5:
    			frag = new SignUp();
    			break;
    		case 6:
    			frag = null;
    			break;
    		case 7:
    			frag = new Settings();
    			break;
    		case 8:
    			frag = new About();
    			break;
    		
    	}
    	if (frag != null)
    	{
	        FragmentManager fragmentManager = getFragmentManager();
	        fragmentManager.beginTransaction()
	                .replace(R.id.container, frag)
	                .commit();
    	}
    	else
    	{
    		signOut(null);
    	}
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.nearby_games);
                break;
            case 2:
                mTitle = getString(R.string.upcoming_games);
                break;
            case 3:
                mTitle = getString(R.string.my_games);
                break;
            case 4:
                mTitle = getString(R.string.new_game);
                break;
            case 5:
                mTitle = getString(R.string.sign_in);
                break;
            case 6:
                mTitle = getString(R.string.sign_up);
                break;
            case 7:
                mTitle = getString(R.string.sign_out);
                break;
            case 8:
                mTitle = getString(R.string.settings);
                break;
            case 9:
                mTitle = getString(R.string.about);
                break;
            
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
//    TODO: Update the list on the slider menu... Sign should now be an option
	public void signOut (View v)
	{
		Log.d(CLASS, "signing out");
		ParseUser.logOut();
		Log.d(CLASS, "signed out");
//		assert(!checkUser());
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		this.finish();
		
		Log.d(CLASS, "done");
	}
}
