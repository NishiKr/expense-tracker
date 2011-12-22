package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.DisplayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ExpenseSubListing extends ListingAbstract {

	private String idList;
	private TextView listingHeader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		idList = getIntent().getStringExtra("idList");
		listingHeader = (TextView) findViewById(R.id.expense_listing_header_title);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		mDataDateList = mConvertCursorToListString.getDateListString(false,idList);
		mSubList = mConvertCursorToListString.getListStringParticularDate(idList);
		
		if(mSubList.size() > 0){
		
			Bundle intentExtras = getIntent().getExtras();
			if(intentExtras != null){
				if(intentExtras.containsKey("toHighLight")){
					highlightID = intentExtras.getString("toHighLight");
				}
			}
			
			Calendar mTempCalendar = Calendar.getInstance();
			mTempCalendar.setTimeInMillis(mSubList.get(0).timeInMillis);
			mTempCalendar.set(mTempCalendar.get(Calendar.YEAR),mTempCalendar.get(Calendar.MONTH),mTempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
			mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			listingHeader.setText(new DisplayDate(mTempCalendar).getDisplayDateSubListingHeader());
			
			int j = 0;
			@SuppressWarnings("rawtypes")
			List listString = new ArrayList<List<DisplayList>>();
			for (int i = 0; i < mDataDateList.size(); i++) {
				List<DisplayList> mList = new ArrayList<DisplayList>();
				String date = mDataDateList.get(i).dateTime;
				while (j < mSubList.size()&& date.equals(mSubList.get(j).displayTime)) {
					DisplayList templist = new DisplayList();
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
					mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					templist = getListCurrentWeek(j);
					mList.add(templist);
					j++;
					if (j < mSubList.size()) {
					} else {
						break;
					}
				}
				listString.add(mList);
				@SuppressWarnings("rawtypes")
				List tt = (List) listString.get(i);
				mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<String>(this, R.layout.expense_listing, tt), mDataDateList);
			}
			doOperationsOnListview();
		} else {
			Intent mIntent = new Intent(this, ExpenseListing.class);
			mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mIntent);
			finish();
		}
	}
	
	@Override
	protected void unknownDialogAction(String userId) {
		super.unknownDialogAction(userId);
		Intent intentExpenseListing = new Intent(ExpenseSubListing.this, ExpenseSubListing.class);
		intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intentExpenseListing.putExtra("idList", idList);
		startActivity(intentExpenseListing);
	}
	
	@Override
	protected void noItemButtonAction(Button noItemButton) {
		super.noItemButtonAction(noItemButton);
		noItemButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
