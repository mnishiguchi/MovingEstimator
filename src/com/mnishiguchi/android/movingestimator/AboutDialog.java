package com.mnishiguchi.android.movingestimator;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutDialog extends DialogFragment
{
	TextView mTextView;
	
	public static AboutDialog newInstance()
	{
		// Instantiate the fragment with the arguments.
		AboutDialog fragment = new AboutDialog();
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		// Dynamically create an mTextView from scratch.
		mTextView = new TextView(getActivity());
		
		String text = getActivity().getString(R.string.aboutdialog_text);
		
		// Set text on the TextView.
		mTextView.setText(text);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		
		return mTextView;
	}
}
