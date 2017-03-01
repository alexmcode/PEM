package com.example.theapp_alfabuild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class Coordonates extends Service implements LocationListener{
	private Context mContext;

    //flag for GPS Status
    boolean isGPSEnabled = false;

    //boolean isLocationTest=false;
    
    //flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;
    double latCh, longitCh;

    //The minimum distance to change updates in metters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; //10 metters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 minute

    //Declaring a Location Manager
    protected LocationManager locationManager;
    
    //=========================================================================================
    
	private static double lat;
	private static double longit;
	private static int idLoc;
	private static int idUser, idUserSP, idUserFolositPtNickname;
    ArrayList<HashMap<String, String>> xPEMList;
	private ArrayList<HashMap<String, String>> nicknameList;
	Button bScan;
	
	static JSONParser jsonParser = new JSONParser();
	static JSONObject jsonLoc;

	JSONObject jsonLoc2;

	JSONObject jsonNickname;
	JSONObject jsonMsg;
	
	
	private static final String LOCATION_BY_COORDS_URL = "http://carlitos.bl.ee/webservice2/locationSelectByCoords2.php";
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
    
    int isLocation;
    
    private int test;
    //=========================================================================================
    
    public Coordonates(Context context) 
    {
    	this.test = 1;
		xPEMList=new ArrayList<HashMap<String,String>>();
        this.mContext = context;
        this.isLocation = 0;
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.mContext);
		//idUserSP = sp.getInt("idUser", 0);
        getLocation();
        //new A().execute(this.latitude, this.longitude);
        
    }

    public Location getLocation()
    {
        try
        {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
            {
                // no network provider is enabled
            }
            else
            {
                this.canGetLocation = true;

                //First get location from Network Provider
                if (isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");

                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPSCoordinates();
                    }
                }

                //if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled)
                {
                    if (location == null)
                    {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("GPS Enabled", "GPS Enabled");

                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateGPSCoordinates();
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Log.e("Error : Location", "Impossible to connect to LocationManager", e);
        }

        return location;
    }

    public void updateGPSCoordinates()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */

    public void stopUsingGPS()
    {
        if (locationManager != null)
        {
            locationManager.removeUpdates(Coordonates.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude()
    {
        if (location != null)
        {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     */
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
   /* public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.GPSAlertDialogTitle);

        //Setting Dialog Message
        alertDialog.setMessage(R.string.GPSAlertDialogMessage);

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() 
        {   
            @Override
            public void onClick(DialogInterface dialog, int which) 
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
        {   
            @Override
            public void onClick(DialogInterface dialog, int which) 
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }*/

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context)
    {
        if (location != null)
        {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            try 
            {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                return addresses;
            } 
            catch (IOException e) 
            {
                //e.printStackTrace();
                Log.e("Error : Geocoder", "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
 /*   public String getAddressLine(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        }
        else
        {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        }
        else
        {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
 /*   public String getPostalCode(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        }
        else
        {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
/*   public String getCountryName(Context context)
    {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        }
        else
        {
            return null;
        }
    }*/

//=======================================================================
    
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
    	
		lat=this.latitude;
		longit=this.longitude;
		
    	if(/*locationProvide != null*/ lat != 0 && longit != 0 ){
    		//lat = (locationProvide.getLatitude() * 1E6);
    		//longit = (locationProvide.getLongitude() * 1E6);
    		try {
    			jsonLoc = jsonParser.getJSONFromUrl(LOCATION_BY_COORDS_URL);
    			
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
	
	public void IsOrNotLocation(){
		
		String latStr, longitStr;
		
		double latChLocal=latCh;
		double longitChLocal=longitCh;
		
		if(latChLocal != 0 && longitChLocal != 0 ){
    		try {
    			List<NameValuePair> paramsLoc = new ArrayList<NameValuePair>();
        		latStr = String.valueOf(latChLocal);
        		longitStr = String.valueOf(longitChLocal);
        		paramsLoc.add(new BasicNameValuePair("lat_coord", latStr));
        		paramsLoc.add(new BasicNameValuePair("longit_coord", longitStr));
    			
    			
    			jsonLoc = jsonParser.makeHttpRequest(LOCATION_BY_COORDS_URL, "POST", paramsLoc, " loc by coords 2");
    			
    			int success = jsonLoc.getInt(TAG_SUCCESS);
    	        if (success == 1) {
    	        	isLocation = 1;	
    	        }else{
    	        	isLocation = 2;
    	        }
			} catch (JSONException e) {
				Log.e("Eroare la parsare in scanner  IN MANUAL SCAN !", "cauta in blocul try");
				e.printStackTrace();
			}
    	}
    	
	}
	
    
	public class A extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//Toast.makeText(mContext, "async task ", Toast.LENGTH_LONG).show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			IsOrNotLocation();
			
			return null;
			
			
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//Coordonates.this.isLocation=result;
		}
		
	}
	
    //=======================================================================
    
    @Override
    public void onLocationChanged(Location locationCh) {  
    	
    	latCh = getLatitude();//=lat si longit globale !!!! loc0 VS loc1
    	longitCh = getLongitude();
    	
    	//new A().execute();
    	
    	/*if(isLocation == 1){
    		//Toast.makeText(this.mContext,String.valueOf(this.latitude)+"   "+ String.valueOf(this.longitude), Toast.LENGTH_LONG).show();
    		//Intent i1 = new Intent(this, ManualScanBeta.class);
			//startActivity(i1);
    	}else if(isLocation == 2){
    		//String strTest = String.valueOf(this.test);
    		//Toast.makeText(this.mContext, "NULL", Toast.LENGTH_LONG).show();
    	}else{
    		//Toast.makeText(this.mContext, "NEINITIALIZAT!!!", Toast.LENGTH_LONG).show();
    	}*/
    }

    @Override
    public void onProviderDisabled(String provider) 
    {   
    }

    @Override
    public void onProviderEnabled(String provider) 
    {   
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) 
    {   
    }

    @Override
    public IBinder onBind(Intent intent) 
    {
        return null;
    }
    
    
}
