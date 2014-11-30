package com.mnishiguchi.android.movingestimator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class Customer
{
	// JSON keys
	// TODO
	
	
	// Instance Variables.
	private String mId;
	private String mRefNumber;
	private String mFirstName, mLastName, mPrefix;
	private String mCompanyName;
	private String mAddress;
	private String mPhoneNumber;
	private int mLimitOcean, mLimitAir;
	private String mLimitNotes;
	private Date mMovingDate;
	private String mHomeDescription;
	private String mRemarks;
	
	/**
	 * Constructor. Create a default Customer object.
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
		return new  JSONObject();
	}
	
	/**
	 * Create a string representation of this Customer.
	 * e.g. Mr. Nishiguchi
	 */
	@Override
	public String toString()
	{
		return mPrefix + " " +  mLastName;
	}
	
	public String getId()
	{
		return mId;
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
	public String getCompanyName()
	{
		return mCompanyName;
	}
	public void setCompanyName(String companyName)
	{
		mCompanyName = companyName;
	}
	public String getAddress()
	{
		return mAddress;
	}
	public void setAddress(String address)
	{
		mAddress = address;
	}
	public String getPhoneNumber()
	{
		return mPhoneNumber;
	}
	public void setPhoneNumber(String phoneNumber)
	{
		mPhoneNumber = phoneNumber;
	}
	public int getLimitOcean()
	{
		return mLimitOcean;
	}
	public void setLimitOcean(int limitOcean)
	{
		mLimitOcean = limitOcean;
	}
	public int getLimitAir()
	{
		return mLimitAir;
	}
	public void setLimitAir(int limitAir)
	{
		mLimitAir = limitAir;
	}
	public String getLimitNotes()
	{
		return mLimitNotes;
	}
	public void setLimitNotes(String limitNotes)
	{
		mLimitNotes = limitNotes;
	}
	public Date getMovingDate()
	{
		return mMovingDate;
	}
	public void setMovingDate(Date movingDate)
	{
		mMovingDate = movingDate;
	}
	public String getHomeDescription()
	{
		return mHomeDescription;
	}
	public void setHomeDescription(String homeDescription)
	{
		mHomeDescription = homeDescription;
	}
	public String getRemarks()
	{
		return mRemarks;
	}
	public void setRemarks(String remarks)
	{
		mRemarks = remarks;
	}
}
