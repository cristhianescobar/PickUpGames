package com.example.pickup;

import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends Fragment {
	
	private View view = null;
	private ParseUser mUser;
	private ProgressDialog mDialog;
	
	@Override 
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle bundle){
		
		view = inflator.inflate(R.layout.sign_up,container, false);
		EditText phone = (EditText) view.findViewById(R.id.phone_value);
		phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		view.findViewById(R.id.submit_button).setOnClickListener((OnClickListener) submit);

		
		return view;
	}
	
	View.OnClickListener submit = new View.OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{
			TextView name_label = (TextView) view.findViewById(R.id.name_label);
			TextView email_label = (TextView) view.findViewById(R.id.email_label);
			TextView phone_label = (TextView) view.findViewById(R.id.phone_label);
			TextView password_label = (TextView) view.findViewById(R.id.password_label);
			TextView confirm_label = (TextView) view.findViewById(R.id.confirm_label);
			EditText name_value = (EditText) view.findViewById(R.id.name_value);
			EditText email_value = (EditText) view.findViewById(R.id.email_value);
			EditText phone_value = (EditText) view.findViewById(R.id.phone_value);
			EditText password_value = (EditText) view.findViewById(R.id.password_value);
			EditText confirm_value = (EditText) view.findViewById(R.id.confirm_value);
			Button submit = (Button) view.findViewById(R.id.submit_button);
			
			try
			{
				// Hide keyboard
				InputMethodManager imm = (InputMethodManager) MainActivity.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
				
				// Set label to white
				name_label.setTextColor(Color.WHITE);
				email_label.setTextColor(Color.WHITE);
				phone_label.setTextColor(Color.WHITE);
				password_label.setTextColor(Color.WHITE);
				confirm_label.setTextColor(Color.WHITE);
				
				String name = name_value.getText().toString();
				String email = email_value.getText().toString();
				String phone = PhoneNumberUtils.stripSeparators(phone_value.getText().toString());
				String password = password_value.getText().toString();
				String confirm = confirm_value.getText().toString();
				if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirm.isEmpty())
					throw new IllegalArgumentException("Fill all required fields.");
				
				if (!password.equals(confirm))
					throw new IllegalArgumentException("Passwords do not match.");
				
				if (password.length() < 6)
					throw new IllegalArgumentException("Passwords must be at least 6 characters.");
				
				if (phone.length() != 10)
					throw new NumberFormatException("Phone number must be 10 digets");
				
				mUser = new ParseUser();
	    		mUser.setUsername(email);
	    		mUser.setPassword(password);
	    		mUser.setEmail(email);
	    		mUser.put("name", name);
	    		mUser.put("phone", Long.parseLong(phone));
				
	    		// Set Submit Button to unclickable
				submit.setClickable(false);
				
				// Start Dialog
				mDialog = new ProgressDialog(MainActivity.mContext);
				mDialog.setMessage("Signing Up...");
				mDialog.setIndeterminate(true);
				mDialog.setCancelable(false);
				mDialog.show();
				
				new signUp().execute();
			}
			catch (NumberFormatException e)
			{
				Log.e("SignUp", "Submit:" + e.toString());
				Toast.makeText(MainActivity.mContext, "Invalid phone number, must be 10 digits.", Toast.LENGTH_SHORT).show();
			}
			catch (IllegalArgumentException e)
			{
				// Always clear password and confirm and set to red
				password_value.setText("");
				password_label.setTextColor(Color.RED);
				password_value.requestFocus();
				confirm_value.setText("");
				confirm_label.setTextColor(Color.RED);
				
				// If empty, set phone to red
				if (phone_value.getText().toString().isEmpty())
				{
					phone_label.setTextColor(Color.RED);
					phone_value.requestFocus();
				}
				
				// If empty, set email to red
				if (email_value.getText().toString().isEmpty())
				{
					email_label.setTextColor(Color.RED);
					email_value.requestFocus();
				}
				
				// If empty, set name to red
				if (name_value.getText().toString().isEmpty())
				{
					name_label.setTextColor(Color.RED);
					name_value.requestFocus();
				}
				
				Log.e("SignUp", "Submit:" + e.toString());
				Toast.makeText(MainActivity.mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private class signUp extends AsyncTask<Void, Void, Void>
    {
		String msg = "";
		@Override
		protected Void doInBackground(Void... params)
		{
	        try
	        {
	        	mUser.signUp();
			}
	        catch (ParseException e)
	        {
    			Log.e("SignUp", "SignUp: " + e.toString());
	        	switch (e.getCode())
	        	{
	        		case 100:
	        			msg = "Check network connection.";
	        			break;
	        		case 125:
	        			msg = "Invalid Email";
	        			break;
	        		case 202:
	        			msg = "Email is already in use";
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
				Activity activity = getActivity();
				Intent intent = new Intent(activity, MainActivity.class);
				startActivity(intent);
				activity.finish();
			}
			else
			{
				Toast.makeText(MainActivity.mContext, msg, Toast.LENGTH_SHORT).show();	        	
	        	
	        	// Set button clickable
	        	Button submit = ((Button) view.findViewById(R.id.submit_button));
				submit.setClickable(true);
				
				// Clear password and focus
	    		EditText password = (EditText) view.findViewById(R.id.password_value);
	    		EditText confirm = (EditText) view.findViewById(R.id.confirm_value);
				password.setText("");
				password.requestFocus();
				confirm.setText("");
			}
		}
    }
}