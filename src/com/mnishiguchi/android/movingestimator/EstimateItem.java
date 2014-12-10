package com.mnishiguchi.android.movingestimator;

class EstimateItem
{
	long rowId = -1;
	String customerId;
	String name;
	double size;
	int quantity;
	String room;
	String mode;
	String comment;
	
	public EstimateItem(String customerId, String name, double size, int quantity, String room,
			String mode, String comment)
	{
		this.customerId = customerId;
		this.name = name;
		this.size = size;
		this.quantity = quantity;
		this.room = room;
		this.mode = mode;
		this.comment = comment;
	}
}
