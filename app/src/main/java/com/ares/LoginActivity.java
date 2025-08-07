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
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.*;
import com.andrognito.patternlockview.utils.*;
import com.budiyev.android.codescanner.*;
import com.google.zxing.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class LoginActivity extends AppCompatActivity {
	
	private String lock = "";
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private TextView textDefine;
	private TextView loginMessage;
	private TextView md5hash;
	private PatternLockView patternlockview1;
	private ImageView imageview1;
	private TextView textview1;
	
	private SharedPreferences sp;
	private Intent i = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.login);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		textDefine = findViewById(R.id.textDefine);
		loginMessage = findViewById(R.id.loginMessage);
		md5hash = findViewById(R.id.md5hash);
		patternlockview1 = findViewById(R.id.patternlockview1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		sp = getSharedPreferences("data", Activity.MODE_PRIVATE);
		
		textDefine.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View _view) {
				
				return true;
			}
		});
		
		patternlockview1.addPatternLockListener(new PatternLockViewListener() {
			@Override
			public void onStarted() {
				
			}
			
			@Override
			public void onProgress(List<PatternLockView.Dot> _pattern) {
				lock = PatternLockUtils.patternToMD5(patternlockview1, _pattern);
				md5hash.setText(lock);
			}
			
			@Override
			public void onComplete(List<PatternLockView.Dot> _pattern) {
				lock = PatternLockUtils.patternToMD5(patternlockview1, _pattern);
				md5hash.setText(lock);
				if (sp.contains("pattern")) {
					if (sp.getString("pattern", "").equals(lock)) {
						patternlockview1.setViewMode(PatternLockView.PatternViewMode.CORRECT);
					}
					else {
						patternlockview1.setViewMode(PatternLockView.PatternViewMode.WRONG);
						SketchwareUtil.showMessage(getApplicationContext(), "wrong pattern, closing app");
						return;
					}
				}
				else {
					sp.edit().putString("pattern", lock).commit();
					SketchwareUtil.showMessage(getApplicationContext(), "successfully saved login pattern");
				}
				patternlockview1.clearPattern();
				overridePendingTransition(0, 0);
				i.setAction(Intent.ACTION_VIEW);
				i.setClass(getApplicationContext(), MainviewActivity.class);
				startActivity(i);
				finish();
			}
			
			@Override
			public void onCleared() {
				
			}
		});
	}
	
	private void initializeLogic() {
		patternlockview1.setDotCount((int)3);
		patternlockview1.setNormalStateColor(0xFF3984D4);
		if (sp.contains("pattern")) {
			textDefine.setVisibility(View.GONE);
		}
		else {
			loginMessage.setVisibility(View.GONE);
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