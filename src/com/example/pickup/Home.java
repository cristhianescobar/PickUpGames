package com.example.pickup;

import com.parse.ParseUser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Home extends Fragment {

	private View view;
	
	@Override 
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle bundle){
		view = inflator.inflate(R.layout.home,container, false);
		TextView welcome = ((TextView) view.findViewById(R.id.welcome));
        
        
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
        {
            welcome.setText("Welcome guest!");
            
        }
        else
        {
            welcome.setText("Welcome " + currentUser.getUsername() + "!");
            
        }  
		return view;
	}



}
