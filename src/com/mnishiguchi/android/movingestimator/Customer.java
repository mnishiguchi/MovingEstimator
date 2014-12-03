package com.mnishiguchi.android.movingestimator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Customer
{
	private static String TAG = "movingestimator.Customer";
	
	// JSON keys
	// TODO
	
	
	// Instance Variables.
	private String mId;
	private String mRefNumber;
	private String mFirstName, mLastName, mPrefix;
	private String mOrganization;
	private String mAddress;
	private String mEmail;
	private String mPhoneHome;
	private String mPhoneWork;
	private String mPhoneCell;
	private float mVolumeOcean, mVolumeAir;
	private String mVolumeComment;
	private Date mMovingDate;
	private String mMovingDateComment;
	private String mHomeDescription;
	private String mSpecialOrder;
	private String mGeneralComment;
	
	/**
	 * Constructor. Create a default Customer object with a unique id.
	 * Used when adding a new customer.
	 */
	public Customer()
	{
		// Generate unique identifier (Time Stamp).
		mId = new SimpleDateFormat("yyyMMdd_HHmm_ss_SSS", Locale.US)
			.format(new Date());
	}
	
	/**
	 * Constructor. Create a Customer object based on the passed-in JSONObject.
	 * Used when loading customers from the file system.
	 * @param json a JSONObject that represents a customer.
	 * @throws JSONException
	 */
	public Customer(JSONObject json) throws JSONException
	{
		// TODO
	}
	
	/**
	 * Convert this Customer object  into a JSONObject.
	 */
	public JSONObject toJSON() throws JSONException
	{
		// TODO
		Log.e(TAG, getClass().getSimpleName() + " is not implemented yet.");
		return new  JSONObject();
	}
	
	/**
	 * Create a string representation of this Customer.
	 * e.g. Mr. Nishiguchi
	 */
	@Override
	public String toString()
	{
		String prefix = (null == mPrefix) ? "" : mPrefix;
		String lastName = (null == mLastName) ? "" : mLastName.toUpperCase(Locale.US);
		
		return prefix + " " + lastName;
	}

	public String getRefNumber()
	{
		return mRefNumber;
	}

	public void setRefNumber(String refNumber)
	{
		mRefNumber = refNumber;
	}

	public String getFirstName()
	{
		return mFirstName;
	}

	public void setFirstName(String firstName)
	{
		mFirstName = firstName;
	}

	public String getLastName()
	{
		return mLastName;
	}

	public void setLastName(String lastName)
	{
		mLastName = lastName;
	}

	public String getPrefix()
	{
		return mPrefix;
	}

	public void setPrefix(String prefix)
	{
		mPrefix = prefix;
	}

	public String getOrganization()
	{
		return mOrganization;
	}

	public void setOrganization(String organization)
	{
		mOrganization = organization;
	}

	public String getAddress()
	{
		return mAddress;
	}

	public void setAddress(String address)
	{
		mAddress = address;
	}

	public String getEmail()
	{
		return mEmail;
	}

	public void setEmail(String email)
	{
		mEmail = email;
	}

	public String getPhoneHome()
	{
		return mPhoneHome;
	}

	public void setPhoneHome(String phoneHome)
	{
		mPhoneHome = phoneHome;
	}

	public String getPhoneWork()
	{
		return mPhoneWork;
	}

	public void setPhoneWork(String phoneWork)
	{
		mPhoneWork = phoneWork;
	}

	public String getPhoneCell()
	{
		return mPhoneCell;
	}

	public void setPhoneCell(String phoneCell)
	{
		mPhoneCell = phoneCell;
	}

	public float getVolumeOcean()
	{
		return mVolumeOcean;
	}

	public void setVolumeOcean(float volumeOcean)
	{
		mVolumeOcean = volumeOcean;
	}

	public float getVolumeAir()
	{
		return mVolumeAir;
	}

	public void setVolumeAir(float volumeAir)
	{
		mVolumeAir = volumeAir;
	}

	public String getVolumeComment()
	{
		return mVolumeComment;
	}

	public void setVolumeComment(String volumeComment)
	{
		mVolumeComment = volumeComment;
	}

	public Date getMovingDate()
	{
		return mMovingDate;
	}

	public void setMovingDate(Date movingDate)
	{
		mMovingDate = movingDate;
	}

	public String getMovingDateComment()
	{
		return mMovingDateComment;
	}

	public void setMovingDateComment(String movingDateComment)
	{
		mMovingDateComment = movingDateComment;
	}

	public String getHomeDescription()
	{
		return mHomeDescription;
	}

	public void setHomeDescription(String homeDescription)
	{
		mHomeDescription = homeDescription;
	}

	public String getSpecialOrder()
	{
		return mSpecialOrder;
	}

	public void setSpecialOrder(String specialOrders)
	{
		mSpecialOrder = specialOrders;
	}

	public String getGeneralComment()
	{
		return mGeneralComment;
	}

	public void setGeneralComment(String generalComment)
	{
		mGeneralComment = generalComment;
	}

	public String getId()
	{
		return mId;
	}
}
