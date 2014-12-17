package com.mnishiguchi.android.movingestimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Customer
{
	//private static String TAG = "movingestimator.Customer";
	
	// Remember the current customer globally.
	private static Customer sCurrentCustomer;
	static Customer getCurrentCustomer()
	{
		return sCurrentCustomer;
	}
	
	static void setCurrentCustomer(Customer customer)
	{
		//Log.d(TAG, "setCurrentCustomer()=>" + customer);
		sCurrentCustomer = customer;
	}
	
	// JSON keys
	private static final String JSON_ID = "id";
	private static final String JSON_REF_NUMBER = "refNumber";
	private static final String JSON_FIRST_NAME = "firstName";
	private static final String JSON_LAST_NAME = "lastName";
	private static final String JSON_PREFIX = "prefix";
	private static final String JSON_ORGANIZATION = "organization";
	private static final String JSON_EMAIL = "email";
	private static final String JSON_PHONE_HOME = "phoneHome";
	private static final String JSON_PHONE_WORK = "phoneWork";
	private static final String JSON_PHONE_CELL = "phoneCell";
	private static final String JSON_FROM = "from";
	private static final String JSON_TO = "to";
	private static final String JSON_MOVING_DATE = "movingDate";
	private static final String JSON_MOVING_SCHEDULE = "movingSchedule";
	private static final String JSON_HOME_DESCRIPTION = "homeDescription";
	private static final String JSON_SPECIAL_ORDER = "specialOrder";
	private static final String JSON_GENERAL_COMMENT = "generalComment";
	private static final String JSON_PHOTO = "photo";
	
	private static final String JSON_ROOMS = "rooms";
	
	// Instance Variables, initially empty/0.0 (except date and photo)
	private String mId = "";
	private String mRefNumber = "";
	
	private String mFirstName = "";
	private String mLastName = "";
	private String mPrefix = "";
	private String mOrganization = "";
	private String mEmail = "";
	private String mPhoneHome = "";
	private String mPhoneWork = "";
	private String mPhoneCell = "";
	private String mFrom = "";
	private String mTo = "";
	private Date mMovingDate = null;
	private String mMovingSchedule = "";
	private String mHomeDescription = "";
	private String mSpecialOrder = "";
	private String mGeneralComment = "";
	private Photo mPhoto = null;
	
	private ArrayList<String> mRooms = new ArrayList<String>();
	
	/**
	 * Constructor. Create a default Customer object with a unique id.
	 * Used when adding a new customer.
	 */
	public Customer()
	{
		// Generate unique identifier (Time Stamp).
		mId = new SimpleDateFormat("yyyMMdd_HHmm_ss_SSS", Locale.US)
			.format(new Date());
		
		// Ref# initially the same as id.
		mRefNumber = mId;
	}
	
	/**
	 * Constructor. Create a Customer object based on the passed-in JSONObject.
	 * Used when loading customers from the file system.
	 * @param json a JSONObject that represents a customer.
	 * @throws JSONException
	 */
	public Customer(JSONObject json) throws JSONException
	{
		//Log.e(TAG, json.toString());
		
		mId = json.getString(JSON_ID);
		mRefNumber = json.getString(JSON_REF_NUMBER);
		mFirstName = json.getString(JSON_FIRST_NAME);
		mLastName = json.getString(JSON_LAST_NAME);
		mPrefix = json.getString(JSON_PREFIX);
		mOrganization = json.getString(JSON_ORGANIZATION);
		mEmail = json.getString(JSON_EMAIL);
		mPhoneHome = json.getString(JSON_PHONE_HOME);
		mPhoneWork = json.getString(JSON_PHONE_WORK);
		mPhoneCell = json.getString(JSON_PHONE_CELL);
		mFrom = json.getString(JSON_FROM);
		mTo = json.getString(JSON_TO);

		if (json.has(JSON_MOVING_DATE))
		{
			mMovingDate = new Date(json.getLong(JSON_MOVING_DATE));
		}

		mMovingSchedule = json.getString(JSON_MOVING_SCHEDULE);
		mHomeDescription = json.getString(JSON_HOME_DESCRIPTION);
		mSpecialOrder = json.getString(JSON_SPECIAL_ORDER);
		mGeneralComment = json.getString(JSON_GENERAL_COMMENT);

		if (json.has(JSON_PHOTO))
		{
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
		
		JSONArray rooms = json.getJSONArray(JSON_ROOMS);
		for (int i = 0, size = rooms.length();
				i < size; i++)
		{
			mRooms.add(rooms.get(i).toString());
		}
	}
	
	/**
	 * Convert this Customer object  into a JSONObject.
	 */
	public JSONObject toJSON() throws JSONException
	{
		// Create a new JSONObject.
		JSONObject json = new JSONObject();
		
		// Map name to value.
		json.put(JSON_ID, mId);
		
		json.put(JSON_REF_NUMBER, mRefNumber);
		json.put(JSON_FIRST_NAME, mFirstName);
		json.put(JSON_LAST_NAME, mLastName);
		json.put(JSON_PREFIX, mPrefix);
		json.put(JSON_ORGANIZATION, mOrganization);
		json.put(JSON_EMAIL, mEmail);
		json.put(JSON_PHONE_HOME, mPhoneHome);
		json.put(JSON_PHONE_WORK, mPhoneWork);
		json.put(JSON_PHONE_CELL, mPhoneCell);
		json.put(JSON_FROM, mFrom);
		json.put(JSON_TO, mTo);
		
		if (mMovingDate != null)
		{
			json.put(JSON_MOVING_DATE, mMovingDate.getTime()); // convert Date to long
		}
		
		json.put(JSON_MOVING_SCHEDULE, mMovingSchedule);
		json.put(JSON_HOME_DESCRIPTION, mHomeDescription);
		json.put(JSON_SPECIAL_ORDER, mSpecialOrder);
		json.put(JSON_GENERAL_COMMENT, mGeneralComment);
		
		if (mPhoto != null)
		{
			json.put(JSON_PHOTO, mPhoto.toJSON());
		}
		
		JSONArray rooms = new JSONArray();
		for (String each : mRooms)
		{
			rooms.put(each);
		}
		json.put(JSON_ROOMS, rooms);
		
		return json;
	}
	
	/**
	 * Create a string representation of this Customer.
	 */
	@Override
	public String toString()
	{
		// No last name => (No Name)
		if (mLastName.equals(""))
		{
			return MyApp.getResourcesStatic().getString(android.R.string.unknownName);
		}
		
		String s = "";
		if (Locale.getDefault().getDisplayLanguage().equals("“ú–{Œê")) // Japanese.
		{
			s = mLastName.toUpperCase(Locale.US) + mPrefix;
		}
		else // the other languages.
		{
			s = mPrefix + " " + mLastName.toUpperCase(Locale.US);
		}
		
		return s;
	}
	
	public String getMovingDateString()
	{
		if (null == mMovingDate)
		{
			// To be determined.
			return MyApp.getResourcesStatic().getString(R.string.tbd);
		}
		return (String) android.text.format.DateFormat
				.format("yyyy-MM-dd / hh:mma", mMovingDate);
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

	public Photo getPhoto()
	{
		return mPhoto;
	}

	public void setPhoto(Photo photo)
	{
		mPhoto = photo;
	}

	public ArrayList<String> getRooms()
	{
		return mRooms;
	}

	public void setRooms(ArrayList<String> rooms)
	{
		mRooms = rooms;
	}

	public String getFrom()
	{
		return mFrom;
	}

	public void setFrom(String from)
	{
		mFrom = from;
	}

	public String getTo()
	{
		return mTo;
	}

	public void setTo(String to)
	{
		mTo = to;
	}

	public String getMovingSchedule()
	{
		return mMovingSchedule;
	}

	public void setMovingSchedule(String movingSchedule)
	{
		mMovingSchedule = movingSchedule;
	}
	
	
}
