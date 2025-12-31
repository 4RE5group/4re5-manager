package com.ares;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.EditText;
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
import com.budiyev.android.codescanner.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class ProcselectActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	
	private HashMap<String, Object> emptyMap = new HashMap<>();
	private String result = "";
	private String scriptpath = "";
	private ProgressDialog prog;
	private boolean searching = false;
	
	private ArrayList<HashMap<String, Object>> processList = new ArrayList<>();
	private ArrayList<String> procFolders = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> searchOutput = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private ImageView imageview1;
	private TextView textview1;
	private LinearLayout linear3;
	private TextView noresult;
	private ListView listview1;
	private EditText edittext1;
	private ImageView imageview2;
	
	private SharedPreferences sp;
	private AlertDialog.Builder d;
	private RequestNetwork script;
	private RequestNetwork.RequestListener _script_request_listener;
	private TimerTask t;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.procselect);
		initialize(_savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		linear3 = findViewById(R.id.linear3);
		noresult = findViewById(R.id.noresult);
		listview1 = findViewById(R.id.listview1);
		edittext1 = findViewById(R.id.edittext1);
		imageview2 = findViewById(R.id.imageview2);
		sp = getSharedPreferences("sharedData", Activity.MODE_PRIVATE);
		d = new AlertDialog.Builder(this);
		script = new RequestNetwork(this);
		
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
				if (searching) {
					sp.edit().putString("returnValue", new Gson().toJson(searchOutput.get((int)(_position)))).commit();
				}
				else {
					sp.edit().putString("returnValue", new Gson().toJson(processList.get((int)(_position)))).commit();
				}
				finish();
			}
		});
		
		edittext1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				if (_charSeq.endsWith("\n")) {
					edittext1.setText(edittext1.getText().toString().replace("\n", ""));
					searchOutput.clear();
					for (int i = 0; i < (int)(processList.size()); i++) {
						if (processList.get((int)i).get("name").toString().toLowerCase().trim().contains(_charSeq.toLowerCase().trim())) {
							searchOutput.add(processList.get((int)(i)));
						}
					}
					searching = true;
					if (searchOutput.size() == 0) {
						noresult.setVisibility(View.VISIBLE);
					}
					else {
						noresult.setVisibility(View.GONE);
						listview1.setAdapter(new Listview1Adapter(searchOutput));
						((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
					}
				}
				if (_charSeq.equals("")) {
					searching = false;
					listview1.setAdapter(new Listview1Adapter(processList));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
		
		imageview2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				edittext1.setText(edittext1.getText().toString().concat("\n"));
			}
		});
		
		_script_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				FileUtil.writeFile(scriptpath, _response);
				_loadProcs();
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				SketchwareUtil.showMessage(getApplicationContext(), _message);
			}
		};
	}
	
	private void initializeLogic() {
		scriptpath = FileUtil.getExternalStorageDir().concat("/4re5 group/mods/4re5proc.sh");
		noresult.setVisibility(View.GONE);
		t = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!result.equals("")) {
							processList = new Gson().fromJson(result, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
							result = "";
							SketchwareUtil.showMessage(getApplicationContext(), "displaying app list");
							listview1.setAdapter(new Listview1Adapter(processList));
							((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
						}
					}
				});
			}
		};
		_timer.scheduleAtFixedRate(t, (int)(3000), (int)(200));
		if (!FileUtil.isExistFile(scriptpath)) {
			script.startRequestNetwork(RequestNetworkController.GET, "https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/mods/4re5procs.sh", "", _script_request_listener);
		}
		else {
			_loadProcs();
		}
	}
	
	public void _loadProcs() {
		if (RootExec.canRunRootCommands()) {
			_ProgresbarShow("loading injectable apps...");
			listview1.setAdapter(new Listview1Adapter(processList));
			if (FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/4re5 group/tmp/tmp.log"))) {
				FileUtil.deleteFile(FileUtil.getExternalStorageDir().concat("/4re5 group/tmp/tmp.log"));
			}
			searching = false;
			result = "";
			AsyncTask.execute(new Runnable() {
				   @Override
				   public void run() {
					try{
						result = RootExec.runCommand(
						"chmod +x \"".concat(scriptpath.concat("\" && sh \"".concat(scriptpath.concat("\""))))
						);
						FileUtil.writeFile(FileUtil.getExternalStorageDir().concat("/4re5 group/tmp/tmp.log"), result);
					}catch(Exception e){
						finishAffinity();
					}
					_ProgresbarDimiss();
					   }
			});
			((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
		}
		else {
			d.setMessage("error, your device must be rooted to be able to access 4re5 mods on android, anyway try to root it or switch to desktop version");
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
	
	
	public void _ProgresbarShow(final String _title) {
		prog = new ProgressDialog(ProcselectActivity.this);
		prog.setMax(100);
		prog.setMessage(_title);
		prog.setIndeterminate(true);
		prog.setCancelable(false);
		prog.show();
	}
	
	
	public void _ProgresbarDimiss() {
		if(prog != null)
		{
			prog.dismiss();
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
			
			if (_data.get((int)_position).get("name").toString().contains("/") || (_data.get((int)_position).get("name").toString().contains(":") || _data.get((int)_position).get("name").toString().contains(" @".replace(" ", "")))) {
				linear1.setVisibility(View.GONE);
			}
			else {
				name.setText(_data.get((int)_position).get("name").toString());
				try{
					pid.setText(String.valueOf((long)(Double.parseDouble(_data.get((int)_position).get("pid").toString()))));
				}catch(Exception e){
					SketchwareUtil.showMessage(getApplicationContext(), "invalid pid");
				}
				try{
					Drawable app_icon = getPackageManager().getApplicationIcon(_data.get(_position).get("name").toString());
					
					 //icon.setImageDrawable(app_icon);
					icon.setImageDrawable(app_icon);
				}catch(Exception e){
					icon.setImageResource(R.drawable.defaulticon);
				}
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