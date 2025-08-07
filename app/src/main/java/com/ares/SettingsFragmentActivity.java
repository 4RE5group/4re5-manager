package com.ares;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
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
import com.google.gson.Gson;
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
	private ArrayList<HashMap<String, Object>> a = new ArrayList<>();
	
	private ScrollView vscroll1;
	private LinearLayout linear1;
	private TextView textview1;
	private LinearLayout linear3;
	private TextView textview3;
	private Switch developerMode;
	private LinearLayout linear4;
	private TextView textview4;
	private TextView textview6;
	private TextView patternhash;
	private Button button1;
	private LinearLayout linear5;
	private TextView textview7;
	private TextView textview8;
	private LinearLayout linear7;
	private LinearLayout linear8;
	private LinearLayout linear6;
	private LinearLayout linear9;
	private TextView textview10;
	private TextView textview11;
	private TextView textview9;
	private TextView textview12;
	private TextView textview13;
	private ImageView imageview1;
	private TextView textview14;
	private ImageView imageview2;
	
	private SharedPreferences sp;
	private Vibrator v;
	private Intent i = new Intent();
	private AlertDialog.Builder d;
	
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
		textview6 = _view.findViewById(R.id.textview6);
		patternhash = _view.findViewById(R.id.patternhash);
		button1 = _view.findViewById(R.id.button1);
		linear5 = _view.findViewById(R.id.linear5);
		textview7 = _view.findViewById(R.id.textview7);
		textview8 = _view.findViewById(R.id.textview8);
		linear7 = _view.findViewById(R.id.linear7);
		linear8 = _view.findViewById(R.id.linear8);
		linear6 = _view.findViewById(R.id.linear6);
		linear9 = _view.findViewById(R.id.linear9);
		textview10 = _view.findViewById(R.id.textview10);
		textview11 = _view.findViewById(R.id.textview11);
		textview9 = _view.findViewById(R.id.textview9);
		textview12 = _view.findViewById(R.id.textview12);
		textview13 = _view.findViewById(R.id.textview13);
		imageview1 = _view.findViewById(R.id.imageview1);
		textview14 = _view.findViewById(R.id.textview14);
		imageview2 = _view.findViewById(R.id.imageview2);
		sp = getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);
		v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		d = new AlertDialog.Builder(getActivity());
		
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
					d.setMessage("are you sure to change your pattern lock? all your accounts and connected servers will be deleted and can then be added again");
					d.setPositiveButton("change pattern", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							sp.edit().putString("accounts", new Gson().toJson(a)).commit();
							sp.edit().remove("pattern").commit();
							i.setAction(Intent.ACTION_VIEW);
							i.setClass(getContext().getApplicationContext(), LoginActivity.class);
							startActivity(i);
						}
					});
					d.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							return;
						}
					});
					d.create().show();
				}
				else {
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "pattern is not set, creating");
					i.setAction(Intent.ACTION_VIEW);
					i.setClass(getContext().getApplicationContext(), LoginActivity.class);
					startActivity(i);
				}
			}
		});
		
		linear6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://github.com/4re5group/4re5-manager"));
				startActivity(i);
			}
		});
		
		linear9.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://www.paypal.me/4re5group"));
				startActivity(i);
			}
		});
		
		textview11.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://github.com/4re5group/"));
				startActivity(i);
			}
		});
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
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
		patternhash.setText(sp.getString("pattern", ""));
		textview8.setText(textview8.getText().toString().replace("v1.0", "v".concat(_getPackageVersion("com.ares"))));
		_LayoutDesigner(textview11, 40, 40, 40, 40, "#ff8500", 0, "#000000", 8);
		settingsDefault.clear();
	}
	
	
	public boolean _loadSettingCheckBox(final String _name) {
		return (sp.getString(_name, "").trim().toLowerCase().equals("true"));
	}
	
	
	public void _setSettingCheckBox(final boolean _value, final String _name) {
		v.vibrate((long)(30));
		sp.edit().putString(_name, String.valueOf(_value)).commit();
	}
	
	
	public String _getPackageVersion(final String _name) {
		return (sp.getString("version", ""));
	}
	
	
	public void _LayoutDesigner(final View _view, final double _cornerLT, final double _cornerLB, final double _cornerRB, final double _cornerRT, final String _color, final double _stroke, final String _strokecolor, final double _elevation) {
		android.graphics.drawable.GradientDrawable Designer = new android.graphics.drawable.GradientDrawable();
		
		Designer.setColor(Color.parseColor(_color));
		
		Designer.setStroke((int)_stroke, Color.parseColor(_strokecolor));
		
		Designer.setCornerRadii(new float[] {(float)_cornerLT, (float)_cornerLT, (float)_cornerRT, (float)_cornerRT, (float)_cornerRB, (float)_cornerRB, (float)_cornerLB, (float)_cornerLB});
		
		_view.setElevation((int)_elevation);
		
		_view.setBackground(Designer);
		
	}
	
}