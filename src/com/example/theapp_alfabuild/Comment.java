package com.example.theapp_alfabuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class Comment extends ListActivity implements OnClickListener{
	
	private int IdMessage=0;
	private ArrayList<HashMap<String, String>> CommentList;
	private static final String COMMENT_URL = "http://carlitos.bl.ee/webservice2/commentSelect.php";
	private static final String INSERT_COMMENT_URL = "http://carlitos.bl.ee/webservice2/commentInsert.php";
	
	JSONParser jsonParser = new JSONParser();
	JSONObject jsonMsg;
	JSONArray commentJson = null;
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_COMMENTS = "comments";
	private static final String TAG_ID_COMM = "id_comment";
	private static final String TAG_ID_MSG = "id_message";
	private static final String TAG_TEXT_COMMENT = "text"; 
	
	
	private Button commBtn, btnBack;
	private EditText textComm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.display_comments);
		
		CommentList=new ArrayList<HashMap<String,String>>();
		
		//primire id mesaj click
		Bundle datePrimite = getIntent().getExtras();
		String date = datePrimite.getString(TAG_ID_MSG);
		
		CommentList=new ArrayList<HashMap<String,String>>();
		
		IdMessage = Integer.valueOf(date);
		
		commBtn=(Button)findViewById(R.id.post_comment);
		commBtn.setOnClickListener(this);
		
		btnBack=(Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		//textComm=(EditText)findViewById(R.id.commentText);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//new getCommentsById().execute(IdMessage);
		new LoadComments().execute();
	}
	
	@Override
	public void onClick(View v) {
		
		
		
    	switch (v.getId()) {
		case R.id.post_comment:
			Bundle dateUser = new Bundle();
			int id=IdMessage;
	    	dateUser.putInt(TAG_ID_COMM, id);
	    	Intent i = new Intent(Comment.this, AddComment.class);
	    	i.putExtras(dateUser);
	    	startActivity(i);
			break;
		case R.id.btnBack:
				Intent i2 = new Intent(Comment.this, ManualScanBeta.class);
				startActivity(i2);
			break;

		default:
			break;
		}
    	
	}
	
	public void Back(){
		Intent i=new Intent(Comment.this, AddComment.class);
	}
	
	public void getCommentById(){
		int idMsg=IdMessage;
		String idMsgString = String.valueOf(idMsg);
		
		int success;
		
		 try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_message", idMsgString));
 
                Log.d("request!", "starting");
                 
                String identificator = "clasa Comment identificator";
                JSONObject jsonC = jsonParser.makeHttpRequest(COMMENT_URL, "POST", params, identificator);

                Log.d("Post message attempt", jsonC.toString());
 
                // json success element
                success = jsonC.getInt(TAG_SUCCESS);
                if (success == 1) {
                	
                	//Toast.makeText(Comment.this, "A intrat in succes", Toast.LENGTH_LONG).show();
                	
                	Log.d("Comment Load!", jsonC.toString());    
                	
                	commentJson = jsonC.getJSONArray(TAG_COMMENTS);
                	for (int i = 0; i < commentJson.length(); i++) {
						JSONObject c = commentJson.getJSONObject(i);
						String commText = c.getString(TAG_TEXT_COMMENT);
						
						HashMap<String, String> map = new HashMap<String, String>();
						
						map.put(TAG_TEXT_COMMENT, commText);
						
						//Toast.makeText(Comment.this, commText, Toast.LENGTH_LONG).show();
						
						CommentList.add(map);
                  	}
                }else{
                	Log.d("Message Failure!", jsonC.getString(TAG_MESSAGE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

	}
	
	public void updateList(){
		
		ListAdapter adapter = new SimpleAdapter(this, CommentList, R.layout.single_comment, 
				new String[] {TAG_TEXT_COMMENT}, 
				new int[] {R.id.message});
		
		setListAdapter(adapter);
		
		ListView lv = getListView();	
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		
	}
	
	public boolean HasComments(){
		boolean are=false;
		
		if(CommentList.size() != 0){
			are=true;
		}
		
		return are;
	}
	
	class LoadComments extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub

			getCommentById();
			
			return null;
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(HasComments()==true){
				updateList();
			}else{
				Toast.makeText(Comment.this, "No comments on this message!! ", Toast.LENGTH_LONG).show();
				
				Bundle dateUser = new Bundle();
		    	dateUser.putInt(TAG_ID_COMM, IdMessage);
		    	//dateUser.putString("keyFN", firstname);
		    	
		    	Intent i = new Intent(Comment.this, AddComment.class);
		    	
		    	i.putExtras(dateUser);
				
	    		startActivity(i);
			}
			
			
		}
		
	}

	
	
}
