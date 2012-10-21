package com.doLast.doGRT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;


public class MyDialogFragment extends SherlockDialogFragment {
	private final static String DIALOG_ID = "id";
	private final static String STOP_ID = "stop_id";
	private final static String STOP_TITLE = "stop_title";
	
	// For dialog creation
	public static final int EDIT_DIALOG_ID = 0;
	public static final int DELETE_DIALOG_ID = 1;
	
    /**
     * Create a new instance of MyDialogFragment, providing ids
     * as an argument.
     */
    static MyDialogFragment newInstance(int dialog_id, String stop_id, String stop_title) {
        MyDialogFragment f = new MyDialogFragment();

        // Supply id of the dialog input as an argument.
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID, dialog_id);
        args.putString(STOP_ID, stop_id);
        args.putString(STOP_TITLE, stop_title);
        f.setArguments(args);

        return f;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int id = getArguments().getInt(DIALOG_ID);
        final int stop_id = Integer.parseInt(getArguments().getString(STOP_ID));
        final String stop_title = getArguments().getString(STOP_TITLE);
        
        Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        switch(id) {
        case EDIT_DIALOG_ID:
        	final EditText input = new EditText(getActivity());
        	input.setText(stop_title);
        	builder.setTitle(R.string.edit_dialog_title)
        		.setView(input)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContentValues values = new ContentValues();
						values.put(UserBusStopsColumns.TITLE, input.getText().toString());
						getActivity().getContentResolver().update(UserBusStopsColumns.CONTENT_URI, 
								values, UserBusStopsColumns.STOP_ID + " = " + stop_id, null);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
        	dialog = builder.create();
        	break;
        case DELETE_DIALOG_ID:
        	builder.setTitle(R.string.delete_dialog_title)
        	.setMessage("Delete " + stop_title + "?")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
	        	    getActivity().getContentResolver().delete(UserBusStopsColumns.CONTENT_URI, 
	        	            UserBusStopsColumns.STOP_ID + " = " + stop_id, null);
	        	    
	        	    // Only for route activity to update its option menu
	        	    // Kinda weird but it works...
	        	    if (getActivity().getClass().getName() == RoutesActivity.class.getName()) ((RoutesActivity)(getActivity())).updateOptionMenu();	        	    
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing
				}
			});
    	dialog = builder.create();
        	break;
        default:
        	Log.v("Unknown fragment dialog", "id: " + id);
        }
        
        return dialog;
    }
}
