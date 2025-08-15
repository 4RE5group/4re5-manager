package com.ares;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class DeviceActivity extends AppCompatActivity {
	
	private HashMap<String, Object> device = new HashMap<>();
	private HashMap<String, Object> header = new HashMap<>();
	private HashMap<String, Object> account = new HashMap<>();
	private HashMap<String, Object> payload = new HashMap<>();
	
	private ArrayList<String> position = new ArrayList<>();
	
	private LinearLayout linear1;
	private ScrollView vscroll1;
	private ImageView imageview1;
	private ImageView deviceIcon;
	private LinearLayout deviceStatus;
	private TextView textview1;
	private LinearLayout linear2;
	private LinearLayout linear3;
	private LinearLayout linear4;
	private LinearLayout linear5;
	private LinearLayout linear6;
	private LinearLayout linear8;
	private LinearLayout linear9;
	private LinearLayout linear10;
	private LinearLayout linear11;
	private LinearLayout linear12;
	private TextView textview17;
	private Button button1;
	private Button button2;
	private Button button3;
	private TextView textview2;
	private TextView device_ip;
	private TextView textview4;
	private TextView device_country;
	private TextView textview6;
	private TextView device_arch;
	private TextView textview9;
	private TextView device_date;
	private TextView textview10;
	private TextView device_type;
	private TextView textview12;
	private TextView device_city;
	private TextView textview14;
	private LinearLayout linear13;
	private TextView device_lat;
	private TextView device_long;
	private TextView textview16;
	private TextView device_timezone;
	
	private Intent i = new Intent();
	private RequestNetwork trigger;
	private RequestNetwork.RequestListener _trigger_request_listener;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.device);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		vscroll1 = findViewById(R.id.vscroll1);
		imageview1 = findViewById(R.id.imageview1);
		deviceIcon = findViewById(R.id.deviceIcon);
		deviceStatus = findViewById(R.id.deviceStatus);
		textview1 = findViewById(R.id.textview1);
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		linear4 = findViewById(R.id.linear4);
		linear5 = findViewById(R.id.linear5);
		linear6 = findViewById(R.id.linear6);
		linear8 = findViewById(R.id.linear8);
		linear9 = findViewById(R.id.linear9);
		linear10 = findViewById(R.id.linear10);
		linear11 = findViewById(R.id.linear11);
		linear12 = findViewById(R.id.linear12);
		textview17 = findViewById(R.id.textview17);
		button1 = findViewById(R.id.button1);
		button2 = findViewById(R.id.button2);
		button3 = findViewById(R.id.button3);
		textview2 = findViewById(R.id.textview2);
		device_ip = findViewById(R.id.device_ip);
		textview4 = findViewById(R.id.textview4);
		device_country = findViewById(R.id.device_country);
		textview6 = findViewById(R.id.textview6);
		device_arch = findViewById(R.id.device_arch);
		textview9 = findViewById(R.id.textview9);
		device_date = findViewById(R.id.device_date);
		textview10 = findViewById(R.id.textview10);
		device_type = findViewById(R.id.device_type);
		textview12 = findViewById(R.id.textview12);
		device_city = findViewById(R.id.device_city);
		textview14 = findViewById(R.id.textview14);
		linear13 = findViewById(R.id.linear13);
		device_lat = findViewById(R.id.device_lat);
		device_long = findViewById(R.id.device_long);
		textview16 = findViewById(R.id.textview16);
		device_timezone = findViewById(R.id.device_timezone);
		trigger = new RequestNetwork(this);
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				finish();
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_triggerApi("devicePing", device.get("ip").toString());
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_triggerApi("deviceDisconnect", device.get("ip").toString());
			}
		});
		
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
		linear13.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				position = new Gson().fromJson(new Gson().toJson((ArrayList<String>)device.get("pos")), new TypeToken<ArrayList<String>>(){}.getType());
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://www.google.com/maps?q=".concat(position.get((int)(0)).concat(",".concat(position.get((int)(1)))))));
				startActivity(i);
			}
		});
		
		_trigger_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		device = new HashMap<>();
		device.clear();
		linear3.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)20, 0xFF1B2430));
		if (getIntent().hasExtra("device")) {
			device = new Gson().fromJson(getIntent().getStringExtra("device"), new TypeToken<HashMap<String, Object>>(){}.getType());
			textview1.setText(device.get("user").toString().concat(" @".replace(" ", "").concat(device.get("ip").toString().concat(":".concat(device.get("port").toString())))));
			device_ip.setText(device.get("ip").toString());
			device_country.setText(device.get("country").toString());
			device_arch.setText(device.get("country").toString());
			device_date.setText(device.get("join-date").toString());
			device_type.setText(device.get("type").toString());
			device_city.setText(device.get("city").toString());
			position = new Gson().fromJson(new Gson().toJson((ArrayList<String>)device.get("pos")), new TypeToken<ArrayList<String>>(){}.getType());
			device_lat.setText(position.get((int)(0)));
			device_long.setText(position.get((int)(1)));
			device_timezone.setText(device.get("timezone").toString());
			if ("online".equals(device.get("status").toString())) {
				deviceStatus.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)deviceStatus.getWidth() / 2, 0xFF4CAF50));
			}
			else {
				deviceStatus.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)deviceStatus.getWidth() / 2, 0xFFF44336));
			}
			switch(device.get("type").toString().trim().toUpperCase()) {
				default: {
					deviceIcon.setImageResource(R.drawable.ic_dns_white);
					break;
				}
				case "WINDOWS": {
					deviceIcon.setImageResource(R.drawable.windows);
					break;
				}
				case "LINUX": {
					deviceIcon.setImageResource(R.drawable.raspberry100_1);
					break;
				}
				case "IOT": {
					deviceIcon.setImageResource(R.drawable.ic_memory_white);
					break;
				}
				case "RASPBERRY": {
					deviceIcon.setImageResource(R.drawable.raspberry100_2);
					break;
				}
			}
		}
		else {
			finish();
		}
	}
	
	public void _triggerApi(final String _name, final String _data) {
		header = new HashMap<>();
		payload = new HashMap<>();
		header.put("Cookie", "token=".concat(account.get("token").toString()));
		payload.put("name", _name);
		payload.put("data", _data);
		trigger.setParams(payload, RequestNetworkController.REQUEST_BODY);
		trigger.setHeaders(header);
		trigger.startRequestNetwork(RequestNetworkController.POST, account.get("url").toString().concat("api/trigger"), "", _trigger_request_listener);
		header.clear();
		payload.clear();
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}