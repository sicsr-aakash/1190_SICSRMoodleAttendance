
package com.example.moodleattendance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditSession extends Activity {

	private Button btnCalendar, btnTimePicker, btnEditSession;
	private int mYear, mMonth, mDay, mHour, mMinute;
	private String NEW_URL, strDate, strTime;
	private EditText edtTextSessionDescription;
	private Spinner spinnerHour, spinnerMinute;
	private String strDuration;
	
	/////////////////////
	private String id,date,timee,desc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.create_session);
		
		btnCalendar = (Button) findViewById(R.id.btnCalendar);		
		btnTimePicker = (Button) findViewById(R.id.btnTimePicker);
		btnEditSession = (Button) findViewById(R.id.btnCreateSession);
		edtTextSessionDescription = (EditText) findViewById(R.id.edtTextSessionDescription);		
		spinnerHour = (Spinner) findViewById(R.id.spinnerHourDur);		
		spinnerMinute = (Spinner) findViewById(R.id.spinnerMinuteDur);
		
		id=getIntent().getExtras().getString("id");
		date=getIntent().getExtras().getString("date");
		timee=getIntent().getExtras().getString("timee");
		desc=getIntent().getExtras().getString("desc");
		
		
		strDate=date;
		strTime=timee;
		
		btnCalendar.setText(date);
		btnTimePicker.setText(timee);
		edtTextSessionDescription.setText(desc);
		btnEditSession.setText("Edit Session");
		spinnerHour.setSelection(10);
		//System.out.println("##### id="+id+"#### time="+time+"#### desc="+desc);
		
		try {
			NEW_URL = getResources().getString(R.string.host_course);
		} catch (Exception e) {
			e.getMessage();
		}
		
		AddSession gses = new AddSession();		
		//gses.execute(id,"get");
		////////////////
		
		if(getIntent().hasExtra("flag") && getIntent().getIntExtra("flag", 0) == 1) {
			//came from Sessions, populate views
		}
		try {
			NEW_URL = getResources().getString(R.string.host_course);
		} catch (Exception e) {
			e.getMessage();
		}
		

		List<String> lstArray;
		ArrayAdapter<String> adapter;

		lstArray = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, lstArray);
		for (int i = 1; i <= 12; i++) {
			lstArray.add(String.valueOf(i));
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerHour.setAdapter(adapter);

		lstArray = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, lstArray);
		for (int i = 1; i <= 60; i++) {
			lstArray.add(String.valueOf(i));
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMinute.setAdapter(adapter);

		
		/* Long Click */
		OnLongClickListener longListener = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		btnCalendar.setOnLongClickListener(longListener);
		
		
		
		
		btnCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);

				try {
					DatePickerDialog dpd = new DatePickerDialog(arg0
							.getContext(),
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									// Display Selected date in textbox
									btnCalendar.setText(dayOfMonth + "-"
											+ (monthOfYear + 1) + "-" + year);
									strDate = year + "-" + (monthOfYear + 1)
											+ "-" + dayOfMonth;
								}
							}, mYear, mMonth, mDay);
					dpd.show();
				} catch (Exception e) {
					e.getMessage();
				}
			}
		});

		btnTimePicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final Calendar c = Calendar.getInstance();
				mHour = c.get(Calendar.HOUR_OF_DAY);
				mMinute = c.get(Calendar.MINUTE);

				// Launch Time Picker Dialog
				TimePickerDialog tpd = new TimePickerDialog(arg0.getContext(),
						new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								// Display Selected time in textbox
								btnTimePicker.setText(hourOfDay + ":" + minute);
								strTime = hourOfDay + ":" + minute + ":00";
							}
						}, mHour, mMinute, false);
				tpd.show();
			}
		});

		btnEditSession.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				strDuration = String.valueOf(((Integer.parseInt(spinnerHour
						.getSelectedItem().toString()) * 60) + Integer
						.parseInt(spinnerMinute.getSelectedItem().toString())) * 60);
				
				System.out.println("This is the Spinner Hour ###"+strDuration);
				
				try {
					NEW_URL = NEW_URL
							+ "?create&sub="
							+ getIntent().getExtras().getString("sub")
							+ "&updatee="
							+ "TRUE"
							+ "&group="
							+ getIntent().getExtras().getString("group")
							+ "&sdate="
							+ strDate
							+ "&stime="
							+ strTime
							+ "&sduration="
							+ strDuration
							+ "&id="
							+ id
							+ "&desc="
							+ java.net.URLEncoder.encode(
									edtTextSessionDescription.getText()
											.toString(), "UTF-8");
					//Toast.makeText(getApplicationContext(), "in try", Toast.LENGTH_LONG).show();
					System.out.println(NEW_URL+"SPARTA!!");
				} catch (UnsupportedEncodingException e) {
					//Toast.makeText(getApplicationContext(), "in catch", Toast.LENGTH_LONG).show();
					e.printStackTrace();
					 
				}
				// Toast.makeText(getApplicationContext(), NEW_URL,
				// Toast.LENGTH_LONG).show();
				//Toast.makeText(getApplicationContext(), NEW_URL, Toast.LENGTH_LONG).show();
				Log.i("---------------query-------------", NEW_URL);
				AddSession ses = new AddSession();
			//	Toast.makeText(getApplicationContext(), "Session has been deleted", Toast.LENGTH_LONG).show();
			//	System.out.println("Just before ses.execute**************");
				ses.execute("null");
				//Toast.makeText(getApplicationContext(), "after add session", Toast.LENGTH_LONG).show();
			//	System.out.println("Just after ses.execute ************");
			}
		});
		// Toast.makeText(getApplicationContext(), "sub id is" +
		// getIntent().getExtras().getString("sub")+ "group id is" +
		// getIntent().getExtras().getString("group"),
		// Toast.LENGTH_LONG).show();

	}

	private class AddSession extends AsyncTask<String, Void, String> {
		private URL url;

		@Override
		protected String doInBackground(String... params) {
			try {
				//Toast.makeText(getApplicationContext(), "inside do in BackGround", Toast.LENGTH_LONG).show();
				url = new URL(NEW_URL);
				System.out.println("#### This is the URl : "+url);
				//Log.e("---------------query-------------","above");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
								BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line = reader.readLine();
				JSONObject ob = new JSONObject(line);
				reader.close();
				return ob.getString("status");
			    }
				catch (NullPointerException e) {
				//Toast.makeText(getApplicationContext(), "in catch NullPointerException", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (MalformedURLException e) {
				//Toast.makeText(getApplicationContext(), "in catch MalformedURLException", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				//Toast.makeText(getApplicationContext(), "in catch IOException", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (Exception e) {
				//Toast.makeText(getApplicationContext(), "in catch Exception", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
			
			
			return null;
		}

		protected void onPostExecute(String status) {
			try {
				if (status.equals("OK")) {
					Toast.makeText(getApplicationContext(), "Session has been Modified appropriately",
							Toast.LENGTH_SHORT).show();
					Intent inet = new Intent(EditSession.this, Sessions.class);
					Bundle bndl = new Bundle();
					bndl.putString("sub",
							getIntent().getExtras().getString("sub"));
					bndl.putString("userid",
							getIntent().getExtras().getString("userid"));
					inet.putExtras(bndl);
					startActivity(inet);
					finish();

				} else
				{
					System.out.println("Status of on postExecute : "+status);
				}
			} catch (Exception e) {
				e.getMessage();
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
				Intent intentObj = new Intent(EditSession.this,
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
				intentObj = new Intent(EditSession.this, Login.class);
				startActivity(intentObj);
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://");
				intentObj.putExtras(bundle);
				finish();
				
				//retrieving
				getIntent().getExtras().getString("url");
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (Exception e) {

		}
		return true;
	}
}
