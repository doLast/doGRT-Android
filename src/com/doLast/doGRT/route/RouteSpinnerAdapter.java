package com.doLast.doGRT.route;

import com.doLast.doGRT.R;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RouteSpinnerAdapter extends ArrayAdapter<CharSequence> {
	private String stop_title = null;
	private Context mContext = null;
	private String[] days = null;
	private int today = 0;
	

	public RouteSpinnerAdapter(Context context, int textViewResourceId, String[] array, String title, int today) {
		super(context, textViewResourceId, array);
		stop_title = title;
		mContext = context;
		days = array;
		this.today = today;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = new TextView(mContext);		
		TextView stop_name = (TextView)convertView;
		
		// Format text view
		stop_name.setTextSize(22);
		stop_name.setGravity(Gravity.TOP);
		stop_name.setText(stop_title);
		return convertView;
	}		
}
