package com.doLast.doGRT;

import android.content.Context;
import android.text.Html;
import android.view.View;
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
		text_view.setText(Html.fromHtml("<font color=\"grey\"><small>" + days[position] + "</small></color>"+ "<br>" + 
										"<font color=\"black\"><x-large>" + stop_title + "</x-large></color>"));
		text_view.setTextAppearance(mContext, android.R.attr.textAppearanceLarge);
		return text_view;
	}
	
	
}
