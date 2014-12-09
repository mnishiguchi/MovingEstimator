package com.mnishiguchi.android.movingestimator;

class EstimateItem
{
	String name;
	double size;
	int quantity;
	String room;
	String mode;
	String comment;
	
	public EstimateItem(String name, double size, int quantity, String room,
			String mode, String comment)
	{
		this.name = name;
		this.size = size;
		this.quantity = quantity;
		this.room = room;
		this.mode = mode;
		this.comment = comment;
	}
}
