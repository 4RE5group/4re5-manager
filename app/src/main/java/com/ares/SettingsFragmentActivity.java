package com.ares;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.budiyev.android.codescanner.*;
import com.google.zxing.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class SettingsFragmentActivity extends Fragment {
	
	private HashMap<String, Object> settingsDefault = new HashMap<>();
	
	private ArrayList<String> tmpListMap = new ArrayList<>();
	
	private ScrollView vscroll1;
	private LinearLayout linear1;
	private TextView textview1;
	private LinearLayout linear3;
	private TextView textview3;
	private Switch developerMode;
	private LinearLayout linear4;
	private TextView textview4;
	private Button button1;
	
	private SharedPreferences sp;
	private Vibrator v;
	private Intent i = new Intent();
	
	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.settings_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		vscroll1 = _view.findViewById(R.id.vscroll1);
		linear1 = _view.findViewById(R.id.linear1);
		textview1 = _view.findViewById(R.id.textview1);
		linear3 = _view.findViewById(R.id.linear3);
		textview3 = _view.findViewById(R.id.textview3);
		developerMode = _view.findViewById(R.id.developerMode);
		linear4 = _view.findViewById(R.id.linear4);
		textview4 = _view.findViewById(R.id.textview4);
		button1 = _view.findViewById(R.id.button1);
		sp = getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);
		v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		developerMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_setSettingCheckBox(developerMode.isChecked(), "developerMode");
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (sp.contains("pattern")) {
					sp.edit().remove("pattern").commit();
				}
				else {
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "pattern is not set, creating");
				}
				i.setAction(Intent.ACTION_VIEW);
				i.setClass(getContext().getApplicationContext(), LoginActivity.class);
				startActivity(i);
			}
		});
	}
	
	private void initializeLogic() {
		_parseSettings();
	}
	
	public void _parseSettings() {
		settingsDefault = new HashMap<>();
		settingsDefault.put("developerMode", false);
		SketchwareUtil.getAllKeysFromMap(settingsDefault, tmpListMap);
		for (int i = 0; i < (int)(tmpListMap.size()); i++) {
			if (!sp.contains(tmpListMap.get((int)(i)))) {
				sp.edit().putString(tmpListMap.get((int)(i)), settingsDefault.get(tmpListMap.get((int)(i))).toString()).commit();
			}
		}
		developerMode.setChecked(_loadSettingCheckBox(sp.getString("developerMode", "")));
		settingsDefault.clear();
	}
	
	
	public boolean _loadSettingCheckBox(final String _name) {
		return (sp.getString(_name, "").trim().toLowerCase().equals("true"));
	}
	
	
	public void _setSettingCheckBox(final boolean _value, final String _name) {
		v.vibrate((long)(30));
		sp.edit().putString(_name, String.valueOf(_value)).commit();
	}
	
}