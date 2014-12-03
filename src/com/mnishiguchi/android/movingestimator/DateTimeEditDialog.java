package com.mnishiguchi.android.movingestimator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;

public class DateTimeEditDialog extends DialogFragment
{
	/* STATIC */
	public static final String DIALOG_DATETIME_PICKER = "dateTimePicker";
	
	public static final String EXTRA_DATE = "com.mnishiguchi.android.movingestimator.date";

	/* INSTANCE VARIABLES */
	private Date mDate;
	
	/**
	 * Creates a new instance of DatePickerFragment and sets its arguments bundle.
	 * @param date
	 * @return a new instance of DatePickerFragment.
	 */
	public static DateTimeEditDialog newInstance(Date date)
	{
		// Prepare arguments.
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
	
		// Create a new instance.
		DateTimeEditDialog fragment = new DateTimeEditDialog();
		
		// Stash the date in this fragment's arguments bundle.
		fragment.setArguments(args);
		
		return fragment;
	}
	
	/**
	 * Creates a new AlertDialog object and configure it.
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		//sTargetRequestCode = getTargetRequestCode();
		
		// Retrieve the arguments.
		mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
		
		// Option list items.
		String[] options = { "Set Date", "Set Time"};
		
		// Configure the AlertDialog and return it.
		return new AlertDialog.Builder(getActivity() )
				.setTitle(mDate.toString())
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) { } // Do nothing.
				})
				.setItems(options, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// Show a datePicker of timePicker based on the user's selection.
						FragmentManager fm = getActivity().getSupportFragmentManager();
						DialogFragment picker = null;
						switch (which)
						{
							case 0: picker = DatePickerDialog.newInstance(mDate);
								break;
							case 1: picker = TimePickerDialog.newInstance(mDate);
								break;
						}
						picker.setTargetFragment(getTargetFragment(), getTargetRequestCode());
						picker.show(fm, DIALOG_DATETIME_PICKER);
						
						dismiss();
					}
				})
				.create();
	}
}

/**
 * Manages the AlertDialog that displays a DatePicker widget.
 */
class DatePickerDialog extends DialogFragment
{	
	/* INSTANCE VARIABLES */
	private Date mDate;
	
	// To remember the user's input.
	private int mYear, mMonth, mDay, mHour, mMin;
	
	/**
	 * Creates a new instance of DatePickerFragment and sets its arguments bundle.
	 * @param date
	 * @return a new instance of DatePickerFragment.
	 */
	public static DatePickerDialog newInstance(Date date)
	{
		// Prepare arguments.
		Bundle args = new Bundle();
		args.putSerializable(DateTimeEditDialog.EXTRA_DATE, date);
		
		// Create a new instance of DatePickerFragment.
		DatePickerDialog dialog = new DatePickerDialog();
		
		// Stash the date in DatePickerFragment's arguments bundle.
		dialog.setArguments(args);
		
		return dialog;
	}
	
	/**
	 * Sends data to the target fragment.
	 * @param resultCode
	 */
	private void sendResult(int resultCode)
	{
		// Do nothing if there is no target fragment.
		if (getTargetFragment() == null) return;
		
		// Send data to the target fragment.
		Intent i = new Intent();
		i.putExtra(DateTimeEditDialog.EXTRA_DATE, mDate);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	/**
	 * Creates a new AlertDialog object and configure it.
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Retrieve the arguments.
		mDate = (Date) getArguments().getSerializable(DateTimeEditDialog.EXTRA_DATE);
		
		// Get initial integers for year, month, day, etc.
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(mDate);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		mHour = calendar.get(Calendar.HOUR_OF_DAY);
		mMin = calendar.get(Calendar.MINUTE);
		
		// Inflate the dialog's layout defined in res/layout/dialog_date.xml.
		View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
		
		// Initialize the DatePicker component.
		DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(mYear, mMonth, mDay, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month, int day)
			{
				// Remember updated values.
				mYear = year;
				mMonth = month;
				mDay = day;
				
				updateDate();
			}
		});

		// Configure it and return it.
		return new AlertDialog.Builder(getActivity() )
				.setView(dialogView)
				.setTitle(R.string.date_picker_title)
				.setPositiveButton(
						android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								updateDate();
								sendResult(Activity.RESULT_OK);
							}
						})
				.create();
	}
	
	/**
	 * Update mDate based on updated values that the user has inputed.
	 */
	public void updateDate()
	{
		// Translate year, month and day into a Date object.
		mDate = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMin).getTime();
		
		// Update arguments to preserve selected value on rotation.
		getArguments().putSerializable(DateTimeEditDialog.EXTRA_DATE, mDate);
	}	
}

/**
 * Manages the AlertDialog that displays a TimePicker widget.
 */
class TimePickerDialog extends DialogFragment
{
	private Date mDate;
	
	// To remember the user's input.
	private int mYear, mMonth, mDay, mHour, mMin;
	
	/**
	 * Creates a new instance of DatePickerFragment and sets its arguments bundle.
	 * @param date
	 * @return a new instance of DatePickerFragment.
	 */
	public static TimePickerDialog newInstance(Date date)
	{
		// Prepare arguments.
		Bundle args = new Bundle();
		args.putSerializable(DateTimeEditDialog.EXTRA_DATE, date);
		
		// Create a new instance of DatePickerFragment.
		TimePickerDialog dialog = new TimePickerDialog();
		
		// Stash the date in DatePickerFragment's arguments bundle.
		dialog.setArguments(args);
		
		return dialog;
	}
	
	/**
	 * Sends data to the target fragment.
	 * @param resultCode
	 */
	private void sendResult(int resultCode)
	{
		// Do nothing if there is no target fragment.
		if (getTargetFragment() == null) return;
		
		// Send data to the target fragment.
		Intent i = new Intent();
		i.putExtra(DateTimeEditDialog.EXTRA_DATE, mDate);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	/**
	 * Creates a new AlertDialog object and configure it.
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Retrieve the arguments.
		mDate = (Date) getArguments().getSerializable(DateTimeEditDialog.EXTRA_DATE);
		
		// Get initial integers for year, month, day, etc.
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(mDate);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		mHour = calendar.get(Calendar.HOUR_OF_DAY);
		mMin = calendar.get(Calendar.MINUTE);
		
		// Inflate the dialog's layout defined in res/layout/dialog_time.xml.
		View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);
		
		// Initialize the TimePicker component.
		TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.dialog_date_timePicker);
		timePicker.setCurrentHour(mHour);
		timePicker.setCurrentMinute(mMin);
		timePicker.setIs24HourView(false);
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hour, int min)
			{
				// Remember updated values.
				mHour = hour;
				mMin = min;
				
				updateDate();
			}
		});

		// Configure it and return it.
		return new AlertDialog.Builder(getActivity() )
				.setView(dialogView)
				.setTitle(R.string.time_picker_title)
				.setPositiveButton(
						android.R.string.ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								updateDate();
								sendResult(Activity.RESULT_OK);
							}
						})
				.create();
	}
	
	/**
	 * Update mDate based on updated values that the user has inputed.
	 */
	public void updateDate()
	{
		// Translate year, month and day into a Date object.
		mDate = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMin).getTime();
		
		// Update arguments to preserve selected value on rotation.
		getArguments().putSerializable(DateTimeEditDialog.EXTRA_DATE, mDate);
	}
}

