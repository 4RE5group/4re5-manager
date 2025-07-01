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
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnAdapterChangeListener;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.budiyev.android.codescanner.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.zxing.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

public class MainviewActivity extends AppCompatActivity {
	
	private LinearLayout linear1;
	private BottomNavigationView bottomnavigation1;
	private LinearLayout linear2;
	private ViewPager viewpager1;
	private TextView textview1;
	
	private FragFragmentAdapter frag;
	private Intent i = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.mainview);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		bottomnavigation1 = findViewById(R.id.bottomnavigation1);
		linear2 = findViewById(R.id.linear2);
		viewpager1 = findViewById(R.id.viewpager1);
		textview1 = findViewById(R.id.textview1);
		frag = new FragFragmentAdapter(getApplicationContext(), getSupportFragmentManager());
		
		bottomnavigation1.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem item) {
				final int _itemId = item.getItemId();
				viewpager1.setCurrentItem((int)_itemId);
				return true;
			}
		});
	}
	
	private void initializeLogic() {
		overridePendingTransition(0, 0);
		try{
			/*
bug here fragmentAdapter is tabcount
*/
			frag.setTabCount(5);
			viewpager1.setAdapter(frag);
			bottomnavigation1.getMenu().add(0, 0, 0, "Mods").setIcon(R.drawable.ic_extension_white);
			bottomnavigation1.getMenu().add(0, 1, 0, "4re5net").setIcon(R.drawable.default_image);
			bottomnavigation1.getMenu().add(0, 2, 0, "Home").setIcon(R.drawable.ic_home_white);
			bottomnavigation1.getMenu().add(0, 3, 0, "Authenticator").setIcon(R.drawable.ic_extension_white);
			bottomnavigation1.getMenu().add(0, 4, 0, "Settings").setIcon(R.drawable.ic_settings_white);
			viewpager1.setCurrentItem((int)2);
			((PagerAdapter)viewpager1.getAdapter()).notifyDataSetChanged();
		}catch(Exception e){
			SketchwareUtil.showMessage(getApplicationContext(), "Error loading UI");
			finishAffinity();
		}
		bottomnavigation1.setItemRippleColor(ColorStateList.valueOf(0xFF616161));
		bottomnavigation1.setItemIconTintList(ColorStateList.valueOf(0xFFFFFFFF));
		bottomnavigation1.setItemTextColor(ColorStateList.valueOf(0xFFFFFFFF));
		textview1.setText("4re5 manager - v".concat(_getPackageVersion("com.ares")));
	}
	
	public class FragFragmentAdapter extends FragmentStatePagerAdapter {
		// This class is deprecated, you should migrate to ViewPager2:
		// https://developer.android.com/reference/androidx/viewpager2/widget/ViewPager2
		Context context;
		int tabCount;
		
		public FragFragmentAdapter(Context context, FragmentManager manager) {
			super(manager);
			this.context = context;
		}
		
		public void setTabCount(int tabCount) {
			this.tabCount = tabCount;
		}
		
		@Override
		public int getCount() {
			return tabCount;
		}
		
		@Override
		public CharSequence getPageTitle(int _position) {
			
			return null;
		}
		
		@Override
		public Fragment getItem(int _position) {
			switch((int)_position) {
				case ((int)0): {
						return new ModsFragmentActivity();
				}
				case ((int)1): {
						return new AresnetmainFragmentActivity();
				}
				case ((int)2): {
						return new HomefFragmentActivity();
				}
				case ((int)3): {
						return new AuthenticatorFragmentActivity();
				}
				case ((int)4): {
						return new SettingsFragmentActivity();
				}
				
			}
			return null;
		}
	}
	
	public String _getPackageVersion(final String _name) {
		try {
			    PackageInfo pInfo = getPackageManager().getPackageInfo(_name, 0);
			    String appVersion = pInfo.versionName;
			    return appVersion;
		} catch (PackageManager.NameNotFoundException e) {
			    e.printStackTrace();
			    return "none";
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