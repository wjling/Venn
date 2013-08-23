package com.app.addActivityPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.app.catherine.R;
import com.app.utils.ListViewUtility;

import android.R.layout;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AdapterForTimeItem extends BaseAdapter
{
	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	private int layoutId;
	private String key[];
	private int viewID[];
	private LayoutInflater mInflater;
	private Set<TimeStruct> originalList;
	private AdapterForTimeItem adapter = this;
	private int timeListID;
	
	public AdapterForTimeItem(Context context, ArrayList<HashMap<String, Object>> list, 
			int layoutID, String key[], int viewID[], Set<TimeStruct> dateList, int timeListLayoutId) 
	{
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.list = list;
		this.layoutId = layoutID;
		this.key = key;
		this.viewID = viewID;
		this.originalList = dateList;
		this.timeListID = timeListLayoutId;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return list.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int rows, View v, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = mInflater.inflate(layoutId, null);

		int keyCount = key.length;
		Button button = (Button)view.findViewById(R.id.deleteTimeContentBtn);
		button.setTag(rows);
		button.setFocusable(false);
		button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt( v.getTag().toString() );
				HashMap<String, Object> timeItem = list.get(position);
				TimeStruct toDelete = (TimeStruct)timeItem.get("activityDate");
				originalList.remove(toDelete);
				
				list.remove(position);
				adapter.notifyDataSetChanged();
				
			}
		});
		
		for (int i = 0; i < keyCount; i++) {
			if (view.findViewById(viewID[i]) instanceof TextView) {	
				TextView tView = (TextView)view.findViewById(viewID[i]);
				tView.setText(  (String)list.get(rows).get(key[i]) );
			}
		}
		
		return view;
	}

}
