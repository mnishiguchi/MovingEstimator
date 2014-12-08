package com.mnishiguchi.android.movingestimator;

import com.mnishiguchi.android.movingestimator.EstimateContract;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EstimateListAdapter extends BaseAdapter
{
	public ArrayList<HashMap> mList;
	Activity mActivity;
 
	public EstimateListAdapter(Activity activity, ArrayList<HashMap> list)
	{
		super();
		this.mActivity = activity;
		this.mList = list;
	}

	@Override
	public int getCount()
	{
		return mList.size();
	}
 
	@Override
	public Object getItem(int position)
	{
		return mList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	private class ViewHolder
	{
		TextView textViewItemName;
		TextView textViewSize;
		TextView textViewQuantity;
		TextView textViewMode;
		TextView textViewComment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		LayoutInflater inflater = mActivity.getLayoutInflater();

		if (null == convertView)
		{
			convertView = inflater.inflate(R.layout.listitem_estimate, null);
			holder = new ViewHolder();
			holder.textViewItemName = (TextView) convertView.findViewById(R.id.TextViewListItemEstimateItemName);
			holder.textViewSize = (TextView) convertView.findViewById(R.id.TextViewListItemEstimateSize);
			holder.textViewQuantity = (TextView) convertView.findViewById(R.id.TextViewListItemEstimateQuantity);
			holder.textViewMode = (TextView) convertView.findViewById(R.id.TextViewListItemEstimateMode);
			holder.textViewComment = (TextView) convertView.findViewById(R.id.TextViewListItemEstimateComment);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
 
		HashMap map = mList.get(position);
		
		//holder.textViewItemName.setText(map.get(FIRST_COLUMN));
		//holder.textViewSize.setText(map.get(SECOND_COLUMN));
		//holder.textViewQuantity.setText(map.get(THIRD_COLUMN));
		//holder.textViewMode.setText(map.get(FOURTH_COLUMN));
		//holder.textViewComment.setText(map.get(FOURTH_COLUMN));
 
		return convertView;
	}
}