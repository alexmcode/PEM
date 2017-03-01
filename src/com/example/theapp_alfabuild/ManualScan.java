package com.example.theapp_alfabuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class ManualScan extends Activity{
	private static double lat;
	private static double longit;
	private static int idLoc;
	private static int idUser;
	ArrayList<HashMap<String, String>> PEMList;
	
	JSONParser jsonParser = new JSONParser();
	JSONObject jsonLoc;
	JSONObject jsonMsg;
	
	
	private static final String LOCATION_URL = "http://192.168.141.1:1337/webservice2/locationSelect.php";
	private static final String MESSAGE_URL = "http://192.168.141.1:1337/webservice2/getMessage.php";
	
	
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    
    private static final String TAG_MESSAGES = "messages";
    private static final String TAG_ID_MSG = "id_message";
    private static final String TAG_MSG_BODY = "msg_body";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CATEGORY = "category";
    
    private static final String TAG_USERS = "users";
    private static final String TAG_ID_USER = "id_user";
    private static final String TAG_NICKNAME = "nickname";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    
    private static final String TAG_ID_LOC = "id_location";
    private static final String TAG_LAT = "lat_coord";
    private static final String TAG_LONGIT = "longit_coord";
    private static final String TAG_LOCATIONS = "locations";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.display_messages);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// loading the comments via AsyncTask
		new Scan().execute();
	}

	public void addComment(View v) {
		Intent i = new Intent(this, AddMessage.class);
		startActivity(i);
	}
	
	
	public class Scan extends AsyncTask<String, String, String>{

		public ArrayList<HashMap<String, String>> physicallyEmbeddedMessages(int idLoc, int idUser){
			int id = idLoc;
			String idStr = String.valueOf(id); 
			
			ArrayList<HashMap<String, String>> theList = new ArrayList<HashMap<String, String>>();
			//este initializata corect????
			
			try{
				 List<NameValuePair> params = new ArrayList<NameValuePair>();
	             params.add(new BasicNameValuePair("id_location", idStr));
				
	             String identificator = "probleme la parsare in scanare!!!!";
	             jsonMsg = jsonParser.makeHttpRequest(MESSAGE_URL, "POST", params, identificator);
	             
	             int success = jsonMsg.getInt(TAG_SUCCESS);
	             if(success == 1){
	            	 Message message=new Message();
	            	 
	            	 Log.d("Messages avaible, start scanning!", jsonLoc.toString());   
	 	        	
	 	        	JSONArray messagesJson = jsonMsg.getJSONArray(TAG_MESSAGES);
	 	        	for(int i=0; i<messagesJson.length();i++){
	 	            	JSONObject m= messagesJson.getJSONObject(i);
	 	            	
	 	            	if(m.getInt(TAG_ID_USER) != idUser){
		 	            	/*message.setId(m.getInt(TAG_ID_MSG));
		 	            	message.setTitle(m.getString(TAG_TITLE));
		 	            	message.setBody(m.getString(TAG_MSG_BODY));
		 	            	message.setCategory(m.getString(TAG_CATEGORY));*/
	 	            		
	 	            		String title = m.getString(TAG_TITLE);
	 	            		String msg_body = m.getString(TAG_MSG_BODY);
	 	            		String nickname = m.getString(TAG_NICKNAME);
	 	            		
	 	            		HashMap<String, String> map = new HashMap<String, String>();
	 	            		
	 	            		map.put(TAG_TITLE, title);
	 	            		map.put(TAG_MSG_BODY, msg_body);
	 	            		map.put(TAG_NICKNAME, nickname);
		 	            	
		 	            	theList.add(map);
	 	            	}
	 	        	}
	            }
			}catch(JSONException e){
				e.printStackTrace();
			}
			
			return theList;
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			double lat = 11191;
        	double longit = 32222;
        	
        	/*
        	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        	Criteria crit = new Criteria();
        	
        	towers = lm.getBestProvider(crit, false);
        	gps = lm.GPS_PROVIDER;
        	Location locationProvide = lm.getLastKnownLocation(towers);
        	Location locationGPS = lm.getLastKnownLocation(gps);
        	
        	//SAU !!!
        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new loc listener);//sau cu constructor looper thread !!! sau intent!!
        	//si apeleaza onLocChange si executa ce e acolo:preluarea locatie->comparaea cu toate loc din tabela
        	 //si daca sunt sa afiseze pe ecran doar daca nu a fost postat de utilizator
        	*/
        	
        	if(/*locationProvide != null*/ lat != 0 && longit != 0 ){
        		//lat = (locationProvide.getLatitude() * 1E6);
        		//longit = (locationProvide.getLongitude() * 1E6);
        		try {
        			jsonLoc = jsonParser.getJSONFromUrl(LOCATION_URL);
        			
        			int success = jsonLoc.getInt(TAG_SUCCESS);
        	        if (success == 1) {
        	        	Log.d("Locations avaible, start scanning!", jsonLoc.toString());   
        	        	
        	        	JSONArray locationsJson = jsonLoc.getJSONArray(TAG_LOCATIONS);
        	        	for(int i=0; i<locationsJson.length();i++){
        	            	JSONObject l= locationsJson.getJSONObject(i);
        	            	int id_locJson = l.getInt(TAG_ID_LOC);
        	            	double latJson = l.getDouble(TAG_LAT);
        	            	double longitJson = l.getDouble(TAG_LONGIT);
        	            	
        	            	if(lat == latJson && longit == longitJson){
        	            		idLoc = id_locJson;
        	            	}
        	        	}
        	        	PEMList = physicallyEmbeddedMessages(idLoc, idUser);
        	        }
				} catch (JSONException e) {
					Log.e("Eroare la parsare in scanner", "cauta in blocul try");
					e.printStackTrace();
				}
        	}
			
        	if(PEMList != null){
        		final ArrayList<HashMap<String, String>> PEMLocal;
        		PEMLocal = PEMList;
        		
        		/*ListAdapter adapter = new SimpleAdapter(this, PEMLocal, R.layout.single_message, 
        				new String[] {TAG_TITLE, TAG_MSG_BODY, TAG_NICKNAME}, 
        				new int[] {R.id.title, R.id.message, R.id.username});
        		
        		setListAdapter(adapter);*/
        		
        		Intent i = new Intent(ManualScan.this, AddMessage.class);
        		startActivity(i);
        	}
        	
			return null;
		}
		
		public void updateList(){
			if(PEMList != null){
        		final ArrayList<HashMap<String, String>> PEMLocal;
        		PEMLocal = PEMList;
        		
        		/*ListAdapter adapter = new SimpleAdapter(this, PEMLocal, R.layout.single_message, 
        				new String[] {TAG_TITLE, TAG_MSG_BODY, TAG_NICKNAME}, 
        				new int[] {R.id.title, R.id.message, R.id.username});
        		
        		setListAdapter(adapter);
        		
        		Intent i = new Intent(ManualScan.this, AddMessage.class);
        		startActivity(i);*/
        	}
		}
		
	}
	
}
