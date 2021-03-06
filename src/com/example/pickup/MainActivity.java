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
    public final static int NEARBY = 0;
    public final static int UPCOMING = 1;
    public final static int MYGAMES = 2;
    

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static boolean parseInit = false;
    public static Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        
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
    	Bundle bundle = new Bundle();
    	
    	if (ParseUser.getCurrentUser() != null)
    	{
    		// Signed In
    		switch (position)
    		{
    		case 0:
    			mTitle = getString(R.string.app_name);
    			frag = new Home();
    			break;
    		case 1:
                mTitle = getString(R.string.nearby_games);
    			bundle.putInt("listType", NEARBY);
    			frag = new ListGames();
    			frag.setArguments(bundle);
    			break;
    		case 2:
                mTitle = getString(R.string.upcoming_games);
    			bundle.putInt("listType", UPCOMING);
    			frag = new ListGames();
    			frag.setArguments(bundle);
    			break;
    		case 3:
                mTitle = getString(R.string.my_games);
    			bundle.putInt("listType", MYGAMES);
    			frag = new ListGames();
    			frag.setArguments(bundle);
    			break;
    		case 4:
                mTitle = getString(R.string.new_game);
                Intent intent = new Intent(mContext, AddGame.class);
				startActivity(intent);
    			return;
    		case 5:
                mTitle = getString(R.string.sign_out);
                signOut(null);
                return;
    		case 6:
//                mTitle = getString(R.string.settings);
//    			frag = new Settings();
    			return;
    		case 7:
                mTitle = getString(R.string.about);
    			frag = new About();
    			break;
    		}
    	}
    	else
    	{
    		// Not Signed In
			switch(position)
			{
    		case 0:
    			mTitle = getString(R.string.app_name);
    			frag = new Home();
    			break;
			case 1:
                mTitle = getString(R.string.nearby_games);
    			bundle.putInt("listType", NEARBY);
    			frag = new ListGames();
    			frag.setArguments(bundle);
    			break;
    		case 2:
                mTitle = getString(R.string.upcoming_games);
    			bundle.putInt("listType", UPCOMING);
    			frag = new ListGames();
    			frag.setArguments(bundle);
    			break;
    		case 3:
                mTitle = getString(R.string.sign_in);
    			frag = new SignIn();
    			break;
    		case 4:
                mTitle = getString(R.string.sign_up);
    			frag = new SignUp();
    			break;
    		case 5:
//                mTitle = getString(R.string.settings);
//    			frag = new Settings();
    			return;
    		case 6:
                mTitle = getString(R.string.about);
    			frag = new About();
    			break;

			}
    	}
    	
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .commit();
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
