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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Sessions extends Activity {

	private Button btnCreateSession;
	private ListView lstViewSessions;
	private String NEW_URL;
	private ArrayList<String> id;
	private ArrayList<String> group;
	private ArrayList<String> time;
	private ArrayList<String> desc;
	private ArrayList<String> img;
	private ArrayList<String> groupid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
		btnCreateSession = (Button) findViewById(R.id.btnAddNewSession);
		lstViewSessions = (ListView) findViewById(R.id.lstViewSessions);

		try {
			NEW_URL = getResources().getString(R.string.host_course);
			NEW_URL = NEW_URL + "?sub="
					+ getIntent().getExtras().getString("sub");
			// Toast.makeText(getApplicationContext(), NEW_URL,
			// Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.getMessage();
		}

		GetSessionsAsyncTask getcrs = new GetSessionsAsyncTask();
		getcrs.execute("null");


		btnCreateSession.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					Intent inet = new Intent(Sessions.this, SelectGroup.class);
					Bundle bndl = new Bundle();
					bndl.putString("sub",
							getIntent().getExtras().getString("sub"));
					bndl.putString("userid",
							getIntent().getExtras().getString("userid"));
					inet.putExtras(bndl);
					startActivity(inet);
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		lstViewSessions.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				try{
					if(img.get(arg2) == "taken"){
						AlertDialog.Builder builder = new AlertDialog.Builder(Sessions.this);
				        builder.setTitle("Alert")
				        .setMessage("The attendance has already been taken.")
				        .setCancelable(false)
				        .setNegativeButton("Okay",new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				            }
				        });
				        AlertDialog alert = builder.create();
				        alert.show();
					}
					else{
						AlertDialog.Builder builder = new AlertDialog.Builder(Sessions.this);
				        builder.setTitle("Alert")
				        .setMessage("Are you sure you want to take attendance.")
				        .setCancelable(false)
				        .setPositiveButton("Continue",new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int idd) {
				            	try {
									Intent inet = new Intent(Sessions.this, Attendance.class);
									Bundle bndl = new Bundle();
									bndl.putString("sub",
											getIntent().getExtras().getString("sub"));
									bndl.putString("userid",
											getIntent().getExtras().getString("userid"));
									bndl.putString("sessionid", id.get(arg2));
									bndl.putString("groupid", groupid.get(arg2));
									inet.putExtras(bndl);
									startActivity(inet);
									finish();
								} catch (Exception e) {
									e.printStackTrace();
								}
				            	//Toast.makeText(getApplicationContext(), id.get(arg2)  + " " + getIntent().getExtras().getString("userid"), Toast.LENGTH_SHORT).show();
				            }
				        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
				                dialog.cancel();
							}
						});
				        
				        AlertDialog alert = builder.create();
				        alert.show();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	private class GetSessionsAsyncTask extends AsyncTask<String, Void, String> {
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
				String line = "";
				String ln = "";
				while ((line = reader.readLine()) != null) {
					ln = ln + line;
				}
				reader.close();
				return ln;
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
					//Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
					JSONObject ob = new JSONObject(str);
					Iterator it = (Iterator) ob.keys();

					id = new ArrayList<String>();
					group = new ArrayList<String>();
					time = new ArrayList<String>();
					desc = new ArrayList<String>();
					img = new ArrayList<String>();
					groupid = new ArrayList<String>();

					while (it.hasNext()) {
						Object tp = it.next();
						JSONObject obj = new JSONObject(ob.getString(tp
								.toString()));
						id.add(tp.toString());
						group.add(obj.getString("group"));
						time.add(obj.getString("time"));
						desc.add(obj.getString("desc"));
						if(obj.getString("lasttakenby").equals("0")){
							img.add("not_taken");
						}
						else{
							img.add("taken");
						}
						groupid.add(obj.getString("groupid"));
					}

					SessionsAdapter adapter = new SessionsAdapter(
							getApplicationContext(), id, group, time, desc, img);
					lstViewSessions.setAdapter(adapter);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
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
		try {
			switch (item.getItemId()) {
			case R.id.action_home:
				Intent intentObj = new Intent(Sessions.this,
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
				intentObj = new Intent(Sessions.this, Login.class);
				startActivity(intentObj);
				finish();
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (Exception e) {

		}
		return true;
	}
}
