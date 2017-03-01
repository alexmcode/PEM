package com.example.theapp_alfabuild;

import java.util.ArrayList;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddComment extends Activity implements OnClickListener {

	JSONParser jsonParser = new JSONParser();
	JSONObject jsonMsg;
	JSONArray commentJson = null;
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_COMMENTS = "comments";
	private static final String TAG_ID_COMM = "id_comment";
	private static final String TAG_ID_MSG = "id_message";
	private static final String TAG_TEXT_COMMENT = "text"; 
	
	private static final String INSERT_COMMENT_URL = "http://carlitos.bl.ee/webservice2/commentInsert2.php";
	
	private int IdMessage;
	private EditText comment;
	private Button addComment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_comment);
		comment=(EditText)findViewById(R.id.textComment);
		
		addComment=(Button)findViewById(R.id.btnAddCom);
		addComment.setOnClickListener(this);
		
		Bundle datePrimite = getIntent().getExtras();
		IdMessage = datePrimite.getInt(TAG_ID_COMM);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new InsertComment().execute();
	}
	
	
	public void InsertComment(){
		int idLocal=IdMessage;
		String idLocalString=String.valueOf(idLocal);
		String textLocal= comment.getText().toString();
		
		int success;
		
		try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("textC", textLocal));
            params.add(new BasicNameValuePair("id_message", idLocalString));
            
            Log.d("request!", "starting");
            
            //Posting user data to script 
            String identificator = "clasa Comment Insert identificator";
            JSONObject jsonC = jsonParser.makeHttpRequest(INSERT_COMMENT_URL, "POST", params, identificator);

            // full json response
            Log.d("Post message attempt", jsonC.toString());

            // json success element
            success = jsonC.getInt(TAG_SUCCESS);
            if (success == 1) {
            	Log.d("Comment Inserted!", jsonC.toString());    
            	     
            	Bundle dateUser = new Bundle();
        		int id=IdMessage;
        		String idStr=String.valueOf(id);
            	dateUser.putString(TAG_ID_MSG, idStr);
            	Intent i = new Intent(AddComment.this, Comment.class);
            	i.putExtras(dateUser);
            	startActivity(i);
            	//finish();
            	
            	//return jsonC.getString(TAG_MESSAGE);
            }else{
            	Log.d("Comment insert Failure!", jsonC.getString(TAG_MESSAGE));
            	//return jsonC.getString(TAG_MESSAGE);
            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	
	
	class InsertComment extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			InsertComment();
			
			return null;
		}
		
	}
	
}
