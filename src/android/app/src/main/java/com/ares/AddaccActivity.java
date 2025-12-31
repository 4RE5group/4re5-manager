package com.ares;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.budiyev.android.codescanner.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.*;
import java.io.*;
import java.io.File;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class AddaccActivity extends AppCompatActivity {
	
	public final int REQ_CD_C = 101;
	
	private HashMap<String, Object> map = new HashMap<>();
	private String username = "";
	private String url = "";
	private String token = "";
	private String type = "";
	private String atChar = "";
	
	private ArrayList<HashMap<String, Object>> accounts = new ArrayList<>();
	private ArrayList<String> tmp = new ArrayList<>();
	
	private LinearLayout linear2;
	private LinearLayout linear4;
	private LinearLayout accFoundView;
	private CodeScannerView linear1;
	private ImageView imageview1;
	private TextView textview1;
	private TextView textview2;
	private TextView accountName;
	private EditText edittext1;
	private LinearLayout linear3;
	private Button button1;
	private Button button2;
	
	private AlertDialog.Builder d;
	private Intent c = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	private File _file_c;
	private CodeScanner Scan;
	private SharedPreferences sp;
	private Vibrator v;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.addacc);
		initialize(_savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
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
		linear2 = findViewById(R.id.linear2);
		linear4 = findViewById(R.id.linear4);
		accFoundView = findViewById(R.id.accFoundView);
		linear1 = findViewById(R.id.linear1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		textview2 = findViewById(R.id.textview2);
		accountName = findViewById(R.id.accountName);
		edittext1 = findViewById(R.id.edittext1);
		linear3 = findViewById(R.id.linear3);
		button1 = findViewById(R.id.button1);
		button2 = findViewById(R.id.button2);
		d = new AlertDialog.Builder(this);
		_file_c = FileUtil.createNewPictureFile(getApplicationContext());
		Uri _uri_c;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			_uri_c = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_c);
		} else {
			_uri_c = Uri.fromFile(_file_c);
		}
		c.putExtra(MediaStore.EXTRA_OUTPUT, _uri_c);
		c.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		sp = getSharedPreferences("data", Activity.MODE_PRIVATE);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				finish();
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				accounts = new Gson().fromJson(sp.getString("accounts", ""), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
				map = new HashMap<>();
				map.put("username", EcryptingTheTextMethod(edittext1.getText().toString(),sp.getString("pattern", "")));
				map.put("token", EcryptingTheTextMethod(token,sp.getString("pattern", "")));
				map.put("url", EcryptingTheTextMethod(url,sp.getString("pattern", "")));
				map.put("type", EcryptingTheTextMethod(type,sp.getString("pattern", "")));
				accounts.add(map);
				SketchwareUtil.showMessage(getApplicationContext(), "added account successfully!");
				sp.edit().putString("accounts", new Gson().toJson(accounts)).commit();
				finish();
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				Scan.startPreview(); 
				accFoundView.setVisibility(View.GONE);
				accountName.setText("");
			}
		});
	}
	
	private void initializeLogic() {
		accFoundView.setVisibility(View.GONE);
		CodeScannerView scannerView = findViewById(R.id.linear1);
		 Scan = new CodeScanner(this, scannerView);
		
		Scan.setDecodeCallback(new DecodeCallback() {
			  @Override public void onDecoded(@NonNull final Result result) { runOnUiThread(new Runnable() {
					         @Override
					          public void run() { 
						          _data = result.getText();
						           QRCodeScan();
						          }
					   });
			}});
		Scan.startPreview(); 
		//download QRCode Component, library and block  link👇https://www.mediafire.com/file/gi49s0axnvgryjs/QRCODE.zip/file
	}
	
	
	private String _data ="";
	private void QRCodeScan(){
		   /*
 @ is always after a space bug
*/
		atChar = " @".replace(" ", "");
		try{
			if (_data.contains(atChar)) {
				v.vibrate((long)(30));
				tmp = new ArrayList<String>(Arrays.asList(_data.split(atChar)));
				type = tmp.get((int)(0));
				url = tmp.get((int)(1));
				token = tmp.get((int)(2));
				accFoundView.setVisibility(View.VISIBLE);
				accountName.setText("account found for '".concat(tmp.get((int)(0)).concat("'")));
			}
			else {
				SketchwareUtil.showMessage(getApplicationContext(), "wrong qrcode. make sure it's from a 4re5 product");
			}
		}catch(Exception e){
			SketchwareUtil.showMessage(getApplicationContext(), "wrong qrcode. make sure it's from a 4re5 product");
			Scan.startPreview(); 
		}
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