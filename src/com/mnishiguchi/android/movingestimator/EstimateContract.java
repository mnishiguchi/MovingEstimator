package com.mnishiguchi.android.movingestimator;

import android.provider.BaseColumns;

/**
 * A contract class is a container for constants that define names for URIs,
 * tables, and columns. The contract class allows you to use the same constants
 * across all the other classes in the same package.
 * This lets you change a column name in one place and have it propagate throughout your code.
 * A good way to organize a contract class is to put definitions that are global
 * to your whole database in the root level of the class.
 * Then create an inner class for each table that enumerates its columns.
 */
public final class EstimateContract
{
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public EstimateContract() {}

	/* Inner class that defines the table contents */
	public static abstract class EstimateTable implements BaseColumns
	{
		public static final String TABLE_NAME = "moving_estimate";
		public static final String COLUMN_CUSTOMER_ID = "customer_id";
		public static final String COLUMN_ESTIMATE_DATE = "estimate_date";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_SIZE = "size";
		public static final String COLUMN_QUANTITY = "quantity";
		public static final String COLUMN_SUBTOTAL = "subtotal";
		public static final String COLUMN_ROOM = "room";
		public static final String COLUMN_MODE = "mode";
		public static final String COLUMN_COMMENT = "comment";
	}
}
