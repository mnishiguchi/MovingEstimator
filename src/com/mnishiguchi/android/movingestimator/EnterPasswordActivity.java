package com.mnishiguchi.android.movingestimator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class EnterPasswordActivity extends FragmentActivity
{
	private static final String TAG = "movingestimator.EnterPasswordActivity";
	
	// For shared preferences
	private static final String PREFS = "prefs"; // fileneme of sorts
	private static final String PREF_PASSWORD = "password";
	private static String mCurrentPassword;
	private static String DIALOG_PASSWORD = "enterPassword";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		mCurrentPassword = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
				.getString(PREF_PASSWORD, "");
		
		Log.d(TAG, "mCurrentPassword" + mCurrentPassword);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_password);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		new EnterPasswordDialog().show(getSupportFragmentManager(), DIALOG_PASSWORD);
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}
	
	class EnterPasswordDialog extends DialogFragment
	{
		private EditText mEtPassword;
		private String mPassword;
		
		/*
		 * Configure the dialog.
		 */
		@SuppressLint("InflateParams")
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Define the response to buttons.
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
			{ 
				public void onClick(DialogInterface dialog, int which) 
				{ 
					switch (which) 
					{ 
						case DialogInterface.BUTTON_POSITIVE:
							
							// User input.
							mPassword = mEtPassword.getText().toString();
							
							if (mPassword.equals(mCurrentPassword))
							{
								Intent i = new Intent(getActivity(), CustomerListActivity.class);
								startActivity(i);
								getActivity().finish();
							}
							else
							{
								Utils.showToast(getActivity(), "Invalid password");
								getActivity().finish();
								return;
							}
							break; 
					} 
				}
			};
			
			// Inflate the dialog's root view.
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_enter_password, null);
			
			// Get a reference to the user input.
			mEtPassword = (EditText)v.findViewById(R.id.EditTextEnterPassword);
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle("Enter your password ")
				.setView(v)
				.setPositiveButton("Submit", listener)
				.create();
		}
		
		@Override
		public void onPause()
		{
			super.onPause();
			
			// Close the dialog.
			this.dismiss();
			
			// Close the activity.
			EnterPasswordActivity.this.finish();
		}
	}
}
