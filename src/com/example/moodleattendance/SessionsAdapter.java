package com.example.moodleattendance;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SessionsAdapter extends ArrayAdapter {
	private final Context context;
	private ArrayList<String> id;
	private ArrayList<String> group;
	private ArrayList<String> time;
	private ArrayList<String> desc;
	private ArrayList<String> img;
	
	@SuppressWarnings("unchecked")
	public SessionsAdapter(Context context, ArrayList id, ArrayList group, ArrayList time, ArrayList desc, ArrayList img){
		super(context, R.layout.session_items, id);
		this.context = context;
		this.id = id;
		this.group = group;
		this.time = time;
		this.desc = desc;
		this.img = img;
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.session_items, null);
		final ImageView imgView = (ImageView) rowView.findViewById(R.id.session_item_img_status);
		final TextView textViewGroup = (TextView) rowView.findViewById(R.id.session_items_group);
		final TextView textViewTime = (TextView) rowView.findViewById(R.id.session_items_time);
		final TextView textViewDesc = (TextView) rowView.findViewById(R.id.session_items_desc);
		
		if(img.get(position) == "taken"){
			imgView.setImageResource(R.drawable.taken);
		}
		else{
			imgView.setImageResource(R.drawable.not_taken);
		}
		textViewGroup.setText(group.get(position));
		textViewGroup.setTextColor(R.color.item_color);
		textViewGroup.setTextSize(15);
		textViewTime.setText(time.get(position));
		textViewTime.setTextColor(R.color.item_color);
		textViewTime.setTextSize(10);
		textViewDesc.setText(desc.get(position));
		textViewDesc.setTextColor(R.color.item_color);
		textViewDesc.setTextSize(15);
		return rowView;
	}
}
