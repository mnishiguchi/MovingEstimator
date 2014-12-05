package com.mnishiguchi.android.movingestimator;

public class MovingItem
{
	private String mName;
	private double mSize;
	private int mQuantity;
	private String mRoom;
	private String mTransportMode;
	private String mComment;
	
	/**
	 * Constructor.
	 */
	public MovingItem(String name, double size, int quantity, String room,
			String transportMode, String comment)
	{
		super();
		mName = name;
		mSize = size;
		mQuantity = quantity;
		mRoom = room;
		mTransportMode = transportMode;
		mComment = comment;
	}
	
	public String getName()
	{
		return mName;
	}
	public void setName(String name)
	{
		mName = name;
	}
	public double getSize()
	{
		return mSize;
	}
	public void setSize(double size)
	{
		mSize = size;
	}
	public int getQuantity()
	{
		return mQuantity;
	}
	public void setQuantity(int quantity)
	{
		mQuantity = quantity;
	}
	public String getRoom()
	{
		return mRoom;
	}
	public void setRoom(String room)
	{
		mRoom = room;
	}
	public String getTransportMode()
	{
		return mTransportMode;
	}
	public void setTransportMode(String transportMode)
	{
		mTransportMode = transportMode;
	}
	public String getComment()
	{
		return mComment;
	}
	public void setComment(String comment)
	{
		mComment = comment;
	}
	
	
}
