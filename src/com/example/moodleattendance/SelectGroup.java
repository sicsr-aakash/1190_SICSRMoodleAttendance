package com.example.moodleattendance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SelectGroup extends Activity {

	private ListView lstViewGroups;
	private ArrayList<Integer> id;
	private ArrayList<String> name;
	private String NEW_URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_group);
		try {
			NEW_URL = getResources().getString(R.string.host_course);
		} catch (Exception e) {
			e.getMessage();
		}
		lstViewGroups = (ListView) findViewById(R.id.lstViewGroups);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			//Toast.makeText(getApplicationContext(), "Internet Working...", Toast.LENGTH_SHORT).show();
			try {
				if (getIntent().getExtras().getString("sub") != null) {
					
					NEW_URL = NEW_URL + "?groups&sub="
							+ getIntent().getExtras().getString("sub");
					// Toast.makeText(getApplicationContext(), NEW_URL,
					// Toast.LENGTH_LONG).show();
					System.out.println("#### This is the URL in selectGroup :"+NEW_URL);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			DisplayGroupsAsyncTask dc = new DisplayGroupsAsyncTask();
			dc.execute("null");

			lstViewGroups.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent inet = new Intent(SelectGroup.this,
							CreateSession.class);
					Bundle bndl = new Bundle();
					bndl.putString("sub",
							getIntent().getExtras().getString("sub"));
					bndl.putString("group", id.get(arg2).toString());
					Toast.makeText(getApplicationContext(),"Group "+name.get(arg2) +" has been Selected",Toast.LENGTH_LONG).show();					
					try{
						bndl.putString("userid",
								getIntent().getExtras().getString("userid"));
					}
					catch(Exception e){
						e.printStackTrace();
					}
					inet.putExtras(bndl);
					startActivity(inet);
					finish();
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "Please check your internet connection...", Toast.LENGTH_LONG).show();
		}
		
	}

	private class DisplayGroupsAsyncTask extends
			AsyncTask<String, Void, String> {
		private URL url;

		@Override
		protected String doInBackground(String... params) {
			
			try {
				url = new URL(NEW_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line = reader.readLine();
				reader.close();
				return line;
				
				
				
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		protected void onPostExecute(String str) {
			if(str.endsWith("[]}"))
			{
				Toast.makeText(getApplicationContext(),"Default Group has been selected Automatically",Toast.LENGTH_LONG).show();
				
					Intent inet = new Intent(SelectGroup.this,
							CreateSession.class);
					Bundle bndl = new Bundle();
					try{

						bndl.putString("sub",
								getIntent().getExtras().getString("sub"));
						bndl.putString("group","0");
						bndl.putString("userid",
								getIntent().getExtras().getString("userid"));
					}
					catch(Exception e){
						e.printStackTrace();
					}
					inet.putExtras(bndl);
					startActivity(inet);
					finish();
			}			
			else if (str != null) {
				try {
					JSONObject ob = new JSONObject(str);
					JSONObject obj = new JSONObject(ob.getString("groups"));
					Iterator it = (Iterator<?>) obj.keys();

					name = new ArrayList<String>();
					id = new ArrayList<Integer>();
					
					System.out.println(id.toString());
					
					while (it.hasNext()) {
						Object tp = it.next();
						id.add(new Integer(tp.toString()));							
						name.add(obj.getString(tp.toString()));
					}				
					CustomAdapter adapter = new CustomAdapter(
							getApplicationContext(), name);
					lstViewGroups.setAdapter(adapter);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
				
			
			
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch (item.getItemId()) {
			case R.id.action_home:
				Intent intentObj = new Intent(SelectGroup.this,
						SelectAvtivity.class);
				Bundle bndl = new Bundle();
				bndl.putString("userid",
						getIntent().getExtras().getString("userid"));
				intentObj.putExtras(bndl);
				startActivity(intentObj);
				finish();
				
				return true;
			case R.id.action_logout:
				SharedPreferences pref = getApplicationContext()
						.getSharedPreferences("MoodleCredentials", 0);
				Editor editor = pref.edit();
				editor.putString("username", "null");
				editor.putString("password", "null");
				editor.commit();
				intentObj = new Intent(SelectGroup.this, Login.class);
				startActivity(intentObj);
				finish();
			default:
				
				return super.onOptionsItemSelected(item);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return true;
	}
}
