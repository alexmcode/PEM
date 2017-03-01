package com.example.theapp_alfabuild;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.theapp_alfabuild.Login.AttemptLogin;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Add_Scan extends Activity implements OnClickListener{
	private Button Scan, Add;
	LocationManager lm;
	Coordonates gpsTracker;
	//private double lat = 0;
	//private double longit = 0;
	
	
	//===================================================
	
	private static double lat;
	private static double longit;
	private static int idLoc;
	private static int idUser, idUserSP, idUserFolositPtNickname;
	private ArrayList<HashMap<String, String>> xPEMList;
	private ArrayList<HashMap<String, String>> nicknameList;
	Button bScan;
	
	JSONParser jsonParser = new JSONParser();
	JSONObject jsonLoc, jsonLoc2, jsonNickname;
	JSONObject jsonMsg;
	
	
	private static final String LOCATION_URL = "http://carlitos.bl.ee/webservice2/locationSelect.php";
	private static final String MESSAGE_URL = "http://carlitos.bl.ee/webservice2/getMessage.php";
	private static final String NICKNAME_URL = "http://carlitos.bl.ee/webservice2/getUsers.php";
	
	
	private static final String TAG_SUCCESS = "success";
    
    
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
	
	//====================================================
	
	private TextView tvTime;
	private TextView tvLocation;
    
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan);
		
		Scan=(Button)findViewById(R.id.scanBtn);
		Add=(Button)findViewById(R.id.addBtn);
		
		Scan.setOnClickListener(this);
		Add.setOnClickListener(this);
		
		//Bundle datePrimite = getIntent().getExtras();
		//date = datePrimite.getInt("keyID2");
		//IdUser = date;
		
		tvTime=(TextView)findViewById(R.id.tvTime);
		tvLocation=(TextView)findViewById(R.id.tvLocation);
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		getActionBar().setTitle("Logout");
		
		//new Start().execute();
		
		AutoScan();
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		 switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent i = new Intent(this, Login.class);
	            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(i);
	            this.finish();
	            return true;
	        default:
	        	return super.onOptionsItemSelected(item);
		 }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		new Start().execute();
		
		//lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		//lm.requestLocationUpdates(lm.GPS_PROVIDER, 1000, 0, this);
		
		Coordonates locChanged = new Coordonates(this);
		
		TimeLocation(locChanged);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.scanBtn:
			Intent i1 = new Intent(this, ManualScanBeta.class);
			startActivity(i1);
			break;
		case R.id.addBtn:
				Intent i2 = new Intent(this, AddMessage.class);
				startActivity(i2);
			break;

		default:
			break;
		}
	}

	public void AutoScan(){
		/*Thread timer = new Thread(){
			public void run(){
				try {
					sleep(88);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};*/
		
		TimerTask t = new TimerTask() {
			int a=0;
			@Override
			public void run() {
				//apel functie
				a++;
				Toast.makeText(Add_Scan.this, String.valueOf(a), Toast.LENGTH_LONG).show();
			}//sunnable!!!
		};
		
		Timer time = new Timer();
		
		time.scheduleAtFixedRate(t, 0, 3000);
	}
	
	public void TimeLocation(Coordonates city){
		Calendar c = Calendar.getInstance();
		int h = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		String hString = String.valueOf(h);
		String minString = String.valueOf(min);
		String time = "It's " + hString + ":" + minString;
		tvTime.setText(time);
		
		String address = city.getLocality(this);
		String add= "You are in "+address;
		tvLocation.setText(add);
	}

	
	//====================================================================================
	
	public void loadNickname(){
		nicknameList = new ArrayList<HashMap<String,String>>();
		
		try {
			jsonNickname = jsonParser.getJSONFromUrl(NICKNAME_URL);
			
			int success = jsonNickname.getInt(TAG_SUCCESS);
	        if (success == 1) {
	        	Log.d("Users availble, start scanning! IN MANUAL SCAN !", jsonNickname.toString());   
	        	
	        	JSONArray usersJson = jsonNickname.getJSONArray(TAG_USERS);
	        	
	        	for(int i=0; i<usersJson.length();i++){
	            	JSONObject l= usersJson.getJSONObject(i);
	            	int id_userJson = l.getInt(TAG_ID_USER);
	            	String nicknameJson = l.getString(TAG_NICKNAME);
	            	
	            	String id=String.valueOf(id_userJson);
	            	
	            	HashMap<String, String> map2 = new HashMap<String, String>();
	            	
	            	map2.put(TAG_ID_USER, id);
	            	map2.put(TAG_NICKNAME, nicknameJson);
	            	
	            	nicknameList.add(map2);
	        	}
	        }
		} catch (JSONException e) {
			Log.e("Eroare la parsare in loadNickname IN MANUAL SCAN !", "cauta in blocul try");
			e.printStackTrace();
		}
	}
	
	public String extractNickname(int idUser){
		String idUserString=String.valueOf(idUser);
		String nicknameFinal=null;
		HashMap<String, String> aux=new HashMap<String, String>();
		
		for (int i = 0; i < nicknameList.size(); i++) {
			aux=nicknameList.get(i);
			if(aux!=null){
				if(idUserString == aux.get(TAG_ID_USER)){
					String nickAux=aux.get(TAG_NICKNAME);
					nicknameFinal=nickAux;
				}
			}
		}
		
		return nicknameFinal;
	}
	
	public void physicallyEmbeddedMessages(/*int idLoc, int idUser*/){
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
            	 
            	Log.d("Messages avaible, start scanning! IN MANUAL SCAN !", jsonMsg.toString());   
 	        	
 	        	JSONArray messagesJson = jsonMsg.getJSONArray(TAG_MESSAGES);
 	        	for(int i=0; i<messagesJson.length();i++){
 	            	JSONObject m= messagesJson.getJSONObject(i);
 	            	
 	            	if(m.getInt(TAG_ID_USER) != idUserSP){//am inlocuit idUser cu idUserSP
	 	            	/*message.setId(m.getInt(TAG_ID_MSG));
	 	            	message.setTitle(m.getString(TAG_TITLE));
	 	            	message.setBody(m.getString(TAG_MSG_BODY));
	 	            	message.setCategory(m.getString(TAG_CATEGORY));*/
 	            		
 	            		String title = m.getString(TAG_TITLE);
 	            		String msg_body = m.getString(TAG_MSG_BODY);
 	            		
 	            		idUserFolositPtNickname=m.getInt(TAG_ID_USER);
 	            		String nickname=extractNickname(idUserFolositPtNickname);
 	            		
 	            		HashMap<String, String> map = new HashMap<String, String>();
 	            		
 	            		map.put(TAG_TITLE, title);
 	            		map.put(TAG_MSG_BODY, msg_body);
 	            		map.put(TAG_NICKNAME, nickname);
	 	            	
	 	            	//theList.add(map);
	 	            	xPEMList.add(map);   
 	            	}
 	            	
 	        	}

            }
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		//return theList;
	}
	
	public void IdUser(){
		
		
		
		//double lat = 11191;
    	//double longit = 32222;
    	
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
    	        	Log.d("Locations avaible, start scanning! IN MANUAL SCAN !", jsonLoc.toString());   
    	        	
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
    	        	/*PEMList = */physicallyEmbeddedMessages();
    	        }
			} catch (JSONException e) {
				Log.e("Eroare la parsare in scanner  IN MANUAL SCAN !", "cauta in blocul try");
				e.printStackTrace();
			}
    	}
	}
	
	//==================================================================================================
	
	public class Start extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			return null;
		}
		
	}
	
}






