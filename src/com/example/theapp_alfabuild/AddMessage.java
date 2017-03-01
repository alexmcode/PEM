package com.example.theapp_alfabuild;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddMessage extends Activity implements OnClickListener, LocationListener{
	
	private static int IdUser, IdUserSP;
	private EditText title, message, category;
	private Button  mSubmit;
	private TextView afisId;
	protected int date;
			public int getDate() { return date; }

	private int idLoc = 0;
	LocationManager lm; 
	String towers;
	String gps;
	int successLoc;
	int succes2;
	JSONObject jsonLoc;
	
	LocationManager locManager;
	LocationListener locL;
	
	 // Progress Dialog
    private ProgressDialog pDialog;
    private ProgressDialog lDialog;
 
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    
    //php login script
    
    //localhost :  
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
   // private static final String POST_COMMENT_URL = "http://xxx.xxx.x.x:1234/webservice/addcomment.php";
    
    //testing on Emulator:http:		//192.168.141.1:80
    private static final String POST_COMMENT_URL = "http://carlitos.bl.ee/webservice2/messageInsert.php";
    private static final String POST_LOCATION_URL = "http://carlitos.bl.ee/webservice2/locationSelectByCoords.php";
    private static final String POST_LOCATION2_URL = "http://carlitos.bl.ee/webservice2/locationSelect.php";
    private static final String POST_LOCATION3_URL = "http://carlitos.bl.ee/webservice2/locationInsert.php";
    
    //real server
    
    
  //testing from a real server:
    //private static final String POST_COMMENT_URL = "http://www.mybringback.com/webservice/addcomment.php";
    
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_LOCATIONS = "locations";
    private static final String TAG_ID_LOC = "id_location";
    private static final String TAG_LAT = "lat_coord";
    private static final String TAG_LONGIT = "longit_coord";
	
    JSONArray locationsJson = null;
    JSONArray locationsJson2 = null;
    int id_locJson;
    double latJson;
    double longitJson;
    String latStr;
    String longitStr;
    
    private double lat, longit;
    Coordonates gpsTracker;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_message);
		
		title = (EditText)findViewById(R.id.title);
		message = (EditText)findViewById(R.id.message);
		category = (EditText)findViewById(R.id.category);
		
		mSubmit = (Button)findViewById(R.id.submit);
		mSubmit.setOnClickListener(this);
		
		//primire id/date din login
		Bundle datePrimite = getIntent().getExtras();
		//date = datePrimite.getInt("keyID");
		//IdUser = date;
		//String s = "Succes, datele pot fi preluate din login";
		//String s = String.valueOf(date);
		//afisId.setText(s);
		
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AddMessage.this);
		IdUserSP = sp.getInt("idUser", 0);
		
		SharedPreferences sp2 = PreferenceManager.getDefaultSharedPreferences(AddMessage.this);
    	Editor edit = sp2.edit();
    	edit.putInt("idUser2", IdUserSP);
    	edit.commit();
		
		//trebue pus in LocationComputing
		gpsTracker=new Coordonates(this);
		
		if(gpsTracker.canGetLocation()){
			//String stringLatitude = String.valueOf(gpsTracker.latitude);
            //lat=gpsTracker.latitude;
			lat=gpsTracker.getLatitude();
            
            //String stringLongitude = String.valueOf(gpsTracker.longitude);
            //longit=gpsTracker.longitude;
			longit=gpsTracker.getLongitude();
  		}
	}

	@Override
	public void onClick(View v) {
		new LocationComputing().execute();
		new PostComment().execute();
	}
	

	
	//in mod normal pt get/set location trebuie clasa separata 
	class LocationComputing extends AsyncTask<String, String, String>{//de ce string de 3 ori
		
		//de mutat cod 
		//de facut cu double inserare in bd
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            lDialog = new ProgressDialog(AddMessage.this);
            lDialog.setMessage("Posting location...");
            lDialog.setIndeterminate(false);
            lDialog.setCancelable(true);
           // lDialog.show();
            
            
        }
		
		
		@Override
		protected String doInBackground(String... params) {
			
			/*double lat = 0;
			double longit = 0;
			//double lat = 11191;
        	//double longit = 32222;
        	
        	
        	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        	Criteria crit = new Criteria();
        	
        	towers = lm.getBestProvider(crit, false);
        	gps = lm.GPS_PROVIDER;
        	Location locationProvide = lm.getLastKnownLocation(towers);
        	Location locationGPS = lm.getLastKnownLocation(gps);*/
        	
        	//SAU !!!
        	//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new loc listener);//sau cu constructor looper thread !!! sau intent!!
        	//si apeleaza onLocChange si executa ce e acolo:preluarea locatie->comparaea cu toate loc din tabela
        	 //si daca sunt sa afiseze pe ecran doar daca nu a fost postat de utilizator
        	
        	
        	if(lat != 0 && longit != 0){
        		//lat = (locationProvide.getLatitude() * 1E6);
        		//longit = (locationProvide.getLongitude() * 1E6);
        		
        		try{
	        		List<NameValuePair> paramsLoc = new ArrayList<NameValuePair>();
	        		latStr = String.valueOf(lat);
	        		longitStr = String.valueOf(longit);
	        		//key tre sa fie ca si in php???????DAAAAAAAAAAAAAAAAADADADADADADADA
	        		paramsLoc.add(new BasicNameValuePair("lat_coord", latStr));
	        		paramsLoc.add(new BasicNameValuePair("longit_coord", longitStr));
	        		
	        		//E BINE FOLOSITA FUCTIA ASTA!!!??
	        		jsonLoc = jsonParser.getJSONFromUrl(POST_LOCATION2_URL);
	        		
	        		successLoc = jsonLoc.getInt(TAG_SUCCESS);
	                if (successLoc == 1) {
	                	Log.d("Locations avaible, start parsing!", jsonLoc.toString());   
	                	
	                	//parsarea si compararea locatiilor pt introducere
	                	locationsJson = jsonLoc.getJSONArray(TAG_LOCATIONS);
	                	for(int i=0; i<locationsJson.length();i++){
	                    	JSONObject l= locationsJson.getJSONObject(i);
	                    	id_locJson = l.getInt(TAG_ID_LOC);
	                    	latJson = l.getDouble(TAG_LAT);
	                    	longitJson = l.getDouble(TAG_LONGIT);
	                    	
	                    	if(lat == latJson && longit == longitJson){
	                    		idLoc = id_locJson;
	                    	}
                    	}
	                	
	                	if(idLoc == 0){
	                		//inseamna ce nu s a mai postat nimic in punctul ala si postam acum
	                		//inserare coordonat + extragere dupa 
	                		try{
	                			String identificator = "inserare loc";
	                			JSONObject jsonInsLoc = jsonParser.makeHttpRequest(POST_LOCATION3_URL, "POST", paramsLoc, identificator);
	              
	                            // full json response
	                            Log.d("Post location attempt", jsonInsLoc.toString());
	              
	                            // json success element
	                            int success = jsonInsLoc.getInt(TAG_SUCCESS);
	                            if (success == 1) {
	                            	Log.d("Location Added!", jsonInsLoc.toString());    
	                             	finish();
	                             	//return jsonInsLoc.getString(TAG_MESSAGE);
	                            }else{
	                             	Log.d("Location Failure!", jsonInsLoc.getString(TAG_MESSAGE));
	                             	return jsonInsLoc.getString(TAG_MESSAGE);
	                            }
	                		}catch(JSONException e){
	                			e.printStackTrace();
	                		}
	                		
	                		try{
	                			String identificator = "select loc dupa coord";
	                			JSONObject jsonSelCoord = jsonParser.makeHttpRequest(POST_LOCATION_URL, "POST", paramsLoc, identificator);
	                			Log.d("se incearca extragerea id loc dupa coord", jsonSelCoord.toString());
	                			succes2 = jsonSelCoord.getInt(TAG_SUCCESS);
	                			if(succes2 == 1){
	                				Log.d("id retras cu succes", jsonSelCoord.toString()); 
	                				
	                				locationsJson2 = jsonSelCoord.getJSONArray(TAG_LOCATIONS);
	        	                	for (int i = 0; i < locationsJson2.length(); i++) {
	        	                    	JSONObject l2= locationsJson2.getJSONObject(i);
	        	                    	idLoc = l2.getInt(TAG_ID_LOC);
	        	                    	//latJson = l.getDouble(TAG_LAT);
	        	                    	//longitJson = l.getDouble(TAG_LONGIT);
									}
	                				
	                             	//finish();
	                             	//return jsonSelCoord.getString(TAG_MESSAGE);
	                			}else{
	                             	Log.d("Location id Failure!", jsonSelCoord.getString(TAG_MESSAGE));
	                             	//return jsonSelCoord.getString(TAG_MESSAGE);
	                             	}
	                		}catch(JSONException e){
	                			e.printStackTrace();
	                		}
	                	}
	                	
	                	//finish();
	                	//return jsonLoc.getString(TAG_MESSAGE);
	                }else{
	                	Log.d("Comment Failure!", jsonLoc.getString(TAG_MESSAGE));
	                	//return jsonLoc.getString(TAG_MESSAGE);
	                }
        		}catch(JSONException e){
        			e.printStackTrace();
        		}
        		
        	}else{
        		Log.e("A sarit tot if-ul", "Es sofer roman");
        	}
			
			return null;
		}
		
		
		/*protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            lDialog.dismiss();
            if (file_url != null){
            	Toast.makeText(AddMessage.this, file_url, Toast.LENGTH_LONG).show();
            }
            	
        }*/
	}
	
	
	class PostComment extends AsyncTask<String, String, String> {
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddMessage.this);
            pDialog.setMessage("Posting Message...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
            
            //sa apeleze aici loccomputing.execute!!?!?!?!
            //new LocationComputing().execute();
            
            
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			
			 // Check for success tag
            int success;
            String post_title = title.getText().toString();
            String post_message = message.getText().toString();
            String post_category = category.getText().toString();
            
            //id-urile trebuie introduse tot ca int!!!!
            String post_id_user = String.valueOf(IdUserSP);//am inlocuit date cu IdUserSP
            
            String post_id_location = String.valueOf(idLoc) ;
            
            //We need to change this:
            //String post_username = "temp";
            
            try {
                // Building Parameters
                List<NameValuePair> paramsM = new ArrayList<NameValuePair>();
                paramsM.add(new BasicNameValuePair("title", post_title));
                paramsM.add(new BasicNameValuePair("msg_body", post_message));
                paramsM.add(new BasicNameValuePair("category", post_category));
                paramsM.add(new BasicNameValuePair("id_user", post_id_user));
                paramsM.add(new BasicNameValuePair("id_location", post_id_location));
 
                Log.d("request!", "starting");
                
                //Posting user data to script 
                String identificator = post_id_user+"  " + post_id_location+" "+ latStr+" "+longitStr+" "+successLoc+" "+succes2 ;
                JSONObject jsonM = jsonParser.makeHttpRequest(
                		POST_COMMENT_URL, "POST", paramsM, identificator);
 
                // full json response
                Log.d("Post message attempt", jsonM.toString());
 
                // json success element
                success = jsonM.getInt(TAG_SUCCESS);
                if (success == 1) {
                	Log.d("Message Added!", jsonM.toString());    
                	//Intent i = new Intent(getApplicationContext(), AddMessage.class);//pentru reload addMessage
                	//onResume();
                	
                	//trimitere id ut de la addMessage la ScanManual
                	Bundle dateUser = new Bundle();
                	dateUser.putInt("keyID2", IdUser);
                	//dateUser.putString("keyFN", firstname);
                	
                	Intent i = new Intent(AddMessage.this, Add_Scan.class);
                	
                	i.putExtras(dateUser);
                	
                	finish();
                	startActivity(i);
                	return jsonM.getString(TAG_MESSAGE);
                }else{
                	Log.d("Message Failure!", jsonM.getString(TAG_MESSAGE));
                	return jsonM.getString(TAG_MESSAGE);
                	
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
			
		}
		
       /* protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
           // pDialog.dismiss();
        	
        	
        	
            if (file_url != null){
            	Toast.makeText(AddMessage.this, file_url, Toast.LENGTH_LONG).show();
            }
            
        }*/
		
	}
	
	
	@Override
	protected void onResume() {

	   super.onResume();
	   this.onCreate(null);
	}
	
	public void ApelInLogin(){
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locL);
	}
	
	public void Apel(){
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locL = new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
	        	Location locationGPS = lm.getLastKnownLocation(locManager.GPS_PROVIDER);
	        	
	        	double lat = (locationGPS.getLatitude() * 1E6);
        		double longit = (locationGPS.getLongitude() * 1E6);
        		
        		//Scanner scan = new Scanner();
        		//scan.Scan(lat, longit, date);
	        		
			}
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
			
		};
		
	}
	
	@Override
	public void onLocationChanged(Location location) {}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}

