package com.ares;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import com.budiyev.android.codescanner.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class AresnetmainFragmentActivity extends Fragment {
	
	private String currentServer = "";
	private HashMap<String, Object> header = new HashMap<>();
	private String currentToken = "";
	private ProgressDialog prog;
	
	private ArrayList<HashMap<String, Object>> devices = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> accounts = new ArrayList<>();
	
	private LinearLayout linear1;
	private SwipeRefreshLayout swiperefreshlayout1;
	private TextView textview1;
	private LinearLayout linear8;
	private LinearLayout aresnetView;
	private LinearLayout devicesView;
	private TextView textview2;
	private ListView listview2;
	private LinearLayout linear5;
	private TextView textview4;
	private TextView textview5;
	private LinearLayout linear4;
	private ListView listview1;
	private ImageView imageview1;
	private TextView selectedServerName;
	
	private RequestNetwork req;
	private RequestNetwork.RequestListener _req_request_listener;
	private Intent i = new Intent();
	private SharedPreferences sp;
	
	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.aresnetmain_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		linear1 = _view.findViewById(R.id.linear1);
		swiperefreshlayout1 = _view.findViewById(R.id.swiperefreshlayout1);
		textview1 = _view.findViewById(R.id.textview1);
		linear8 = _view.findViewById(R.id.linear8);
		aresnetView = _view.findViewById(R.id.aresnetView);
		devicesView = _view.findViewById(R.id.devicesView);
		textview2 = _view.findViewById(R.id.textview2);
		listview2 = _view.findViewById(R.id.listview2);
		linear5 = _view.findViewById(R.id.linear5);
		textview4 = _view.findViewById(R.id.textview4);
		textview5 = _view.findViewById(R.id.textview5);
		linear4 = _view.findViewById(R.id.linear4);
		listview1 = _view.findViewById(R.id.listview1);
		imageview1 = _view.findViewById(R.id.imageview1);
		selectedServerName = _view.findViewById(R.id.selectedServerName);
		req = new RequestNetwork((Activity) getContext());
		sp = getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);
		
		swiperefreshlayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (aresnetView.getVisibility() == View.VISIBLE) {
					_loadAccs();
					swiperefreshlayout1.setRefreshing(false);
				}
				else {
					_loadDevices();
				}
			}
		});
		
		listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				try{
					_ProgresbarShow("loading devices...");
					currentServer = DecryptingTheTextMethod(accounts.get((int)_position).get("url").toString(),sp.getString("pattern", ""));
					currentToken = DecryptingTheTextMethod(accounts.get((int)_position).get("token").toString(),sp.getString("pattern", ""));
					selectedServerName.setText("Connected to ".concat(currentServer));
					_loadDevices();
				}catch(Exception e){
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "error, no pattern set");
				}
			}
		});
		
		textview5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setClass(getContext().getApplicationContext(), AddaccActivity.class);
				startActivity(i);
			}
		});
		
		listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				i.setAction(Intent.ACTION_VIEW);
				i.putExtra("device", new Gson().toJson(devices.get((int)(_position))));
				i.setClass(getContext().getApplicationContext(), DeviceActivity.class);
				startActivity(i);
			}
		});
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				currentServer = "";
				devicesView.setVisibility(View.GONE);
				aresnetView.setVisibility(View.VISIBLE);
			}
		});
		
		_req_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				devices.clear();
				devices = new Gson().fromJson(_response, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
				listview1.setAdapter(new Listview1Adapter(devices));
				((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				devicesView.setVisibility(View.VISIBLE);
				aresnetView.setVisibility(View.GONE);
				swiperefreshlayout1.setRefreshing(false);
				_ProgresbarDimiss();
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				SketchwareUtil.showMessage(getContext().getApplicationContext(), "Request Unauthorised or could not reach host");
				swiperefreshlayout1.setRefreshing(false);
				_logMessage(_message);
				_ProgresbarDimiss();
			}
		};
	}
	
	private void initializeLogic() {
		listview2.setAdapter(new Listview2Adapter(accounts));
		_loadAccs();
	}
	
	public void _loadDevices() {
		if (SketchwareUtil.isConnected(getContext().getApplicationContext())) {
			header.clear();
			header = new HashMap<>();
			header.put("Cookie", "token=".concat(currentToken));
			req.setParams(header, RequestNetworkController.REQUEST_BODY);
			req.setHeaders(header);
			req.startRequestNetwork(RequestNetworkController.GET, currentServer.concat("api/devices"), "devices", _req_request_listener);
		}
		else {
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "verify your internet connection");
		}
		swiperefreshlayout1.setRefreshing(false);
	}
	
	
	public void _extra() {
	}
	public String EcryptingTheTextMethod(final String _string, final String _key) {
				try{
			javax.crypto.SecretKey key = generateKey(_key);
			javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
			c.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
			byte[] encVal = c.doFinal(_string.getBytes());
			return android.util.Base64.encodeToString(encVal,android.util.Base64.DEFAULT);
				} catch (Exception e) {
				}
		return "";
		}
	
		public String DecryptingTheTextMethod(final String _string, final String _key) {
				try {
			javax.crypto.spec.SecretKeySpec key = (javax.crypto.spec.SecretKeySpec) generateKey(_key);
			javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
			c.init(javax.crypto.Cipher.DECRYPT_MODE,key);
			byte[] decode = android.util.Base64.decode(_string,android.util.Base64.DEFAULT);
			byte[] decval = c.doFinal(decode);
			return new String(decval);
				} catch (Exception ex) {
				}
		return "";
		}
		public static javax.crypto.SecretKey generateKey(String pwd) throws Exception {
		final java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
		byte[] b = pwd.getBytes("UTF-8");
		digest.update(b,0,b.length);
		byte[] key = digest.digest();
		javax.crypto.spec.SecretKeySpec sec = new javax.crypto.spec.SecretKeySpec(key, "AES");
		return sec;
		}
	{
	}
	
	
	public void _loadAccs() {
		if (sp.contains("accounts")) {
			accounts = new Gson().fromJson(sp.getString("accounts", ""), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
		}
		else {
			accounts.clear();
			sp.edit().putString("accounts", new Gson().toJson(accounts)).commit();
		}
		listview2.setAdapter(new Listview2Adapter(accounts));
		((BaseAdapter)listview2.getAdapter()).notifyDataSetChanged();
	}
	
	
	public void _logMessage(final String _message) {
		if (FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/4re5 group/log.txt"))) {
			FileUtil.writeFile(FileUtil.getExternalStorageDir(), FileUtil.readFile(FileUtil.getExternalStorageDir().concat("/4re5 group/log.txt")).concat("\n".concat(_message)));
		}
		else {
			FileUtil.writeFile(FileUtil.getExternalStorageDir(), _message);
		}
	}
	
	
	public void _ProgresbarShow(final String _title) {
		Context context = getContext();
		if (context != null) {
			prog = new ProgressDialog(context);
			prog.setMax(100);
			prog.setMessage(_title);
			prog.setIndeterminate(true);
			prog.setCancelable(false);
			prog.show();
		}
	}
	
	
	public void _ProgresbarDimiss() {
		if(prog != null)
		{
			prog.dismiss();
		}
	}
	
	public class Listview2Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview2Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getActivity().getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.deviceview, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView botIcon = _view.findViewById(R.id.botIcon);
			final LinearLayout botStatus = _view.findViewById(R.id.botStatus);
			final TextView botIp = _view.findViewById(R.id.botIp);
			
			try{
				botIp.setText(DecryptingTheTextMethod(_data.get((int)_position).get("url").toString(),sp.getString("pattern", "")));
				if ("4re5net".equals(DecryptingTheTextMethod(_data.get((int)_position).get("type").toString(),sp.getString("pattern", "")).toLowerCase().trim())) {
					botIcon.setImageResource(R.drawable.icon1_1);
				}
				else {
					botIcon.setImageResource(R.drawable.ic_cloud_queue_white);
				}
				botStatus.setVisibility(View.INVISIBLE);
			}catch(Exception e){
				SketchwareUtil.showMessage(getContext().getApplicationContext(), "could not view server list");
			}
			
			return _view;
		}
	}
	
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getActivity().getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.deviceview, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView botIcon = _view.findViewById(R.id.botIcon);
			final LinearLayout botStatus = _view.findViewById(R.id.botStatus);
			final TextView botIp = _view.findViewById(R.id.botIp);
			
			try{
				botIp.setText(_data.get((int)_position).get("ip").toString());
				if (_data.get((int)_position).get("status").toString().equals("online")) {
					botStatus.setBackgroundColor(0xFF4CAF50);
					botStatus.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)botStatus.getWidth() / 2, 0xFF4CAF50));
				}
				else {
					botStatus.setBackgroundColor(0xFFF44336);
					botStatus.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)botStatus.getWidth() / 2, 0xFFF44336));
				}
				switch(_data.get((int)_position).get("type").toString().toUpperCase().trim()) {
					case "WINDOWS": {
						botIcon.setImageResource(R.drawable.windows);
						break;
					}
					case "LINUX": {
						botIcon.setImageResource(R.drawable.raspberry100_1);
						break;
					}
					case "IOT": {
						botIcon.setImageResource(R.drawable.ic_memory_white);
						break;
					}
					case "RASPBERRY": {
						botIcon.setImageResource(R.drawable.raspberry100_2);
						break;
					}
				}
			}catch(Exception e){
				SketchwareUtil.showMessage(getContext().getApplicationContext(), "could not fetch device list");
			}
			
			return _view;
		}
	}
}