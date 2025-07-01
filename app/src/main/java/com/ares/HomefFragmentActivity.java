package com.ares;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

public class HomefFragmentActivity extends Fragment {
	
	private Timer _timer = new Timer();
	
	private String working_dir = "";
	private boolean downloading = false;
	private double currentpos = 0;
	private HashMap<String, Object> platform = new HashMap<>();
	private String tmp = "";
	private  BottomSheetBehavior bottomSheetBehavior;
	private HashMap<String, Object> platformAndroid = new HashMap<>();
	
	private ArrayList<HashMap<String, Object>> appsList = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> tmpListMap = new ArrayList<>();
	
	private CoordinatorLayout linear2;
	private SwipeRefreshLayout swiperefreshlayout1;
	private LinearLayout sheetlayout;
	private GridView gridview1;
	private LinearLayout indicator;
	private LinearLayout linear3;
	private LinearLayout linear6;
	private ImageView appicon;
	private LinearLayout linear5;
	private TextView appname;
	private TextView appdesc;
	private LinearLayout linear9;
	private TextView installbtn;
	private TextView uninstallbtn;
	private TextView appkeywords;
	private TextView appversion;
	
	private Intent i = new Intent();
	private RequestNetwork repoDownload;
	private RequestNetwork.RequestListener _repoDownload_request_listener;
	private TimerTask t;
	private Vibrator v;
	
	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.homef_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		linear2 = _view.findViewById(R.id.linear2);
		swiperefreshlayout1 = _view.findViewById(R.id.swiperefreshlayout1);
		sheetlayout = _view.findViewById(R.id.sheetlayout);
		gridview1 = _view.findViewById(R.id.gridview1);
		indicator = _view.findViewById(R.id.indicator);
		linear3 = _view.findViewById(R.id.linear3);
		linear6 = _view.findViewById(R.id.linear6);
		appicon = _view.findViewById(R.id.appicon);
		linear5 = _view.findViewById(R.id.linear5);
		appname = _view.findViewById(R.id.appname);
		appdesc = _view.findViewById(R.id.appdesc);
		linear9 = _view.findViewById(R.id.linear9);
		installbtn = _view.findViewById(R.id.installbtn);
		uninstallbtn = _view.findViewById(R.id.uninstallbtn);
		appkeywords = _view.findViewById(R.id.appkeywords);
		appversion = _view.findViewById(R.id.appversion);
		repoDownload = new RequestNetwork((Activity) getContext());
		v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		swiperefreshlayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				try{
					v.vibrate((long)(30));
					_fetch_repo(true);
					swiperefreshlayout1.setRefreshing(false);
				}catch(Exception e){
					 
				}
			}
		});
		
		installbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (installbtn.getText().toString().contains("available")) {
					
				}
				else {
					tmpListMap.clear();
					try{
						tmpListMap = new Gson().fromJson(_json_parser(appsList.get((int)currentpos).get("platform").toString()), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
						for (int i = 0; i < (int)(tmpListMap.size()); i++) {
							if (tmpListMap.get((int)i).get("name").toString().contains("Android")) {
								platformAndroid = new HashMap<>();
								platformAndroid = tmpListMap.get((int)i);
								if (installbtn.getText().toString().equals("install")) {
									_download_file(platformAndroid.get("url").toString(), "/4re5 group/tmp/", appsList.get((int)currentpos).get("name").toString().concat(".apk"));
									_Install(working_dir.concat("tmp/".concat(appsList.get((int)currentpos).get("name").toString().concat(".apk"))));
									if (!_getPackageVersion(appsList.get((int)currentpos).get("package").toString()).equals("none")) {
										installbtn.setText("launch");
										uninstallbtn.setVisibility(View.VISIBLE);
									}
								}
								else {
									if (installbtn.getText().toString().equals("launch")) {
										/*
try{
Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appsList.get((int)currentpos).get("package").toString());  { startActivity(launchIntent);}
}catch(Exception e){
SketchwareUtil.showMessage(getContext().getApplicationContext(), "an error occurred while launching package");
}
*/
									}
									else {
										SketchwareUtil.showMessage(getContext().getApplicationContext(), "Updating...");
										try{
											_download_file(platformAndroid.get("url").toString(), "/4re5 group/tmp/", appsList.get((int)currentpos).get("name").toString().concat(".apk"));
											_Install(working_dir.concat("tmp/".concat(appsList.get((int)currentpos).get("name").toString().concat(".apk"))));
										}catch(Exception e){
											SketchwareUtil.showMessage(getContext().getApplicationContext(), "an error occurred while updating package");
										}
									}
								}
								platformAndroid.clear();
							}
						}
					}catch(Exception e){
						SketchwareUtil.showMessage(getContext().getApplicationContext(), "error in json repository");
					}
				}
			}
		});
		
		uninstallbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				try{
					_uninstallPackage(appsList.get((int)currentpos).get("package").toString());
				}catch(Exception e){
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "could not find package to uninstall ");
				}
			}
		});
		
		_repoDownload_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				try{
					FileUtil.writeFile(working_dir.concat("repo.json"), _response);
					appsList = new Gson().fromJson(_response, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
					gridview1.setAdapter(new Gridview1Adapter(appsList));
				}catch(Exception e){
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "could not download repositiory");
				}
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				SketchwareUtil.showMessage(getContext().getApplicationContext(), _message);
			}
		};
	}
	
	private void initializeLogic() {
		working_dir = FileUtil.getExternalStorageDir().concat("/4re5 group/");
		FileUtil.makeDir(working_dir);
		downloading = false;
		gridview1.setAdapter(new Gridview1Adapter(appsList));
		gridview1.setNumColumns((int)4);
		gridview1.setColumnWidth((int)100);
		gridview1.setHorizontalSpacing((int)20);
		gridview1.setVerticalSpacing((int)10);
		bottomSheetBehavior = BottomSheetBehavior.from(sheetlayout); bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		sheetlayout.setVisibility(View.INVISIBLE);
		uninstallbtn.setVisibility(View.INVISIBLE);
		installbtn.setVisibility(View.INVISIBLE);
		_LayoutDesigner(sheetlayout, 40, 0, 0, 40, "#212121", 0, "#000000", 8);
		_LayoutDesigner(indicator, 100, 100, 100, 100, "#E0E0E0", 0, "#000000", 0);
		_fetch_repo(false);
		_LayoutDesigner(installbtn, 16, 16, 16, 16, "#E53935", 0, "#000000", 8);
		_LayoutDesigner(uninstallbtn, 16, 16, 16, 16, "#E53935", 0, "#000000", 8);
		if (!FileUtil.isExistFile(working_dir.concat("default.png"))) {
			_download_file("https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/default.png", "/4re5 group/", "default.png");
		}
	}
	
	public String _load_image(final String _image_url) {
		if (!_image_url.trim().equals("")) {
			FileUtil.makeDir(working_dir.concat("tmp/"));
			if (!FileUtil.isExistFile(working_dir.concat("tmp/".concat(_nader_generateMD5(_image_url).concat(".png"))))) {
				SketchwareUtil.showMessage(getContext().getApplicationContext(), working_dir.concat("tmp/".concat(_nader_generateMD5(_image_url).concat(".png"))));
				_download_file(_image_url, "/4re5 group/tmp/", _nader_generateMD5(_image_url).concat(".png"));
			}
			return working_dir.concat("tmp/".concat(_nader_generateMD5(_image_url).concat(".png")));
		}
		return working_dir.concat("default.png");
	}
	
	
	public void _fetch_repo(final boolean _forceUpdate) {
		appsList.clear();
		if (!FileUtil.isExistFile(working_dir.concat("repo.json")) || _forceUpdate) {
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "updating repository...");
			repoDownload.startRequestNetwork(RequestNetworkController.GET, "https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/repo.json", "", _repoDownload_request_listener);
		}
		else {
			try{
				appsList = new Gson().fromJson(FileUtil.readFile(working_dir.concat("repo.json")), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
				gridview1.setAdapter(new Gridview1Adapter(appsList));
			}catch(Exception e){
				SketchwareUtil.showMessage(getContext().getApplicationContext(), "error in json repo");
			}
		}
	}
	
	
	public String _nader_generateMD5(final String _input) {
		
		  try { 
			
			            // Static getInstance method is called with hashing MD5 
			
			            MessageDigest md = MessageDigest.getInstance("MD5"); 
			
			  
			
			            // digest() method is called to calculate message digest 
			
			            //  of an input digest() return array of byte 
			
			            byte[] messageDigest = md.digest(_input.getBytes()); 
			
			  
			
			            // Convert byte array into signum representation 
			
			            BigInteger no = new BigInteger(1, messageDigest); 
			
			  
			
			            // Convert message digest into hex value 
			
			            String hashtext = no.toString(16); 
			
			            while (hashtext.length() < 32) { 
				
				                hashtext = "0" + hashtext; 
			}
			           
			
			            return hashtext; 
			
			       }
		  catch (NoSuchAlgorithmException e) { 
			
			    throw new RuntimeException(e); 
		} 
		
		
	}
	
	
	public void _LayoutDesigner(final View _view, final double _cornerLT, final double _cornerLB, final double _cornerRB, final double _cornerRT, final String _color, final double _stroke, final String _strokecolor, final double _elevation) {
		android.graphics.drawable.GradientDrawable Designer = new android.graphics.drawable.GradientDrawable();
		
		Designer.setColor(Color.parseColor(_color));
		
		Designer.setStroke((int)_stroke, Color.parseColor(_strokecolor));
		
		Designer.setCornerRadii(new float[] {(float)_cornerLT, (float)_cornerLT, (float)_cornerRT, (float)_cornerRT, (float)_cornerRB, (float)_cornerRB, (float)_cornerLB, (float)_cornerLB});
		
		_view.setElevation((int)_elevation);
		
		_view.setBackground(Designer);
		
	}
	
	
	public void _download_file(final String _url, final String _path, final String _filename) {
		if (FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/".concat(_path.concat("/".concat(_filename)))))) {
			FileUtil.deleteFile(FileUtil.getExternalStorageDir().concat("/".concat(_path.concat("/".concat(_filename)))));
		}
		else {
			
		}
		Context context = getContext();
		if (context != null) {
			    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(_url));
			    request.setMimeType(context.getContentResolver().getType(Uri.parse(_url)));
			    String cookies = CookieManager.getInstance().getCookie(_url);
			    request.addRequestHeader("cookie", cookies);
			    // request.addRequestHeader("User-Agent", tab.getSettings().getUserAgentString());
			    request.setDestinationInExternalPublicDir(_path, _filename);
			
			    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			    dm.enqueue(request);
		} else {
			    // Handle the case where the context is null
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "an error occured while downloading file");
		}
		/*
try{
DownloadManager.Request request = new DownloadManager.Request(Uri.parse( _url)); 
request.setMimeType(This.getContentResolver().getType(Uri.parse(_url))); 
String cookies = CookieManager.getInstance().getCookie(_url); 
request.addRequestHeader("cookie", cookies); 
//request.addRequestHeader("User-Agent", tab.getSettings().getUserAgentString());
request.setDestinationInExternalPublicDir(_path, _filename); 
DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE); dm.enqueue(request);
}catch(Exception e){
SketchwareUtil.showMessage(getContext().getApplicationContext(), "an error occured while downloading file");
return;
}
*/
		while(!FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/".concat(_path.concat("/".concat(_filename)))))) {
			 
		}
	}
	
	
	public void _viewapp(final double _pos, final ArrayList<HashMap<String, Object>> _map) {
		currentpos = _pos;
		appname.setText(_map.get((int)_pos).get("name").toString());
		appdesc.setText(_map.get((int)_pos).get("description").toString());
		appkeywords.setText("first release: ".concat(_map.get((int)_pos).get("first_date").toString()));
		appicon.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_load_image(_map.get((int)_pos).get("image").toString()), 1024, 1024));
		platform = new HashMap<>();
		platform.clear();
		installbtn.setVisibility(View.GONE);
		uninstallbtn.setVisibility(View.GONE);
		appversion.setText("not available for android yet");
		try{
			tmpListMap = new Gson().fromJson(_json_parser(_map.get((int)_pos).get("platform").toString()), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
			SketchwareUtil.sortListMap(tmpListMap, "platformid", false, true);
			for (int i = 0; i < (int)(tmpListMap.size()); i++) {
				if (tmpListMap.get((int)i).get("name").toString().contains("Android")) {
					installbtn.setVisibility(View.VISIBLE);
					uninstallbtn.setVisibility(View.VISIBLE);
					appversion.setText("version: ".concat(tmpListMap.get((int)i).get("version").toString()));
					if (_getPackageVersion(appsList.get((int)currentpos).get("package").toString()).equals("none")) {
						installbtn.setText("install");
						installbtn.setVisibility(View.VISIBLE);
						uninstallbtn.setVisibility(View.GONE);
					}
					else {
						if (_getPackageVersion(tmpListMap.get((int)i).get("version").toString()).equals(_getPackageVersion(appsList.get((int)currentpos).get("package").toString()))) {
							installbtn.setText("launch");
						}
						else {
							installbtn.setText("update");
						}
					}
				}
			}
		}catch(Exception e){
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "invalid platform json");
			((ClipboardManager) getContext().getSystemService(getContext().getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", _json_parser(_map.get((int)_pos).get("platform").toString())));
		}
		sheetlayout.setVisibility(View.VISIBLE);
		_bottomSheetSetState(true);
	}
	
	
	public String _getPackageVersion(final String _name) {
		try {
				Context context = getContext();
			    PackageInfo pInfo = context.getPackageManager().getPackageInfo(_name, 0);
			    String appVersion = pInfo.versionName;
			    return appVersion;
		} catch (PackageManager.NameNotFoundException e) {
			    e.printStackTrace();
			    return "none";
		}
	}
	
	
	public void _uninstallPackage(final String _name) {
		Uri packageURI = Uri.parse("package:".concat(_name)); 
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI); 
		startActivity(uninstallIntent);
	}
	
	
	public String _json_parser(final String _inp) {
		try{
			tmp = _inp.replace("={", "\": {\"").replace("=[", "\": [\"").replace("=", "\": \"").replace("}, ", "\"}, ");
			// Use regex to replace more complex patterns
			tmp = tmp.replaceAll("(\\w+)], ", "$1\"],");
			tmp = tmp.replaceAll("\\{(\\w+)", "{\"$1");
			tmp = tmp.replaceAll("(\\w+), (\\w+)", "$1\", \"$2");
			
			tmp = tmp.replaceAll("],(\\w+)", "],\"$1");
			tmp = tmp.replaceAll("], (\\w+)", "], \"$1");
			
			
			// Replace the remaining patterns
			tmp = tmp.replace("}, ", "\"}, ");
			tmp = tmp.replace("[\"]", "[]");
			tmp = tmp.replace("}}", "\"}}");
			tmp = tmp.replace("\"\"}", "\"}");
			tmp = tmp.replace("}]", "\"}]");
			return tmp;
		}catch(Exception e){
			return _inp;
		}
	}
	
	
	public void _bottomSheetSetState(final boolean _opened) {
		if (_opened) {
			if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
				    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
		}
		else {
			if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
				    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		}
	}
	
	
	public void _Install(final String _apk) {
		/*
wait for file exists 
*/
		if (FileUtil.isExistFile(_apk)) {
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			i.setAction(Intent.ACTION_VIEW);
			i.setData(Uri.parse(_apk));
			i.setType("application/vnd.android.package-archive");
			startActivity(i);
		}
		else {
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "apk file not found");
		}
	}
	
	public class Gridview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Gridview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
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
				_view = _inflater.inflate(R.layout.appview, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView imageview1 = _view.findViewById(R.id.imageview1);
			final TextView textview1 = _view.findViewById(R.id.textview1);
			
			if (_data.get((int)_position).containsKey("disabled")) {
				if (_data.get((int)_position).get("disabled").toString().equals("true")) {
					linear1.setVisibility(View.GONE);
				}
			}
			imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_load_image(_data.get((int)_position).get("image").toString()), 1024, 1024));
			textview1.setText(_data.get((int)_position).get("name").toString());
			linear1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					_bottomSheetSetState(false);
					currentpos = _position;
					_viewapp(_position, _data);
					_bottomSheetSetState(true);
				}
			});
			
			return _view;
		}
	}
}