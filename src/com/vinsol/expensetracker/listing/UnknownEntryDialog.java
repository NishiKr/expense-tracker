/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;

public class UnknownEntryDialog extends Dialog implements android.view.View.OnClickListener {

	private Button deleteButton;
	private Button textEntryButton;
	private Button voiceEntryButton;
	private Button cameraEntryButton;
	private Button favoriteEntryButton;
	private Button cancelButton;
	private Bundle bundle;
	private DatabaseAdapter mDatabaseAdapter;
	private Entry mTempClickedList;
	private TextView headerTextView;
	private TextView locationTextView;
	
	protected void onCreateDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.unknown_entry_dialog);
		textEntryButton = (Button) findViewById(R.id.main_text);
		deleteButton = (Button) findViewById(R.id.unknown_entry_dialog_delete);
		voiceEntryButton = (Button) findViewById(R.id.main_voice);
		cameraEntryButton = (Button) findViewById(R.id.main_camera);
		favoriteEntryButton = (Button) findViewById(R.id.main_favorite);
		cancelButton = (Button) findViewById(R.id.unknown_entry_dialog_cancel);
		headerTextView = (TextView) findViewById(R.id.unknown_entry_dialog_header_title);
		locationTextView = (TextView) findViewById(R.id.unknown_entry_dialog_location);
		bundle = new Bundle();
	}
	
	public UnknownEntryDialog(Entry toInsert,Context mContext, android.view.View.OnClickListener myClickListener) {
		super(mContext);
		onCreateDialog();
		textEntryButton.setOnClickListener(myClickListener);
		deleteButton.setVisibility(View.GONE);
		voiceEntryButton.setOnClickListener(myClickListener);
		cameraEntryButton.setOnClickListener(myClickListener);
		favoriteEntryButton.setOnClickListener(myClickListener);
		cancelButton.setOnClickListener(myClickListener);
		
		if(toInsert.location != null)
			if(!toInsert.location.equals("")) {
				locationTextView.setText(toInsert.location);
			}
		
		if(toInsert.timeInMillis != null) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(toInsert.timeInMillis);
			headerTextView.setText(new DisplayDate(mCalendar).getDisplayDate());
		}
		show();
	}
	
	public UnknownEntryDialog(Context mContext,Entry _list,android.view.View.OnClickListener deleteClickListener) {
		super(mContext);
		onCreateDialog();
		textEntryButton.setOnClickListener(this);
		deleteButton.setOnClickListener(deleteClickListener);
		voiceEntryButton.setOnClickListener(this);
		cameraEntryButton.setOnClickListener(this);
		favoriteEntryButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		mDatabaseAdapter = new DatabaseAdapter(getContext());
		mTempClickedList = _list;
		
		if(mTempClickedList.location != null)
			if(mTempClickedList.location != "") {
				locationTextView.setText(mTempClickedList.location);
			}
		
		if(mTempClickedList.timeInMillis != null) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(mTempClickedList.timeInMillis);
			headerTextView.setText(new DisplayDate(mCalendar).getDisplayDate());
		}
		show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.main_text:
				Intent intentTextEntry = new Intent(getContext(), TextEntry.class);
				editDatabase(R.string.text);
				bundle.putParcelable("mDisplayList", mTempClickedList);
				intentTextEntry.putExtras(bundle);
				getContext().startActivity(intentTextEntry);
				dismiss();
				break;
				
			case R.id.unknown_entry_dialog_delete:
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteEntryTableEntryID(mTempClickedList.id);
				mDatabaseAdapter.close();
				dismiss();
				break;
				
			case R.id.main_voice:
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					Intent intentVoice = new Intent(getContext(), Voice.class);
					editDatabase(R.string.voice);
					bundle.putParcelable("mDisplayList", mTempClickedList);
					intentVoice.putExtras(bundle);
					getContext().startActivity(intentVoice);
					dismiss();
				} else {
					Toast.makeText(getContext(), "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case R.id.main_camera:
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					Intent intentCamera = new Intent(getContext(), CameraActivity.class);
					editDatabase(R.string.camera);
					bundle.putParcelable("mDisplayList", mTempClickedList);
					intentCamera.putExtras(bundle);
					getContext().startActivity(intentCamera);
					dismiss();
				} else {
					Toast.makeText(getContext(), "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case R.id.main_favorite:
				Intent intentFavorite = new Intent(getContext(), FavoriteActivity.class);
				bundle.putParcelable("mDisplayList", mTempClickedList);
				intentFavorite.putExtras(bundle);
				getContext().startActivity(intentFavorite);
				dismiss();
				break;
				
			case R.id.unknown_entry_dialog_cancel:
				cancel();
				break;
				
			default:
				break;
		}
	}
	
	private void editDatabase(int type) {
		Entry list = new Entry();
		list.id = mTempClickedList.id;
		list.type = getContext().getString(type);
		mDatabaseAdapter.open();
		mDatabaseAdapter.editEntryTable(list);
		mDatabaseAdapter.close();
	}
}
