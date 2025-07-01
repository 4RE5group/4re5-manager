package com.ares;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
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
import com.google.zxing.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class ProcselectActivity extends AppCompatActivity {
	
	private HashMap<String, Object> emptyMap = new HashMap<>();
	
	private ArrayList<HashMap<String, Object>> processList = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private ImageView imageview1;
	private TextView textview1;
	private ListView listview1;
	
	private SharedPreferences sp;
	private AlertDialog.Builder d;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.procselect);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		listview1 = findViewById(R.id.listview1);
		sp = getSharedPreferences("sharedData", Activity.MODE_PRIVATE);
		d = new AlertDialog.Builder(this);
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				finish();
			}
		});
		
		listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				sp.edit().putString("returnValue", new Gson().toJson(processList.get((int)(_position)))).commit();
				finish();
			}
		});
	}
	
	private void initializeLogic() {
		try {
			
			Runtime.getRuntime().exec("su"); 
			
			emptyMap = new HashMap<>();
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			
			// Get the list of running processes
			List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
			
			if (runningProcesses != null) {
					int i=0;
					for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
					emptyMap.put("name", processInfo.processName.toString());
					SketchwareUtil.showMessage(getApplicationContext(), processInfo.processName.toString());
					emptyMap.put("pid", String.valueOf((long)(processInfo.pid)));
					processList.add(emptyMap);
					processList.set((int)(i), emptyMap);
					emptyMap.clear();
					i++;
				}}
			listview1.setAdapter(new Listview1Adapter(processList));
			((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
			
		} catch (Exception e ) {
			
			d.setTitle("error");
			d.setIcon(R.drawable.icon_1);
			d.setMessage("error, your device must be root to be able to access 4re5 mods on android, anyway try to root it or switch to desktop version");
			d.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface _dialog, int _which) {
					finish();
				}
			});
			d.create().show();
			finish();
			
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
			LayoutInflater _inflater = getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.procview, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView icon = _view.findViewById(R.id.icon);
			final TextView name = _view.findViewById(R.id.name);
			final TextView pid = _view.findViewById(R.id.pid);
			
			try{
				name.setText(_data.get((int)_position).get("name").toString());
				pid.setText(_data.get((int)_position).get("pid").toString());
			}catch(Exception e){
				SketchwareUtil.showMessage(getApplicationContext(), "error loading list of process");
			}
			
			return _view;
		}
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