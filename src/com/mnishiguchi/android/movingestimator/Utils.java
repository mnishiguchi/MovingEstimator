package com.mnishiguchi.android.movingestimator;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.widget.Toast;

public class Utils
{
	/**
	 * Determine which interface was inflated, single-pane or two-pane.
	 * @return true if in the two-pane mode, else false.
	 */
	static boolean hasTwoPane(Activity a)
	{
		//return c.getResources().getBoolean(R.bool.has_two_panes);
		return (a.findViewById(R.id.detailFragmentContainer) != null);
	}
	
	/**
	 * This is to check how many activities can respond to the passed-in intent.
	 * Run this check in onCreateView() to disable options that the device will not be able to respond to.
	 * If the OS cannot find a matching activity, then the app will crash.
	 * An Android device is guaranteed to have an email app and a contacts app of one kind or another.
	 */
	static boolean isIntentSafe(Activity activity, Intent i)
	{
		PackageManager pm = activity.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
		return (activities.size() > 0);
	}
	
	static boolean hasCamera(Activity activity)
	{
		PackageManager pm = activity.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
				pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
				Camera.getNumberOfCameras() > 0;
	}
	
	/**
	 * Show a toast message.
	 */
	static void showToast(Context context, String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
