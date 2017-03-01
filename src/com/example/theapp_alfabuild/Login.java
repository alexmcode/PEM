package com.example.theapp_alfabuild;

    import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

    import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

    import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

    public class Login extends Activity implements OnClickListener{

    	private EditText user, pass;
    	private Button mSubmit, mRegister;

    	 // Progress Dialog
        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();

        //php login script location:

        //localhost :
        //testing on your device
        //put your local ip instead,  on windows, run CMD > ipconfig
        //or in mac's terminal type ifconfig and look for the ip under en0 or en1
       // private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/login.php";

        //testing on Emulator://192.168.141.1 sau 192.168.42.1
        //private static final String LOGIN_URL = "http://192.168.141.1:1337/webservice2/login.php";
        private static final String LOGIN_URL2 = "http://192.168.141.1:80/webservice2/login.php";
        
        private static final String LOGIN_URL = "http://www.carlitos.bl.ee/webservice2/login.php";

      //testing from a real server:
        //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/login.php";

        //JSON element ids from repsonse of php script:
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_USERS = "users";
        private static final String TAG_ID = "id_user";
        private static final String TAG_NICKNAME = "nickname";
        private static final String TAG_PASSWORD = "password";
        private static final String TAG_FIRSTNAME = "firstname";
        private static final String TAG_LASTNAME = "lastname";
        
        //variabile in care se preia json
        JSONArray users = null;
        int id_user;
        String firstname;
        String lastname;
        

    	@Override
    	protected void onCreate(Bundle savedInstanceState) {
    		// TODO Auto-generated method stub
    		super.onCreate(savedInstanceState);
    		setContentView(R.layout.login);

    		//setup input fields
    		user = (EditText)findViewById(R.id.nickname);
    		pass = (EditText)findViewById(R.id.password);

    		//setup buttons
    		mSubmit = (Button)findViewById(R.id.login);
    		mRegister = (Button)findViewById(R.id.register);

    		//register listeners
    		mSubmit.setOnClickListener(this);
    		mRegister.setOnClickListener(this);
    		
    	}

    	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		switch (v.getId()) {
    		case R.id.login:
    				new AttemptLogin().execute();
    			break;
    		case R.id.register:
    				Intent i = new Intent(this, Register.class);
    				startActivity(i);
    			break;

    		default:
    			break;
    		}
    	}

    	//<=============================================================================================>
    	
    	public static String MD5(String s) 
    	{
    	    MessageDigest digest;
    	    try 
    	    {
    	        digest = MessageDigest.getInstance("MD5");
    	        digest.update(s.getBytes(),0,s.length());
    	        String hash = new BigInteger(1, digest.digest()).toString(16);
    	        return hash;
    	    } 
    	    catch (NoSuchAlgorithmException e) 
    	    {
    	        e.printStackTrace();
    	    }
    	    return "";
    	}
    	
    	//<=============================================================================================>
    	class AttemptLogin extends AsyncTask<String, String, String> {

    		 /**
             * Before starting background thread Show Progress Dialog
             * */
    		boolean failure = false;

            @Override
            protected void onPreExecute() {
            	try{
	                super.onPreExecute();
	                pDialog = new ProgressDialog(Login.this);
	                pDialog.setMessage("Attempting login...");
	                pDialog.setIndeterminate(false);
	                pDialog.setCancelable(true);
	                pDialog.show();
	               	}
            	catch(ExceptionInInitializerError e){
            		e.printStackTrace();
            	}
            }

    		@Override
    		protected String doInBackground(String... args) {
    			// TODO Auto-generated method stub
    			 // Check for success tag
                int success;
                String nickname = user.getText().toString();
                
                String passwordBrut = pass.getText().toString();
                String password = MD5(passwordBrut);
                
                try {
                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("nickname", nickname));
                    params.add(new BasicNameValuePair("password", password));

                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    String identificator = "login";
                    JSONObject json = jsonParser.makeHttpRequest(
                           LOGIN_URL, "POST", params, identificator);

                    // check your log for json response
                    Log.d("Login attempt", json.toString());

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                    	Log.d("Login Successful!", json.toString());
                    	
                    	//parsare completa !!!
                    	users = json.getJSONArray(TAG_USERS);
                    	JSONObject u= users.getJSONObject(0);
                    	id_user = u.getInt(TAG_ID);
                    	firstname = u.getString(TAG_FIRSTNAME);
                    	lastname = u.getString(TAG_LASTNAME);
                    	
                    	//trimitere date
                    	Bundle dateUser = new Bundle();
                    	dateUser.putInt("keyID", id_user);
                    	//dateUser.putString("keyFN", firstname);
                    	
                    	Intent i = new Intent(Login.this, Add_Scan.class);
                    	
                    	i.putExtras(dateUser);
                    	
                    	//SHEREAD PREFERANCES salvare id in alt mod
                    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Login.this);
                    	Editor edit = sp.edit();
                    	edit.putInt("idUser", id_user);
                    	edit.commit();

                    	/*SharedPreferences IdUserContext = mContext.getSharedPreferences("coords", MODE_PRIVATE);
                    	Editor edit = LatLongit.edit();
                    	edit.putFloat("currLat", (float) this.latitude);*/
                    	
                    	finish();
        				startActivity(i);
                    	return json.getString(TAG_MESSAGE);
                    }else{
                    	Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    	return json.getString(TAG_MESSAGE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;

    		}
    		/**
             * After completing background task Dismiss the progress dialog
             * **/
            protected void onPostExecute(String file_url) {
                // dismiss the dialog once product deleted
                pDialog.dismiss();
                if (file_url != null){
                	Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
                }

            }

    	}

    }