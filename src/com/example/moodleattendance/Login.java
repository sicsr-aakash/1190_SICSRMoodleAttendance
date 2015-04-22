package com.example.moodleattendance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private Button btnLogin;
	private EditText edtTextUserName;
	private EditText edtTextPassword;
	private SharedPreferences pref;
	private String NEW_URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			try {
				NEW_URL = getResources().getString(R.string.host_auth);
			} catch (Exception e) {
				e.getMessage();
			}
			// Controls assignment
			btnLogin = (Button) findViewById(R.id.btnLogin);
			edtTextUserName = (EditText) findViewById(R.id.edtTextUserName);
			edtTextPassword = (EditText) findViewById(R.id.editTextPassword);

			// for storing credentials into internal memory
			pref = getApplicationContext().getSharedPreferences(
					"MoodleCrede"
					+ "ntials", 0);
			if (!pref.getString("username", "null").equals("null")) {
//				Toast.makeText(getApplicationContext(), "I am Here", Toast.LENGTH_LONG).show();
				LoginTask lgn = new LoginTask();
				lgn.execute(pref.getString("username", "null"),
						pref.getString("password", "null"));
			}

			btnLogin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					LoginTask lgn = new LoginTask();
					String user = edtTextUserName.getText().toString();
					String pass = edtTextPassword.getText().toString();
					lgn.execute(user, pass);
				}
			});			
		}
		else{
			Toast.makeText(getApplicationContext(), "Please check your internet connection and restart your application.", Toast.LENGTH_LONG).show();
		}
	}

	private class LoginTask extends AsyncTask<String, Void, String> {
		private URL url;
		Intent inet;
		Bundle bndl;

		@Override
		protected String doInBackground(String... arg0) {

			try {
				inet = new Intent(Login.this, SelectAvtivity.class);
				bndl = new Bundle();
				url = new URL(NEW_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();

				connection.setDoInput(true);
				connection.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(
						connection.getOutputStream());
				JSONObject json = new JSONObject();
				json.put("username", arg0[0]);
				json.put("password", arg0[1]);
				System.out.println("username");
				//System.out.println("#### This is the JSON written by Login.java"+json);
				writer.write(json.toString());
				writer.close();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line = "";
				int aaaaa=0;
				System.out.println("Reached here!!");
				while ((line = reader.readLine()) != null) {
					aaaaa++;
					//System.out.println("Read from the php line :"+aaaaa+"="+line);
					JSONObject obj = new JSONObject(line);
//					Log.e("--------------------------val-------------------------", obj.getString("userid"));
					bndl.putString("userid", obj.getString("userid"));
					//System.out.println("######LDAP"+line);					
					//System.out.println("This is what we return eventually :"+obj.getString("status"));
					return obj.getString("status");
				}
				reader.close();
			} catch (MalformedURLException e) {
				System.out.println("MalFormed"+e);
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IO"+e);
				e.printStackTrace();
			} catch (JSONException e) {
				System.out.println("JSON"+e);
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("??"+e);
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String status) {
			try {
				if (status.equals("OK")) {
					Toast.makeText(getApplicationContext(),
							"Login Successful", Toast.LENGTH_SHORT).show();
					
					Editor editor = pref.edit();
					editor.putString("username", edtTextUserName.getText()
							.toString());
					editor.putString("password", edtTextPassword.getText()
							.toString());
					editor.commit();
					inet.putExtras(bndl);
					startActivity(inet);
					finish();

				} else {
					Toast.makeText(getApplicationContext(),
							"Wrong credentials. Kindly try again",
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Check your network Connection",
						Toast.LENGTH_SHORT).show();
				// TODO: handle exception
			}

		}
	}
}
