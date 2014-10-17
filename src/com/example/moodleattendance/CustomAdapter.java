package com.example.moodleattendance;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
@SuppressWarnings("rawtypes")
public class CustomAdapter extends ArrayAdapter {
	private final Context context;
	private ArrayList<String> values;
	
	public CustomAdapter(Context context, ArrayList values){
		super(context, R.layout.list_view_item, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_view_item, null);
		final TextView textView = (TextView) rowView.findViewById(R.id.lst_view_item);
		textView.setText(values.get(position));
		textView.setHeight(70);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(R.color.item_color);
		textView.setTextSize(20);
		return rowView;
	}
	
}
