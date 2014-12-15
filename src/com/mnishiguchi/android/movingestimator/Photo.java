package com.mnishiguchi.android.movingestimator;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * Creating a Photo class is useful when we need to display a caption or handling a touch event.
 */
class Photo
{
	private static final String TAG = "movingestimator.Photo";
	
	// JSON keys
	private static final String JSON_FILENAME ="filename";
	
	private String mFilename;
	
		/**
		 * Constructor to create a Photo representing existing file on disk
		 */
		Photo(String filename)
		{
			mFilename = filename;
		}
	

	/**
	 * Constructor to create a Photo from a specified JSON filename.
	 * Used when saving and loading its property of type Photo.
	 */
	Photo (JSONObject json) throws JSONException
	{
		mFilename = json.getString(JSON_FILENAME);
	}
	
	/**
	 * Serialize this Photo into a JSON object.
	 */
	JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put(JSON_FILENAME, mFilename);
		return json;
	}
	
	/**
	 * @return this Photo's filename
	 */
	String getFilename()
	{
		return mFilename;
	}

	/**
	 *  Get the absolute path to the file where this photo's image data  is stored.
	 */
	String getAbsolutePath(Context context)
	{
		//File dir = context.getApplicationContext()
				//.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File dir = ImageUtils.getPictureStorageDir(context);
		File file = new File(dir, getFilename());
		
		return file.getAbsolutePath(); // Convert the filepath to string.
	}
	
	/**
	 * @param context
	 * @return true if this image's file was deleted from disk, false otherwise.
	 */
	boolean deletePhoto(Context context)
	{
		// Get the image data file on disk.
		String path = this.getAbsolutePath(context);
		File imageFile = new File(path);
		
		// Delete the file and return success or fail.
		boolean success = imageFile.delete();
		
		if (!success)
		{
			Log.e(TAG, "deletePhoto(), success=>: " + String.valueOf(success));
		}
		
		return success;
	}

	/**
	 * Get a BitmapDrawable from disk for this photo object.
	 */
	BitmapDrawable loadBitmapDrawable(Activity activity)
	{
		String path = this.getAbsolutePath(activity);

		//Log.d(TAG, "Bitmap loaded from: " + path);
		
		// Get a scaled bitmap drawable based on the data in this file.
		return ImageUtils.getScaledDrawable(activity, path);
	}
}