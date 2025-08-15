package com.ares;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.Context;
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
import android.os.Vibrator;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.regex.*;
import org.json.*;

public class ModmanagerActivity extends AppCompatActivity {
	
	private HashMap<String, Object> map = new HashMap<>();
	private double pid = 0;
	private String procName = "";
	private boolean flag = false;
	private HashMap<String, Object> selectedproc = new HashMap<>();
	
	private ArrayList<HashMap<String, Object>> options = new ArrayList<>();
	private ArrayList<String> favourites = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private ImageView imageview1;
	private TextView textview1;
	private ImageView imageview2;
	private LinearLayout linear3;
	private ListView listview1;
	private ImageView selectedAppIcon;
	private LinearLayout linear4;
	private Button pickApp;
	private TextView SelectedAppName;
	private TextView selectedAppPid;
	
	private SharedPreferences sp;
	private Intent i = new Intent();
	private Vibrator v;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.modmanager);
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
		imageview2 = findViewById(R.id.imageview2);
		linear3 = findViewById(R.id.linear3);
		listview1 = findViewById(R.id.listview1);
		selectedAppIcon = findViewById(R.id.selectedAppIcon);
		linear4 = findViewById(R.id.linear4);
		pickApp = findViewById(R.id.pickApp);
		SelectedAppName = findViewById(R.id.SelectedAppName);
		selectedAppPid = findViewById(R.id.selectedAppPid);
		sp = getSharedPreferences("sharedData", Activity.MODE_PRIVATE);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				finish();
			}
		});
		
		imageview2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				v.vibrate((long)(30));
				if (favourites.contains(map.get("name").toString())) {
					favourites.remove((int)(favourites.indexOf(map.get("name").toString())));
					imageview2.setImageResource(R.drawable.ic_favorite_outline_white);
				}
				else {
					favourites.add(map.get("name").toString());
					imageview2.setImageResource(R.drawable.ic_favorite_white);
				}
				FileUtil.writeFile(FileUtil.getExternalStorageDir().concat("/4re5 group/mods/favourites.json"), new Gson().toJson(favourites));
			}
		});
		
		pickApp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (sp.contains("returnValue")) {
					sp.edit().remove("returnValue").commit();
				}
				i.setAction(Intent.ACTION_VIEW);
				i.setClass(getApplicationContext(), ProcselectActivity.class);
				startActivity(i);
				overridePendingTransition(0, 0);
			}
		});
	}
	
	private void initializeLogic() {
		if (sp.contains("returnValue")) {
			sp.edit().remove("returnValue").commit();
		}
		map = new HashMap<>();
		SketchwareUtil.showMessage(getApplicationContext(), "This feature is still under development");
		try{
			if (sp.contains("sharedModMap")) {
				listview1.setAdapter(new Listview1Adapter(options));
				map = new Gson().fromJson(sp.getString("sharedModMap", ""), new TypeToken<HashMap<String, Object>>(){}.getType());
				options = new Gson().fromJson(new Gson().toJson((ArrayList<HashMap<String,Object>>)map.get("options")), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
				textview1.setText(map.get("name").toString());
				listview1.setAdapter(new Listview1Adapter(options));
				favourites = new Gson().fromJson(FileUtil.readFile(FileUtil.getExternalStorageDir().concat("/4re5 group/mods/favourites.json")), new TypeToken<ArrayList<String>>(){}.getType());
				if (favourites.contains(map.get("name").toString())) {
					imageview2.setImageResource(R.drawable.ic_favorite_white);
				}
			}
			else {
				SketchwareUtil.showMessage(getApplicationContext(), "no passed json data... finishing");
				finish();
			}
		}catch(Exception e){
			SketchwareUtil.showMessage(getApplicationContext(), "error while loading options... exiting");
			finish();
		}
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		layoutParams = new WindowManager.LayoutParams();
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			
			layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
			
		} else {
			
			layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
			
		}
		
		
		layoutParams.format = PixelFormat.RGBA_8888;
		
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		
		layoutParams.width = 850; layoutParams.height = 1000;
		
		layoutParams.x = 0; layoutParams.y = 0;
		
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (sp.contains("returnValue")) {
			try{
				selectedproc = new Gson().fromJson(sp.getString("returnValue", ""), new TypeToken<HashMap<String, Object>>(){}.getType());
				SelectedAppName.setText(selectedproc.get("name").toString());
				selectedAppPid.setText(selectedproc.get("pid").toString());
				sp.edit().remove("returnValue").commit();
				try{
					Drawable app_icon = getPackageManager().getApplicationIcon(selectedproc.get("name").toString());
					
					selectedAppIcon.setImageDrawable(app_icon);
				}catch(Exception e){
					 
				}
			}catch(Exception e){
				SketchwareUtil.showMessage(getApplicationContext(), "invalid selected process");
			}
		}
	}
	public void _callSetValue(final String _offset, final String _value, final double _pid) {
		
	}
	
	
	public void _FloatingWindow() {
	}
	private WindowManager windowManager;
	
	private WindowManager.LayoutParams layoutParams;
	
	private View displayView;
	
	
	private void showFloatingWindow() {
		
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		
		displayView = layoutInflater.inflate(R.layout.custom_floating, null);
		
		displayView.setOnTouchListener(new FloatingOnTouchListener());
		
		Button btn1 = displayView.findViewById(R.id.button_close);
		
		ImageView img1 = displayView.findViewById(R.id.imageview1);
		
		Switch swi1 = displayView.findViewById(R.id.switch1);
		
		Switch swi2 = displayView.findViewById(R.id.switch2);
		
		Switch swi3 = displayView.findViewById(R.id.switch3);
		
		CheckBox CB1 = displayView.findViewById(R.id.checkbox1);
		
		CheckBox CB2 = displayView.findViewById(R.id.checkbox2);
		swi1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton _param1, boolean _param2)  {
								final boolean _isChecked = _param2;
								if (_isChecked) {
										SketchwareUtil.showMessage(getApplicationContext(), "Name ✓");
								}
								else {
										SketchwareUtil.showMessage(getApplicationContext(), "Name X");
								}
						}
				});
		
		
		swi2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton _param1, boolean _param2)  {
								final boolean _isChecked = _param2;
								if (_isChecked) {
										SketchwareUtil.showMessage(getApplicationContext(), "Health ✓");
								}
								else {
										SketchwareUtil.showMessage(getApplicationContext(), "Health X");
								}
						}
				});
		
		
		swi3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton _param1, boolean _param2)  {
								final boolean _isChecked = _param2;
								if (_isChecked) {
										SketchwareUtil.showMessage(getApplicationContext(), "Team ✓");
								}
								else {
										SketchwareUtil.showMessage(getApplicationContext(), "Team X");
								}
						}
				});
				
				CB1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton _param1, boolean _param2)  {
								final boolean _isChecked = _param2;
								if (_isChecked) {
										SketchwareUtil.showMessage(getApplicationContext(), "Root ✓");
								}
								else {
										SketchwareUtil.showMessage(getApplicationContext(), "Root X");
								}
						}
				});
		
		
		CB2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton _param1, boolean _param2)  {
								final boolean _isChecked = _param2;
								if (_isChecked) {
										SketchwareUtil.showMessage(getApplicationContext(), "No Root ✓");
								}
								else {
										SketchwareUtil.showMessage(getApplicationContext(), "No Root X");
								}
						}
				});
		
		
		btn1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				closes();
				
			}
			
		});
		
		
		img1.setOnClickListener(new OnClickListener(){ public void onClick(View v) {
				
				if (flag) {
					
					layoutParams.width = 850; layoutParams.height = 1000;
					
					flag = false;
					
				} else{
					
					layoutParams.width = 140; layoutParams.height = 140;
					
					flag = true;
					
				}
				
				closes();
				
				showFloatingWindow();
				
			}
			
		});
		
		
		windowManager.addView(displayView, layoutParams);
		
	}
	
	
	private class FloatingOnTouchListener implements View.OnTouchListener {
		
		private int x;
		
		private int y;
		
		
		@Override public boolean onTouch(View view, MotionEvent event) {
			
			
			switch (event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
				
				x = (int) event.getRawX();
				
				y = (int) event.getRawY();
				
				break;
				
				
				case MotionEvent.ACTION_MOVE: int nowX = (int) event.getRawX();
				
				int nowY = (int) event.getRawY();
				
				int movedX = nowX - x;
				
				int movedY = nowY - y;
				
				x = nowX; y = nowY;
				
				layoutParams.x = layoutParams.x + movedX;
				
				layoutParams.y = layoutParams.y + movedY; windowManager.updateViewLayout(view, layoutParams);
				
				break;
				
				default:
				
				break;
				
			}
			
			return true;
			
		}
		
	}
	
	
	public void closes(){
		
		try{
			
			windowManager.removeView(displayView);
			
		}
		
		catch(Exception e){
			
		}
		
	}
	
	
	{
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
				_view = _inflater.inflate(R.layout.modoption, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final Switch switchType = _view.findViewById(R.id.switchType);
			final LinearLayout rangeType = _view.findViewById(R.id.rangeType);
			final TextView labelType = _view.findViewById(R.id.labelType);
			final LinearLayout inputType = _view.findViewById(R.id.inputType);
			final TextView valRange = _view.findViewById(R.id.valRange);
			final SeekBar isRange = _view.findViewById(R.id.isRange);
			final TextView rangeValue = _view.findViewById(R.id.rangeValue);
			final EditText edittext1 = _view.findViewById(R.id.edittext1);
			final Button button1 = _view.findViewById(R.id.button1);
			
			switchType.setVisibility(View.GONE);
			inputType.setVisibility(View.GONE);
			rangeType.setVisibility(View.GONE);
			labelType.setVisibility(View.GONE);
			if (_data.get((int)_position).get("editType").toString().equals("input")) {
				edittext1.setHint(_data.get((int)_position).get("text").toString());
				inputType.setVisibility(View.VISIBLE);
			}
			else {
				if (_data.get((int)_position).get("editType").toString().equals("switch")) {
					switchType.setVisibility(View.VISIBLE);
					switchType.setText(_data.get((int)_position).get("text").toString());
				}
				else {
					if (_data.get((int)_position).get("editType").toString().equals("range")) {
						valRange.setText(_data.get((int)_position).get("text").toString());
						rangeType.setVisibility(View.VISIBLE);
						try{
							isRange.setProgress((int)Double.parseDouble(_data.get((int)_position).get("min").toString()));
							isRange.setMax((int)Double.parseDouble(_data.get((int)_position).get("max").toString()));
						}catch(Exception e){
							 
						}
						isRange.setOnTouchListener(new View.OnTouchListener(){
							@Override
							public boolean onTouch(View _view, MotionEvent _motionEvent){
								rangeValue.setText(String.valueOf((long)(isRange.getProgress())));
								return true;
							}
						});
					}
					else {
						labelType.setVisibility(View.VISIBLE);
						labelType.setText(_data.get((int)_position).get("text").toString());
					}
				}
			}
			switchType.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					if (switchType.isChecked()) {
						_callSetValue(_data.get((int)_position).get("offset").toString(), "", pid);
					}
					else {
						
					}
				}
			});
			
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