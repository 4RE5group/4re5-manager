package com.ares;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
	
	private ArrayList<HashMap<String, Object>> devices = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> accounts = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout aresnetView;
	private LinearLayout devicesView;
	private TextView textview1;
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
		aresnetView = _view.findViewById(R.id.aresnetView);
		devicesView = _view.findViewById(R.id.devicesView);
		textview1 = _view.findViewById(R.id.textview1);
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
		
		listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				currentServer = devices.get((int)_position).get("url").toString();
				currentToken = devices.get((int)_position).get("token").toString();
				selectedServerName.setText("Connected to ".concat(currentServer));
				devicesView.setVisibility(View.VISIBLE);
				aresnetView.setVisibility(View.GONE);
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
				SketchwareUtil.showMessage(getContext().getApplicationContext(), "selected");
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
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		if (sp.contains("accounts")) {
			accounts = new Gson().fromJson(sp.getString("accounts", ""), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
		}
		else {
			accounts.clear();
			sp.edit().putString("accounts", new Gson().toJson(accounts)).commit();
		}
		listview2.setAdapter(new Listview2Adapter(accounts));
	}
	
	public void _loadDevices() {
		if (SketchwareUtil.isConnected(getContext().getApplicationContext())) {
			header = new HashMap<>();
			header.put("token", currentToken);
			req.setHeaders(header);
			req.startRequestNetwork(RequestNetworkController.GET, currentServer.concat("api/devices"), "devices", _req_request_listener);
			header.clear();
		}
		else {
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "verify your internet connection");
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
			
			botIp.setText(_data.get((int)_position).get("url").toString());
			
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
			
			botStatus.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)10, Color.TRANSPARENT));
			botIp.setText(_data.get((int)_position).get("ip").toString());
			if (_data.get((int)_position).get("status").toString().equals("online")) {
				botStatus.setBackgroundColor(0xFF4CAF50);
			}
			else {
				
			}
			
			return _view;
		}
	}
}