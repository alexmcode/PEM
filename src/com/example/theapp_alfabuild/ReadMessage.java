package com.example.theapp_alfabuild;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ReadMessage extends Activity{

	TextView afisId;
	int date;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_message);
		
		afisId = (TextView) findViewById(R.id.textView1);
		
		Bundle datePrimite = getIntent().getExtras();
		date = datePrimite.getInt("keyID");
		
		String s = "Succes, datele pot fi preluate din login";
		//String s = String.valueOf(date);
		afisId.setText(s);
	}
}
