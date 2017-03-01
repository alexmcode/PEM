package com.example.theapp_alfabuild;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class ListViewMsg extends ListActivity{
	public ArrayList<HashMap<String, String>> PEMlista;
	LocationManager lm;
	double lat;
	double longit;
	
	private static final String TAG_USER="nickname";
	private static final String TAG_TITLE="title";
	private static final String TAG_MSG="msg_body";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_messages);
		
		//final Scanner s = new Scanner();
		PEMlista = new ArrayList<HashMap<String, String>>();
		
		Bundle datePrimite = getIntent().getExtras();
		final int id = datePrimite.getInt("keyID");
		
		//lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// PT SATELIT?????
		//Criteria crit = new Criteria();

		//String towers = lm.getBestProvider(crit, false);
		//String gps = lm.GPS_PROVIDER;
		//Location locationProvide = lm.getLastKnownLocation(towers);
		//Location locationGPS = lm.getLastKnownLocation(gps);
		//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new loc listener);
		
		//lat = (locationProvide.getLatitude() * 1E6);
		//longit = (locationProvide.getLongitude() * 1E6);
		
		
		
		LocationListener listener = new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
				//apelam lista si o punem in una statica
				//apelam o functie cu lista noua ca parametru in care executa un tread
				//si lanseaza activitatea daca e cazul
				//pentru testare definire arraylista de vectori de double pt set coordonate
		
				//lat = lm.getLastKnownLocation(towers).getLatitude();
				//longit = lm.getLastKnownLocation(towers).getLongitude();
				//SAU
				lat = location.getLatitude();
				longit = location.getLongitude();
				//PEMlista = s.Scan(lat, longit, id);
				
				if(PEMlista.isEmpty() == false){
					startThread(PEMlista);
				}
				
			}
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
			
		};
		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
			
		}

	public void startThread(ArrayList<HashMap<String, String>> PEMList){
		//pornim thread cu afisare lista
		/*Thread magic = new Thread(){
			public void run(){
				
			}
		};*/
		
		
		
		
		new Thread(new Runnable() {
	        public void run() {
	        	try {
					//lansare listView layout cu lista primita
					//Intent startListViw = new Intent("com.example.theapp_alfabuild.ListView");
					//startActivity(startListViw);
					//CUM POSNESC ACTIVITATEA LIST VIEW CU PEMlista CA PARAMETRU????????????????
	        		
	        		
	        		
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }).start();
		
		
	}
	
	
	public void updateList(ArrayList<HashMap<String, String>> PEMList){
		final ArrayList<HashMap<String, String>> PEMLocal;
		PEMLocal = PEMList;
		
		ListAdapter adapter = new SimpleAdapter(this, PEMLocal, R.layout.single_message, 
				new String[] {TAG_TITLE, TAG_MSG, TAG_USER}, 
				new int[] {R.id.title, R.id.message, R.id.username});
		
		setListAdapter(adapter);
	}

	public class DisplayListView extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {

			updateList(PEMlista);
			
			return null;
		}
		
	}
	
}




