package com.example.moodleattendance;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AttendanceOverview extends Activity {

	private Spinner course;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	
        setContentView(R.layout.attendance_overview);
        addItemsToCourse();
        
    }

    
    public void addItemsToCourse(){
    	course = (Spinner)findViewById(R.id.course);
    	List<String> courseList = new ArrayList<String>();
    	courseList.add("JAVA");
    	courseList.add("OS");
    	courseList.add("PHP");
    	courseList.add("WEB 2.0");
    	courseList.add("SA");
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,courseList);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	course.setAdapter(dataAdapter);
    }
 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select, menu);
        return true;
    }    
}
