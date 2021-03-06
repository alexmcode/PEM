package com.example.theapp_alfabuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.theapp_alfabuild.ManualScanBeta.LoadMessages;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

//prin aceasta clasa impreuna cu thread-urile aplicatia va compara in continuu locatia curenta 
//cu toate inregistrarile din tabela locatii. Cand sunt identificate in tabela, id-ul lor va fi preluat
//si se va face o extragere din tabela mesaje, a mesajelor care au id-ul locatiei si care NU au id-ul utilizatorului.
//Mesajul va fi afisat
//acest lucru se va repeta cand locatia se va schimba!!!!

public class Scanner extends ListActivity {
	private static double Lat;
	private static double Longit;
	private static int idLoc;
	private static int idUser, idUserSP, idUserFolositPtNickname;
	private static ArrayList<HashMap<String, String>> xPEMList;
	private static ArrayList<HashMap<String, String>> nicknameList;
	Button bScan;
	
	static JSONParser jsonParser = new JSONParser();
	static JSONObject jsonLoc, jsonLoc2, jsonNickname;
	static JSONObject jsonMsg;
	
	
	private static final String LOCATION_URL = "http://carlitos.bl.ee/webservice2/locationSelect.php";
	private static final String MESSAGE_URL = "http://carlitos.bl.ee/webservice2/getMessage.php";
	private static final String NICKNAME_URL = "http://carlitos.bl.ee/webservice2/getUsers.php";
	
	
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
	
 // Progress Dialog
 	private ProgressDialog pDialog;
    
 	private Coordonates gpsTracker;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// note that use read_comments.xml instead of our single_post.xml
		setContentView(R.layout.display_messages);
		
		//Bundle datePrimite = getIntent().getExtras();
		//idUser = datePrimite.getInt("keyID2");
		
		xPEMList=new ArrayList<HashMap<String,String>>();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Scanner.this);
		idUserSP = sp.getInt("idUser", 0);
		Lat = sp.getFloat("scanLat", 0);
		Longit = sp.getFloat("scanLongit", 0);
		
		//trebuie pus in LoadMessages
		/*gpsTracker=new Coordonates(this);
		
		if(gpsTracker.canGetLocation()){
			String stringLatitude = String.valueOf(gpsTracker.latitude);
            lat=gpsTracker.latitude;
			
            String stringLongitude = String.valueOf(gpsTracker.longitude);
            longit=gpsTracker.longitude;
  		}*/
		
	}
	
	public Scanner(ArrayList<HashMap<String, String>> l){
		l = this.testPEM();
	}
	
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// loading the comments via AsyncTask
	
		new LoadMessages().execute();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		//v.getId(R.id.scanBtn);
		
			
	}
	
	//LEGATURA dintre addMessage si this???
	public void addComment(View v) {
		Intent i = new Intent(Scanner.this, AddMessage.class);
		startActivity(i);
	}
	
	//FUNCTII PT ADUCERE DIN BD A NICKNAME-ULUI
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
	
	public static String extractNickname(int idUser){
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
	
	public static ArrayList<HashMap<String, String>> testPEM(){
		double lat = Lat;
    	double longit = Longit;
    	ArrayList<HashMap<String, String>> theList = new ArrayList<HashMap<String, String>>();
    	
    	if(lat != 0 && longit != 0 ){
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
	    	        	//physicallyEmbeddedMessages();
	    	        }
				} catch (JSONException e) {
					Log.e("Eroare la parsare in scanner  IN MANUAL SCAN !", "cauta in blocul try");
					e.printStackTrace();
				}
	    	
			
			
	    	
			int id = idLoc;
			String idStr = String.valueOf(id); 
			
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
    	}
    	
    	for (HashMap<String, String> hashMap : xPEMList) {
			theList.add(hashMap);
		}
		return theList;
	}
	
	//ADUCERE MESAJE DIN BAZA DE DATE
	public static void physicallyEmbeddedMessages(/*int idLoc, int idUser*/){
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
	
	public static void IdUser(){
		
		double lat = Lat;
    	double longit = Longit;
    	
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
	
	public void updateList(){
		
		ListAdapter adapter = new SimpleAdapter(this, xPEMList, R.layout.single_message, 
				new String[] {TAG_TITLE, TAG_MSG_BODY, TAG_NICKNAME}, 
				new int[] {R.id.title, R.id.message, R.id.username});
		
		setListAdapter(adapter);
		
	}
	
	public static ArrayList<HashMap<String, String>> extractPEMList(){
		ArrayList<HashMap<String, String>> localList=new ArrayList<HashMap<String,String>>();
		
		for (HashMap<String, String> hashMap : xPEMList) {
			localList.add(hashMap);
		}
		
		return localList;
	}
	
	public class LoadMessages extends AsyncTask<Void, Void, Boolean>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Scanner.this);
			pDialog.setMessage("Loading Comments...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			//pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			loadNickname();
			
			IdUser();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			//if(PEMList.size()!=0){
				updateList();
			//}
		}
		
	}
}
