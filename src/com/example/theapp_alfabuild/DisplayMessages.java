package com.example.theapp_alfabuild;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListAdapter;

public class DisplayMessages extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_messages);
		
		
		
	}
	
	private void updateList() {
		// For a ListActivity we need to set the List Adapter, and in order to do
		//that, we need to create a ListAdapter.  This SimpleAdapter,
		//will utilize our updated Hashmapped ArrayList, 
		//use our single_post xml template for each item in our list,
		//and place the appropriate info from the list to the
		//correct GUI id.  Order is important here.
		/*ListAdapter adapter = new SimpleAdapter(this, mCommentList,
				R.layout.single_post, new String[] { TAG_TITLE, TAG_MESSAGE,
						TAG_USERNAME }, new int[] { R.id.title, R.id.message,
						R.id.username });

		// I shouldn't have to comment on this one:
		setListAdapter(adapter);
		
		// Optional: when the user clicks a list item we 
		//could do something.  However, we will choose
		//to do nothing...
		ListView lv = getListView();	
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// This method is triggered if an item is click within our
				// list. For our example we won't be using this, but
				// it is useful to know in real life applications.

			}
		});
	}  */     
}
}
