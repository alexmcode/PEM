package com.example.theapp_alfabuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

public class TestPEM {

	private static double lat;
	private static double longit;
	private static int idLoc;
	private static int idUser, idUserSP, idUserFolositPtNickname;
    ArrayList<HashMap<String, String>> xPEMList = new ArrayList<HashMap<String,String>>();
	private ArrayList<HashMap<String, String>> nicknameList = new ArrayList<HashMap<String,String>>();
	Button bScan;
	
	static JSONParser jsonParser = new JSONParser();
	static JSONObject jsonLoc;
	JSONObject jsonLoc2;
	JSONObject jsonNickname;
	JSONObject jsonMsg;
	
	
	private static final String LOCATION_URL = "http://carlitos.bl.ee/webservice2/locationSelect.php";
	private static final String MESSAGE_URL = "http://carlitos.bl.ee/webservice2/getMessage.php";
	private static final String NICKNAME_URL = "http://carlitos.bl.ee/webservice2/getUsers.php";
	
	
	private static final String TAG_SUCCESS = "success";
    
    
    private static final String TAG_MESSAGES = "messages";
    private static final String TAG_MSG_BODY = "msg_body";
    private static final String TAG_TITLE = "title";
    
    private static final String TAG_USERS = "users";
    private static final String TAG_ID_USER = "id_user";
    private static final String TAG_NICKNAME = "nickname";
    
    private static final String TAG_ID_LOC = "id_location";
    private static final String TAG_LAT = "lat_coord";
    private static final String TAG_LONGIT = "longit_coord";
    private static final String TAG_LOCATIONS = "locations";
    
    private Context mContext;
    
    public TestPEM(Context context, double latC, double longitC){
    	this.mContext = context;
    	this.lat = latC;
    	this.longit = longitC;
    	/*SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(this.mContext);
		this.lat = sp1.getFloat("currLat", 0);
    	
		SharedPreferences sp2 = PreferenceManager.getDefaultSharedPreferences(this.mContext);
		this.lat = sp2.getFloat("currLongit", 0);*/
		
    	/*SharedPreferences LatLongit = mContext.getSharedPreferences("coords", mContext.MODE_PRIVATE);
    	this.lat = LatLongit.getFloat("currLat", 0);
    	this.longit = LatLongit.getFloat("currLongit", 0);*/
    	
    	
    	new A().execute();
    	
    }
    
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
	
	class A extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			IdUser();
			
			return null;
		}
	}
	
	private static Boolean IsOrNotLocation(double lat, double longit){
		Boolean isLocationLocal = false;
		
		if(lat != 0 && longit != 0 ){
    		try {
    			
    			jsonLoc = jsonParser.getJSONFromUrl(LOCATION_URL);
    			
    			int success = jsonLoc.getInt(TAG_SUCCESS);
    	        if (success == 1) {
    	        	
    	        	Log.d("Locations avaible, start scanning! IN MANUAL SCAN !", jsonLoc.toString());   
    	        	
    	        	JSONArray locationsJson = jsonLoc.getJSONArray(TAG_LOCATIONS);
    	        	for(int i=0; i<locationsJson.length();i++){
    	        		
    	            	JSONObject l= locationsJson.getJSONObject(i);
    	          
    	            	double latJson = l.getDouble(TAG_LAT);
    	            	double longitJson = l.getDouble(TAG_LONGIT);
    	            	
    	            	if(latJson == lat && longitJson == longit){
    	            		isLocationLocal= true;
    	            	}
    	        	}
    	        }
			} catch (JSONException e) {
				Log.e("Eroare la parsare in scanner  IN MANUAL SCAN !", "cauta in blocul try");
				e.printStackTrace();
			}
    	}
		
		return isLocationLocal;
	}
	
}
