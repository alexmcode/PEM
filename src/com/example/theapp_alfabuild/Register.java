package com.example.theapp_alfabuild;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener{

	private EditText nickname, password, firstname, lastname, email;
	private Button  mRegister;

	 // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //CRIPTARE
    //private String nicknameCrypt;
    private String passwordCrypt;
    
    //php login script

    //localhost :
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
   // private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/register.php";

    //testing on Emulator:
    private static final String REGISTER_URL = "http://carlitos.bl.ee/webservice2/register2.php";

  //testing from a real server:
    //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/register.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		nickname = (EditText)findViewById(R.id.nickname);
		password = (EditText)findViewById(R.id.password);
		firstname=(EditText)findViewById(R.id.firstname);
		lastname=(EditText)findViewById(R.id.lastname);
		email=(EditText)findViewById(R.id.email);

		mRegister = (Button)findViewById(R.id.register);
		mRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

				new CreateUser().execute();

	}

	//<=========================================================================================>

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

	
	//<=========================================================================================>
	
	class CreateUser extends AsyncTask<String, String, String> {

		 /**
         * Before starting background thread Show Progress Dialog
         * */
		boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
            String nick = nickname.getText().toString();
            String passBrut = password.getText().toString();
            String pass = MD5(passBrut);
            String first= firstname.getText().toString();
            String last=lastname.getText().toString();
            String mail = email.getText().toString();
            
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("nickname", nick));
                params.add(new BasicNameValuePair("password", pass));
                params.add(new BasicNameValuePair("firstname", first));
                params.add(new BasicNameValuePair("lastname", last));
                params.add(new BasicNameValuePair("email", mail));

                Log.d("request!", "starting");

                //Posting user data to script
                String identificator = "register";
                JSONObject json = jsonParser.makeHttpRequest(
                       REGISTER_URL, "POST", params, identificator);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	Log.d("User Created!", json.toString());
                	
                	Intent i = new Intent(Register.this, Login.class);
                	
                	finish();
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
            	Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

	}

}