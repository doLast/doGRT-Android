package com.doLast.doGRT.route;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RouteSpinnerAdapter extends ArrayAdapter<CharSequence> {
	private String stop_title = null;
	private Context mContext = null;
	private String[] days = null;
	

	public RouteSpinnerAdapter(Context context, int textViewResourceId, String[] array, String title) {
		super(context, textViewResourceId, array);
		stop_title = title;
		mContext = context;
		days = array;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = new TextView(mContext);
		TextView text_view = (TextView)convertView;
		
		// Format text view
		text_view.setTextSize(19);
		if (parent.getHeight() > 0 ) text_view.setHeight(parent.getHeight());
		text_view.setGravity(Gravity.TOP);
		text_view.setText(stop_title);
		//text_view.setTextAppearance(mContext, android.R.attr.textAppearanceLarge);
		return text_view;
	}	
}
