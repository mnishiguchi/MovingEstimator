package com.mnishiguchi.android.movingestimator;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Display a larger image.
 */
public class ImageFragment extends DialogFragment
{
	//private static String TAG = "CriminalIntent.ImageFragment";
	public static final String EXTRA_IMAGE_PATH = "com.mnishiguchi.android.movingestimator.image_path";
	
	private ImageView mImageView;
	
	/**
	 * Create a new instance associated with a specified image file path and orientation.
	 */
	public static ImageFragment newInstance(String imagePath)
	{
		// Set the image's filepath on the arguments.
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
		
		// Instantiate the fragment with the arguments.
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		return fragment;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		// Dynamically create an ImageView from scratch.
		mImageView = new ImageView(getActivity());
		
		// Get the image's filepath and orientation from the arguments.
		String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
		
		// Create a scaled bitmap image based on image data that is stored in disk. 
		BitmapDrawable image = ImageUtils.getScaledDrawable(getActivity(), path);
		
		// Set image on the ImageView.
		mImageView.setImageDrawable(image);
		
		return mImageView;
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ImageUtils.cleanImageView(mImageView);
	}
}
