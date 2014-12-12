package com.mnishiguchi.android.movingestimator;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;


/**
 * Utility class that provides static methods for image handling.
 * Pictures will easily blow out your app's memory budget.
 * So you need some code to scale the image before loading it and
 * some code to clean up when the image is no longer needed.
 */
public class ImageUtils
{
	private static final String TAG = "CriminalIntent.PictureUtils";
	
	/**
	 * Standardize on the storage location for pictures for this application.
	 */
	public static File getPictureStorageDir(Context context)
	{
		// context.openFileOutput(filename, Context.MODE_PRIVATE); // Internal Private
		// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		
		// Ensure that the external storage is available.
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			Log.e(TAG, "External Storage is not available." );
			return null;
		}
		
		return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); // external private
	}
	
	/**
	 * Get a BitmapDrawable from a local file that is scaled down to fit the current Window size.
	 * Adjust the orientation based on the EXIF data.
	 */
	public static BitmapDrawable getScaledDrawable(Activity activity, String path)
	{
		// Get  the dimensions of the display.
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float destWidth = size.x;
		float destHeight = size.y;
		
		return getScaledDrawable(activity, path, destWidth, destHeight);
	}
	
	/**
	 * Get a BitmapDrawable from a local file that is scaled down to the specified size.
	 * Adjust the orientation based on the EXIF data.
	 */
	public static BitmapDrawable getScaledDrawable(Activity activity, String path, float destWidth, float destHeight)
	{
		// Get the dimensions of the image on disk.
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // No pixel data needed.
		BitmapFactory.decodeFile(path, options); 
	
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;
		
		// Initialize inSampleSize.
		int inSampleSize = 1; // Read every ??? px.
		
		// Check if the image is larger than the display.
		if (srcHeight > destHeight || srcWidth > destWidth)
		{
			if (srcWidth > srcHeight) // Landscape
			{
				// Let the height match.
				inSampleSize = Math.round(srcHeight / destHeight);
			}
			else // Portrait
			{
				// Let the width match.
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		
		// Set the inSampleSize.
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		
		// Scale down the bitmap data based on the inSampleSize.
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		if (null == bitmap) return null; // Null check.
		
		// Get the rotation data.
		Matrix matrix = readEXIF(path);

		// Rotate the bitmap.
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		// Return a new drawable.
		return new BitmapDrawable(activity.getResources(), rotatedBitmap);
	}
	
	/**
	 * Get an image data as byte array and create a BitmapDrawable that is 
	 * scaled it down to fit the current Window size.
	 * The orientation is not considered.
	 */
	public static Bitmap getScaledBitmap(Activity activity, byte[] data)
	{
		// Get  the dimensions of the display.
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float destWidth = size.x;
		float destHeight = size.y;
		
		// Get the dimensions of the image on disk.
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // No pixel data needed.
		BitmapFactory.decodeByteArray(data , 0, data.length, options);
		
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;
		
		// Initialize inSampleSize.
		int inSampleSize = 1; // Read every ??? px.
		
		// Check if the image is larger than the display.
		if (srcHeight > destHeight || srcWidth > destWidth)
		{
			if (srcWidth > srcHeight) // Landscape
			{
				// Let the height match.
				inSampleSize = Math.round(srcHeight / destHeight);
			}
			else // Portrait
			{
				// Let the width match.
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		
		// Set the inSampleSize.
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		
		// Scale down the bitmap data based on the inSampleSize.
		return BitmapFactory.decodeByteArray(data , 0, data.length, options);
	}
	
	/**
	 * Rotate the passed-in BitmapDrawable by 90 degrees.
	 */
	public static BitmapDrawable getPortraitDrawable(ImageView imageView, BitmapDrawable drawable)
	{
		// Matrix to rotate.
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		
		return rotateDrawable(imageView, drawable, matrix);
	}
	
	/**
	 * Rotate the passed-in BitmapDrawable based on the Matrix.
	 */
	public static BitmapDrawable rotateDrawable(ImageView imageView,
			BitmapDrawable drawable, Matrix matrix)
	{
		// Original bitmap
		Bitmap src = drawable.getBitmap();
		
		Bitmap rotatedBitmap = Bitmap.createBitmap(src, 0, 0,
				drawable.getIntrinsicWidth(), 
				drawable.getIntrinsicHeight(),
				matrix, true);
		return new BitmapDrawable(imageView.getResources(), rotatedBitmap);
	}
	
	/**
	 * Get the orientation data of an image file.
	 */
	static Matrix readEXIF(String filepath)
	{
		Matrix matrix = new Matrix();
		try
		{
			ExifInterface exif = new ExifInterface(filepath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			if (orientation == 3)
			{
				matrix.postRotate(180);
			}
			else if (orientation == 6)
			{
				matrix.postRotate(90);
			}
			else if (orientation == 8)
			{
				matrix.postRotate(270);
			}
		}
		catch (Exception ex)
		{
			Log.e(TAG, "error: ", ex);
		}
		return matrix;
	}
	
	/**
	 * Compress the bitmap.
	 */
	public static Bitmap compressBitmap(Bitmap bitmap)
	{
		// Write a compressed version of the bitmap to the specified outputstream.
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 0, out);
		Bitmap compressedImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
		try
		{
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			Log.e(TAG, "Error closing ByteArrayOutputStream", e);
			
		}
		
		Log.e("Original   dimensions", bitmap.getWidth() + " " + bitmap.getHeight());
		Log.e("Compressed dimensions", compressedImage.getWidth() + " " + compressedImage.getHeight());
		
		return compressedImage;
	}
	
	/**
	 * Explicitly clean up an ImageView's BitmapDrawable, if it has one.
	 * This can prevent the possibility of ugly memory bugs.
	 * Loading images in onStart() and them unloading in onStop is a good practice.
	 */
	public static void cleanImageView(ImageView imageView)
	{
		// Ensure that the passed-in view is of type BitmapDrawable.
		if (!(imageView.getDrawable() instanceof BitmapDrawable))
		{
			return;
		}
			
		// Clear the reference to the image's pixel data.
		BitmapDrawable bitmap = (BitmapDrawable)imageView.getDrawable();
		if (bitmap.getBitmap() != null)
		{
			bitmap.getBitmap().recycle(); 
		}
		
		// Clear the imageView.
		imageView.setImageDrawable(null);
	}
	
	/**
	 * 
	 */
	static String generateImageFileName(Context context)
	{
		String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		return "IMG_"+ timeStamp + ".jpg";
	}
	
	/**
	 * Create a new image file at the directory that is standardized for this application.
	 * The standard directory is defined in getPictureStorageDir(context) method.
	 */
	static File createImageFile(Context context, String filename)
	{
		File directory = getPictureStorageDir(context);
		
		if (!directory.exists())
		{
			if (!directory.mkdirs())
			{
				Log.e(TAG, "Failed to create storage directory.");
				return null;
			}
		}
		
		return new File(directory, filename);
	}
	
	/**
	 * Save an image data as the specified file name inside the specified directory.
	 */
	public static boolean savePicture(Context context, byte[] data, File dir, String filename)
	{
		// Save the jpeg data to disk.
		BufferedOutputStream out = null;
		try
		{
			File file = new File(dir, filename);
			out = new BufferedOutputStream(new FileOutputStream(file));
			out.write(data);
			out.close();
			
			Log.e(TAG, "Photo saved to: " + file.getAbsolutePath());
			
			// Get the media scanner service tol read metadata from the file and
			// add the file to the media content provider. 
			MediaScannerConnection.scanFile(context,
					new String[] { file.toString() }, null,
					new MediaScannerConnection.OnScanCompletedListener()
					{
						@Override
						public void onScanCompleted(String path, Uri uri)
						{
							Log.i("ExternalStorage", "Scanned " + path + ":");
							Log.i("ExternalStorage", "-> uri=" + uri);
						}
					});
		}
		catch (Exception e)
		{
			Log.e(TAG, "Error saving photo file: " + filename, e);
			return false;
		}
		return true;
	}
}
