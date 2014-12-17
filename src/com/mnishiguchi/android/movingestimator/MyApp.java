package com.mnishiguchi.android.movingestimator;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Quickly access resources from anywhere, without passing Activity or Context around
 * http://twigstechtips.blogspot.com/2012/12/android-quickly-access-resources-from.html
 */
public class MyApp extends Application
{
	private static Context context;
	
	/**
	 * Quickly access resources from anywhere, without passing Activity or Context around
	 */
	public static Resources getResourcesStatic()
	{
		return context.getResources();
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
 
		context = getApplicationContext();
	}


}
