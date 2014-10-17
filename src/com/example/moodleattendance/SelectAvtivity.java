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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SelectAvtivity extends Activity {

	private ListView lstView;
	private ArrayList<Integer> id;
	private ArrayList<String> name;

	private String NEW_URL;
	private boolean flagCourseCaptured = false;
	private String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		ActionBar actionBar = getActionBar();
		try {
			NEW_URL = getResources().getString(R.string.host_course);
			userid = getIntent().getExtras().getString("userid");
			NEW_URL = NEW_URL + "?userid=" + userid;
			//Toast.makeText(getApplicationContext(), NEW_URL, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.getMessage();
		}
		lstView = (ListView) findViewById(R.id.lstView);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			//Toast.makeText(getApplicationContext(), "Internet Working...", Toast.LENGTH_SHORT).show();

			try {
				if (getIntent().getExtras().getString("params") != null) {
					NEW_URL = NEW_URL
							+ getIntent().getExtras().getString("params");
					 //Toast.makeText(getApplicationContext(), NEW_URL, Toast.LENGTH_LONG).show();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			DisplayCoursesAsyncTask dc = new DisplayCoursesAsyncTask();
			dc.execute("null");

			lstView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (flagCourseCaptured == true) {
						Intent inet = new Intent(SelectAvtivity.this,
								Sessions.class);
						Bundle bndl = new Bundle();
						bndl.putString("sub", id.get(arg2).toString());
						bndl.putString("userid", userid);
						inet.putExtras(bndl);
						startActivity(inet);
						finish();
					} else {
						Intent inet = new Intent(SelectAvtivity.this,
								SelectAvtivity.class);
						Bundle bndl = new Bundle();
						String str = "&course=" + id.get(arg2).toString();
						bndl.putString("params", str);
						bndl.putString("userid", userid);
						inet.putExtras(bndl);
						startActivity(inet);
						finish();
					}
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "Please check your internet connection...",
					Toast.LENGTH_LONG).show();
		}
	}

	private class DisplayCoursesAsyncTask extends
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
			if (str != null) {
				try {
					Log.e("string", str);
					JSONObject ob = new JSONObject(str);
					if (ob.has("flag")) {
						flagCourseCaptured = true;
					}
					JSONObject obj = new JSONObject(ob.getString("courses"));
					Iterator it = (Iterator) obj.keys();

					name = new ArrayList<String>();
					id = new ArrayList<Integer>();

					while (it.hasNext()) {
						Object tp = it.next();
						id.add(new Integer(tp.toString()));
						name.add(obj.getString(tp.toString()));
					}
					CustomAdapter adapter = new CustomAdapter(
							getApplicationContext(), name);
					lstView.setAdapter(adapter);
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
		Intent intentObj;
		switch (item.getItemId()) {
		case R.id.action_home:
			intentObj = new Intent(SelectAvtivity.this, SelectAvtivity.class);
			Bundle bndl = new Bundle();
			bndl.putString("userid", userid);
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
			intentObj = new Intent(SelectAvtivity.this, Login.class);
			startActivity(intentObj);
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
