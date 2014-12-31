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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class EnterPasswordActivity extends FragmentActivity
{
	//private static final String TAG = "movingestimator.EnterPasswordActivity";
	
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
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_password);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if (mCurrentPassword.equals(""))
		{
			startCustomerListActivity();
		}
		else
		{
			new EnterPasswordDialog().show(getSupportFragmentManager(), DIALOG_PASSWORD);
		}
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}
	
	private void startCustomerListActivity()
	{
		Intent i = new Intent(this, CustomerListActivity.class);
		startActivity(i);
		this.finish();
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
								startCustomerListActivity();
							}
							else
							{
								Utils.showToast(getActivity(), getActivity().getString(
										R.string.invalid_password));
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
				.setTitle(R.string.enter_your_password)
				.setView(v)
				.setPositiveButton(android.R.string.ok, listener)
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
