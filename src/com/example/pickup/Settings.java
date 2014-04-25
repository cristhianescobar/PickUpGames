package com.example.pickup;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends Fragment {

	@Override 
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle bundle){
		return inflator.inflate(R.layout.settings,container, false);
	}
}
