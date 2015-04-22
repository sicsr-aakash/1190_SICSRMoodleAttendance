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
import android.content.pm.FeatureInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Attendance extends Activity {

    private String NEW_URL;
    private ArrayList<String> st_id;
    private ArrayList<String> st_username;
    private ArrayList<String> st_name;
    private ArrayList<String> st_att_status;
    private ArrayList<String> st_att_remarks;


    private ArrayList<String> st_status_values;//if attendance has already been taken
    
    private TextView txtViewUserFullName;
    private TextView txtViewUserUserName;
    private EditText edtTextRemarks;
   
    private Button btnPresent;
    private Button btnAbsent;
    private Button btnImgNext;
    private Button btnImgPrev;
    private int index = 0;
    private int max_index = 0;
    private String present_id;
    private String absent_id;
    private String status_set;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        btnPresent = (Button) findViewById(R.id.btnPresent);
        btnAbsent = (Button) findViewById(R.id.btnAbsent);
        txtViewUserFullName = (TextView)findViewById(R.id.txtViewUserFullName);
        txtViewUserUserName = (TextView)findViewById(R.id.txtViewUserUserName);
        edtTextRemarks = (EditText)findViewById(R.id.edtTextRemarks);
        btnImgNext = (Button)findViewById(R.id.btnImgNext);
        btnImgPrev = (Button)findViewById(R.id.btnImgPrev);
        st_att_status = new ArrayList<String>();
        st_att_remarks = new ArrayList<String>();
        st_status_values =new ArrayList<String>();

        try {
            // Toast.makeText(getApplicationContext(),
            // getIntent().getExtras().getString("userid") + " " +
            // getIntent().getExtras().getString("sessionid") + "   " +
            // getIntent().getExtras().getString("groupid"),
            // Toast.LENGTH_SHORT).show();
            NEW_URL = getResources().getString(R.string.host_course);
            
            //****************put if and else here incase things dont work out!!
            if(getIntent().getExtras().getString("groupid").equals("0"))
            {
            	NEW_URL = NEW_URL + "?students&zeroGroupid="+"0"+"&courseid="+getIntent().getExtras().getString("sub");
            }
            else
            {
            	NEW_URL = NEW_URL + "?students&groupid=" + getIntent().getExtras().getString("groupid");	
            }
            
            //***************
            
            st_id = new ArrayList<String>();
            st_username = new ArrayList<String>();
            st_name = new ArrayList<String>();


            GetUsers gu = new GetUsers();
            gu.execute("null");
           
            GetStatuses gs = new GetStatuses();
            gs.execute("null");
            ///////////////////////////////////////////////////
           ////////////////////////////////////////////////////
           ///////////////////////////////////////////////////
            GetStatusValues gsv = new GetStatusValues();
            if(getIntent().getExtras().getString("taken").equals("Yes"))
            {
            	System.out.println("####Entered gsv####");
            	gsv.execute("null");
            	System.out.println("####Exited gsv####");
            }
            System.out.println();
            //////////////////////////////////////////////////
            ///////////////////////////////////////////////////
            //////////////////////////////////////////////////
            //Toast.makeText(getApplicationContext(), String.valueOf(st_id.size()) + " " + String.valueOf(max_index) , Toast.LENGTH_SHORT).show();
            //changeColour(index);
            btnPresent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    st_att_status.add(present_id);        //last check point
                    st_att_remarks.add(edtTextRemarks.getText().toString());
                    index++;
                    if(index == max_index){
                        saveAttendance();
                    }
                    else{
                        txtViewUserFullName.setText(st_name.get(index));
                        txtViewUserUserName.setText(st_username.get(index));
                        edtTextRemarks.setText("");
                        if(getIntent().getExtras().getString("taken").equals("Yes"))
                        {
                        	changeColour(index);
                        }
                    }                    
                }
            });

            btnAbsent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    st_att_status.add(absent_id);
                    st_att_remarks.add(edtTextRemarks.getText().toString());
                    index++;
                    if(index == max_index){
                        saveAttendance();
                    }
                    else{
                        txtViewUserFullName.setText(st_name.get(index));
                        txtViewUserUserName.setText(st_username.get(index));
                        edtTextRemarks.setText("");
                        if(getIntent().getExtras().getString("taken").equals("Yes"))
                        {
                        	changeColour(index);
                        }
                        }
                }
            });
           
           
            btnImgNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    index++;
                    if(index == max_index || index > max_index){
                        index = max_index - 1;
                        Toast.makeText(getApplicationContext(), "End of list reached.", Toast.LENGTH_SHORT).show();
                    }else{
                        txtViewUserFullName.setText(st_name.get(index));
                        txtViewUserUserName.setText(st_username.get(index));
                        edtTextRemarks.setText("");
                        if(getIntent().getExtras().getString("taken").equals("Yes"))
                        {
                        	changeColour(index);
                        }
                        }
                }
            });
            btnImgPrev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(index == 0 || index < 0){
                        Toast.makeText(getApplicationContext(), "This is the First Student", Toast.LENGTH_SHORT).show();                       
                    }else{
                        index--;
                        txtViewUserFullName.setText(st_name.get(index));
                        txtViewUserUserName.setText(st_username.get(index));
                        edtTextRemarks.setText("");
                        if(getIntent().getExtras().getString("taken").equals("Yes"))
                        {
                        	changeColour(index);
                        }
                        }
                }
            });
           
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error processing request...", Toast.LENGTH_SHORT).show();
            System.out.println("####ERROR!!!!!"+e+"####");
        }

    } // end of on create
//**************************************
    void changeColour(int index)
    {
    	System.out.println("####"+st_status_values.get(index)+"  Change colour called");
    	System.out.println("Present id value="+present_id+"!!!!!");
    	System.out.println("Absent id value="+absent_id+"!!!!!");
    	try{   	
	    	if((st_status_values.get(index)).equals(present_id))
	    	{
	    		btnPresent.setText("Marked Present");
	    		btnAbsent.setBackgroundColor(Color.GRAY);
	    		btnPresent.setBackgroundColor(0xFF5DB05C);
	    		btnAbsent.setText("Absent");	    		
	    	}
	    	else if((st_status_values.get(index)).equals(absent_id))
	    	{
	    		btnAbsent.setText("Marked Absent");
	    		btnPresent.setBackgroundColor(Color.GRAY);
	    		btnAbsent.setBackgroundColor(0xFFD05A4E);
	    		btnPresent.setText("Present");
	    	}
    	}
    	catch(Exception e)
    	{
    		System.out.println("******");
    	}
    	
    }
    
//****************************************    
    void saveAttendance(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Attendance.this);
        builder.setTitle("Alert").setMessage("Do you want to save this attendance.").setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idd) {
                                try {
	                                    SaveAttendance sa = new SaveAttendance();
	                                    String str = getResources().getString(R.string.host_course);
	                                    str = str + "?modify&session_id=" + getIntent().getExtras().getString("sessionid") + "&userid=" + getIntent().getExtras().getString("userid");
	                                    sa.execute(str);
	                                    if(getIntent().getExtras().getString("taken").equals("No"))
	                                    {
		                                    for(int i=0;i<st_att_status.size();i++)
		                                    {
		                                    	
		                                        str = getResources().getString(R.string.host_course);
		                                        str = str + "?add&session_id=" + getIntent().getExtras().getString("sessionid") + "&studentid=" + st_id.get(i) + "&statusid=" + st_att_status.get(i) + "&statusset=" + status_set + "&takenby=" + getIntent().getExtras().getString("userid") + "&remarks=" + st_att_remarks.get(i);
		                                        sa = new SaveAttendance();
		                                        System.out.println("#### add attendance url"+str+"####");
		                                        sa.execute(str);                                        
		                                        str = "";
		                                    }
	                                    }
	                                    else if((getIntent().getExtras().getString("taken").equals("Yes")))
	                                    {
	                                        for(int i=0;i<st_att_status.size();i++)
	                                        {
	                                        	str = getResources().getString(R.string.host_course);			                                      
	                                        	str = str + "?updateAttendance&session_id=" + getIntent().getExtras().getString("sessionid") + "&studentid=" + st_id.get(i) + "&statusid=" + st_att_status.get(i) + "&statusset=" + status_set + "&takenby=" + getIntent().getExtras().getString("userid") + "&remarks=" + st_att_remarks.get(i);
	                                            sa = new SaveAttendance();
	                                            System.out.println("#### update attendance url "+str+"####");
	                                            sa.execute(str);                                        
	                                            str = "";
	                                        }
                                    }
                                    ////////////////////////////////////////////
                                    //if and else here is important
                                    ////////////////////////////////////////////
                                    Toast.makeText(getApplicationContext(), "Attendance saved successfully", Toast.LENGTH_SHORT).show();
                                    Intent intentObj = new Intent(Attendance.this, Sessions.class);
                                    Bundle bndl = new Bundle();
                                    bndl.putString("sub", getIntent().getExtras().getString("sub"));
                                    bndl.putString("userid", getIntent().getExtras().getString("userid"));
                                    intentObj.putExtras(bndl);
                                    startActivity(intentObj);
                                    finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();
                                try {
                                    Intent intentObj = new Intent(Attendance.this, Sessions.class);
                                    Bundle bndl = new Bundle();
                                    bndl.putString("sub", getIntent().getExtras().getString("sub"));
                                    bndl.putString("userid",
                                            getIntent().getExtras().getString("userid"));
                                    intentObj.putExtras(bndl);
                                    startActivity(intentObj);
                                    finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }// end of function saveAttendance
   
   
    private class GetUsers extends AsyncTask<String, Void, String> {
        private URL url;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(NEW_URL);
                System.out.println("#### Url for GetUsers = "+NEW_URL+" ####");
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
                System.out.println("####return = "+ln+" ####");
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
                if (str.equals("[]")) {
                    try{
                        Toast.makeText(getApplicationContext(), "No students found in this group,navigating back to Sessions",
                                Toast.LENGTH_SHORT).show();
                        Intent intentObj = new Intent(Attendance.this, Sessions.class);
                        Bundle bndl = new Bundle();
                        bndl.putString("sub", getIntent().getExtras().getString("sub"));
                        bndl.putString("userid",
                                getIntent().getExtras().getString("userid"));
                        intentObj.putExtras(bndl);
                        startActivity(intentObj);
                        finish();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        JSONObject ob = new JSONObject(str);
                        Iterator it = (Iterator) ob.keys();

                        while (it.hasNext()) {
                            Object tp = it.next();
                            JSONObject obj = new JSONObject(ob.getString(tp
                                    .toString()));
                            st_id.add(tp.toString());
                            st_username.add(obj.getString("username"));
                            st_name.add(obj.getString("name"));
                        }
                        System.out.println("####id , UserName , names added to ArrayList ####");
                        max_index = st_id.size();
                        txtViewUserFullName.setText(st_name.get(index));
                        txtViewUserUserName.setText(st_username.get(index));
                        //Toast.makeText(getApplicationContext(), String.valueOf(st_id.size()) , Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    } // end of GetUsers inner class
   
    //inner class for fetching status
    private class GetStatuses extends AsyncTask<String, Void, String> {
        private URL url;

        @Override
        protected String doInBackground(String... params) {
            try {
                NEW_URL = getResources().getString(R.string.host_course);
                NEW_URL = NEW_URL + "?fetchstatus&sub=" + getIntent().getExtras().getString("sub");
                System.out.println("####Url for GetStatuses = "+NEW_URL+" ####");
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
                System.out.println("####return = "+ln+" ####");
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
                    JSONObject ob = new JSONObject(str);
                    present_id = ob.getString("P");
                    absent_id = ob.getString("A");
                    System.out.println("####present_id(Attendance:line 338) = "+present_id+" ####");
                    System.out.println("####absent_id = "+absent_id+" ####");
                    int temp = Integer.parseInt(absent_id);
                    status_set = present_id + "," + String.valueOf((temp + 1)) + "," + String.valueOf((temp + 2)) + "," + absent_id;
                    System.out.println("####status_set = "+status_set+" ####");
                    //JSONObject ob = new JSONObject(str);
                    //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    } // end of SaveAttendance inner class
//**********************************************************************

private class GetStatusValues extends AsyncTask<String, String, String> {
        private URL url;

        @Override
        protected String doInBackground(String... params) {
            try {
                NEW_URL = getResources().getString(R.string.host_course);
                NEW_URL = NEW_URL + "?fetchstatusvalues&sub=" + getIntent().getExtras().getString("sub") + "&sessionid="+getIntent().getExtras().getString("sessionid");
                System.out.println("####Url for GetStatusvalues = "+NEW_URL+" ####");
                url = new URL(NEW_URL);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                System.out.println("#### Connection Opened = "+NEW_URL+" ####");
                
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line = "";
                String ln = "";
                while ((line = reader.readLine()) != null) {
                	System.out.println("#### line by line= "+line+" ####");
                     
                	ln = ln + line;
                }
                reader.close();
                System.out.println("#### return = "+ln+" ####");
                publishProgress(ln);
                return null;
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
        
        protected void onProgressUpdate(String... string) {
        	System.out.println("####Entered onProgressUpdate = "+NEW_URL+" ####");             
        	String str = string[0];
        	System.out.println("######(((");
        	if (str != null) {
            	try {

               	//Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    JSONObject ob = new JSONObject(str);
                    Iterator it = (Iterator) ob.keys();
                    while (it.hasNext()) {
                        Object tp = it.next();
                        JSONObject obj = new JSONObject(ob.getString(tp
                                .toString()));
                        st_status_values.add(obj.getString("statusid"));;
                    }
                    System.out.println("#### While loop completed");
                    if(getIntent().getExtras().getString("taken").equals("Yes"))
                    {
                    	changeColour(index);
                    }
                    }
                    catch (JSONException e) {
                    //e.printStackTrace();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }        	
        }
        
        protected void onPostExecute(String str) {
        	        }
    } // end of SaveAttendance inner class
    
//**********************************************************************
    
   
    //inner class for saving attendance status and update attendance session
    private class SaveAttendance extends AsyncTask<String, Void, String> {
        private URL url;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(params[0]);
                System.out.println("####Url for SaveAttendance = "+url+" ####");
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
                System.out.println("#### return = "+ln+" ####");
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
                    //JSONObject ob = new JSONObject(str);
                    //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                } /*catch (JSONException e) {
                    e.printStackTrace();
                } */catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    } // end of SaveAttendance inner class
   
   
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_home:
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    Attendance.this);
            builder.setTitle("Alert")
                    .setMessage("Are you sure you want to cancel this session.")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int idd) {
                                    try {
                                        Intent intentObj = new Intent(
                                                Attendance.this,
                                                SelectAvtivity.class);
                                        Bundle bndl = new Bundle();
                                        bndl.putString("userid", getIntent()
                                                .getExtras()
                                                .getString("userid"));
                                        intentObj.putExtras(bndl);
                                        startActivity(intentObj);
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        case R.id.action_logout:
            final AlertDialog.Builder builder2 = new AlertDialog.Builder(
                    Attendance.this);
            builder2.setTitle("Alert")
                    .setMessage("Are you sure you want to cancel taking attendace ?, Warning : All inputed data will be lost.")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int idd) {
                                    try {
                                        SharedPreferences pref = getApplicationContext()
                                                .getSharedPreferences(
                                                        "MoodleCredentials", 0);
                                        Editor editor = pref.edit();
                                        editor.putString("username", "null");
                                        editor.putString("password", "null");
                                        editor.commit();
                                        Intent intentObj = new Intent(
                                                Attendance.this, Login.class);
                                        startActivity(intentObj);
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert2 = builder2.create();
            alert2.show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    } // end of onOptionsItemSelected method
} // end of Attendance class
