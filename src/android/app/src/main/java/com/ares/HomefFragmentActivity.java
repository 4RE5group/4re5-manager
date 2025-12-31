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
import androidx.core.content.FileProvider;
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
	private double j = 0;
	
	private ArrayList<HashMap<String, Object>> appsList = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> tmpListMap = new ArrayList<>();
	
	private CoordinatorLayout linear2;
	private SwipeRefreshLayout swiperefreshlayout1;
	private LinearLayout sheetlayout;
	private LinearLayout linear10;
	private TextView textview1;
	private GridView gridview1;
	private LinearLayout indicator;
	private LinearLayout linear3;
	private LinearLayout linear6;
	private ImageView appicon;
	private LinearLayout linear5;
	private TextView appname;
	private TextView appdesc;
	private LinearLayout linear9;
	private LinearLayout linear11;
	private TextView installbtn;
	private TextView uninstallbtn;
	private TextView appkeywords;
	private TextView appversion;
	private LinearLayout linear13;
	private LinearLayout linear12;
	private TextView textview3;
	private TextView author;
	private LinearLayout linear15;
	private ImageView github;
	private TextView textview4;
	private TextView liscence;
	
	private Intent i = new Intent();
	private RequestNetwork repoDownload;
	private RequestNetwork.RequestListener _repoDownload_request_listener;
	private TimerTask t;
	private Vibrator v;
	private Intent intent = new Intent();
	
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
		linear10 = _view.findViewById(R.id.linear10);
		textview1 = _view.findViewById(R.id.textview1);
		gridview1 = _view.findViewById(R.id.gridview1);
		indicator = _view.findViewById(R.id.indicator);
		linear3 = _view.findViewById(R.id.linear3);
		linear6 = _view.findViewById(R.id.linear6);
		appicon = _view.findViewById(R.id.appicon);
		linear5 = _view.findViewById(R.id.linear5);
		appname = _view.findViewById(R.id.appname);
		appdesc = _view.findViewById(R.id.appdesc);
		linear9 = _view.findViewById(R.id.linear9);
		linear11 = _view.findViewById(R.id.linear11);
		installbtn = _view.findViewById(R.id.installbtn);
		uninstallbtn = _view.findViewById(R.id.uninstallbtn);
		appkeywords = _view.findViewById(R.id.appkeywords);
		appversion = _view.findViewById(R.id.appversion);
		linear13 = _view.findViewById(R.id.linear13);
		linear12 = _view.findViewById(R.id.linear12);
		textview3 = _view.findViewById(R.id.textview3);
		author = _view.findViewById(R.id.author);
		linear15 = _view.findViewById(R.id.linear15);
		github = _view.findViewById(R.id.github);
		textview4 = _view.findViewById(R.id.textview4);
		liscence = _view.findViewById(R.id.liscence);
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
						 
					}catch(Exception e){
						SketchwareUtil.showMessage(getContext().getApplicationContext(), "error in json repository");
					}
					tmpListMap = new Gson().fromJson(_json_parser(appsList.get((int)currentpos).get("platform").toString()), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
					for (int i = 0; i < (int)(tmpListMap.size()); i++) {
						if (tmpListMap.get((int)i).get("platform").toString().toLowerCase().contains("android")) {
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
									Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(appsList.get((int)currentpos).get("package").toString());
									if (launchIntent != null) {
										    startActivity(launchIntent);
									} else {
										    // Handle the case where the app is not installed
										    SketchwareUtil.showMessage(getContext().getApplicationContext(), "an error occurred while updating package");
									}
								}
								else {
									if (installbtn.getText().toString().contains("buy")) {
										intent.setAction(Intent.ACTION_VIEW);
										intent.setData(Uri.parse("https://4re5group.github.io/products.html"));
										startActivity(intent);
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
							}
							platformAndroid.clear();
						}
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
		
		author.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://github.com/".concat(appsList.get((int)currentpos).get("author").toString().replace(" ", ""))));
				startActivity(i);
			}
		});
		
		github.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse(appsList.get((int)currentpos).get("github").toString()));
				startActivity(i);
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
				}catch(Exception e){
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "could not download repositiory");
				}
				j = 0;
				while(j < appsList.size()) {
					if (appsList.get((int)j).containsKey("disabled")) {
						if (appsList.get((int)j).get("disabled").toString().equals("true")) {
							appsList.remove((int)(j));
						}
						else {
							j++;
						}
					}
					else {
						j++;
					}
				}
				gridview1.setAdapter(new Gridview1Adapter(appsList));
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
		gridview1.setNumColumns((int)3);
		gridview1.setColumnWidth((int)100);
		gridview1.setHorizontalSpacing((int)20);
		gridview1.setVerticalSpacing((int)10);
		bottomSheetBehavior = BottomSheetBehavior.from(sheetlayout); bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		sheetlayout.setVisibility(View.INVISIBLE);
		uninstallbtn.setVisibility(View.INVISIBLE);
		installbtn.setVisibility(View.INVISIBLE);
		_LayoutDesigner(sheetlayout, 40, 0, 0, 40, "#212121", 0, "#000000", 8);
		_LayoutDesigner(author, 40, 40, 40, 40, "#ff8500", 0, "#000000", 8);
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
			}catch(Exception e){
				SketchwareUtil.showMessage(getContext().getApplicationContext(), "error in json repo");
			}
			j = 0;
			while(j < appsList.size()) {
				if (appsList.get((int)j).containsKey("disabled")) {
					if (appsList.get((int)j).get("disabled").toString().equals("true")) {
						appsList.remove((int)(j));
					}
					else {
						j++;
					}
				}
				else {
					j++;
				}
			}
			gridview1.setAdapter(new Gridview1Adapter(appsList));
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
		/*
if (FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/".concat(_path.concat("/".concat(_filename)))))) {
FileUtil.deleteFile(FileUtil.getExternalStorageDir().concat("/".concat(_path.concat("/".concat(_filename)))));
}
else {

}
*/
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
		liscence.setText(_map.get((int)_pos).get("license").toString());
		author.setText(" @".replace(" ", "").concat(_map.get((int)_pos).get("author").toString()));
		appicon.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_load_image(_map.get((int)_pos).get("image").toString()), 1024, 1024));
		platform = new HashMap<>();
		platform.clear();
		installbtn.setVisibility(View.GONE);
		uninstallbtn.setVisibility(View.GONE);
		appversion.setText("not available for android yet");
		try{
			tmpListMap = new Gson().fromJson(_json_parser(_map.get((int)_pos).get("platform").toString()), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
		}catch(Exception e){
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "invalid platform json");
			((ClipboardManager) getContext().getSystemService(getContext().getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", _map.get((int)_pos).get("platform").toString()));
			((ClipboardManager) getContext().getSystemService(getContext().getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", _json_parser(_map.get((int)_pos).get("platform").toString())));
		}
		for (int i = 0; i < (int)(tmpListMap.size()); i++) {
			if (tmpListMap.get((int)i).get("platform").toString().toLowerCase().contains("android")) {
				installbtn.setVisibility(View.VISIBLE);
				uninstallbtn.setVisibility(View.VISIBLE);
				appversion.setText("version: ".concat(tmpListMap.get((int)i).get("version").toString()));
				if (_getPackageVersion(_map.get((int)_pos).get("package").toString()).equals("none")) {
					installbtn.setText("install");
					_LayoutDesigner(installbtn, 16, 16, 16, 16, "#E53935", 0, "#000000", 8);
					if (_map.get((int)_pos).get("price").toString().contains("€") || _map.get((int)_pos).get("price").toString().contains("$")) {
						_LayoutDesigner(installbtn, 16, 16, 16, 16, "#FF8500", 0, "#000000", 8);
						installbtn.setText("buy at ".concat(_map.get((int)_pos).get("price").toString()));
					}
					installbtn.setVisibility(View.VISIBLE);
					uninstallbtn.setVisibility(View.GONE);
				}
				else {
					if ((_getVersionInt(_getPackageVersion(_map.get((int)_pos).get("package").toString())) == _getVersionInt(tmpListMap.get((int)i).get("version").toString())) || (_getVersionInt(_getPackageVersion(_map.get((int)_pos).get("package").toString())) > _getVersionInt(tmpListMap.get((int)i).get("version").toString()))) {
						installbtn.setText("launch");
					}
					else {
						installbtn.setText("update");
					}
				}
			}
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
		try {
			            // Remove outer brackets and split into individual objects
			            String trimmedInput = _inp.substring(1, _inp.length() - 1);
			            String[] objects = trimmedInput.split("\\},\\s*\\{");
			
			            JSONArray jsonArray = new JSONArray();
			
			            for (String objStr : objects) {
				                // Clean up the object string
				                objStr = objStr.replaceAll("[{}]", "").trim();
				                String[] keyValuePairs = objStr.split("\\s*,\\s*");
				
				                JSONObject jsonObject = new JSONObject();
				
				                for (String pair : keyValuePairs) {
					                    String[] keyValue = pair.split("\\s*=\\s*", 2);
					                    if (keyValue.length != 2) continue;
					
					                    String key = keyValue[0].trim();
					                    String value = keyValue[1].trim();
					
					                    // Handle different types of values
					                    if (value.startsWith("[") && value.endsWith("]")) {
						                        // Handle arrays
						                        String arrayContent = value.substring(1, value.length() - 1);
						                        JSONArray array = new JSONArray();
						                        if (!arrayContent.isEmpty()) {
							                            for (String item : arrayContent.split("\\s*,\\s*")) {
								                                array.put(item.trim());
								                            }
							                        }
						                        jsonObject.put(key, array);
						                    } else if (value.equals("true") || value.equals("false")) {
						                        // Handle booleans
						                        jsonObject.put(key, Boolean.parseBoolean(value));
						                    } else if (value.matches("\\d+(\\.\\d+)?")) {
						                        // Handle numbers (integers or decimals)
						                        if (value.contains(".")) {
							                            jsonObject.put(key, Double.parseDouble(value));
							                        } else {
							                            jsonObject.put(key, Integer.parseInt(value));
							                        }
						                    } else if (value.isEmpty()) {
						                        // Handle empty values (e.g., empty strings or null)
						                        jsonObject.put(key, "");
						                    } else {
						                        // Handle strings (including URLs and versions)
						                        jsonObject.put(key, value);
						                    }
					                }
				
				                jsonArray.put(jsonObject);
				            }
			
			            return jsonArray.toString(2); // Pretty-print with 2-space indentation
			        } catch (Exception e) {
			            System.err.println("Error converting to JSON: " + e.getMessage());
			            return "{}";
			        }
		
		/*
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
*/
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
			Context mContext = getContext();
			File outputFile = null;
			//try {
				
				outputFile = new File(_apk);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						Uri apkUri = FileProvider.getUriForFile(mContext, "com.ares.provider", outputFile);
						Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
						intent.setData(apkUri);
						intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						mContext.startActivity(intent);
				} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
						Uri apkUri = Uri.fromFile(outputFile);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(intent);
				}else {
						Toast.makeText(mContext, "File not found.", Toast.LENGTH_LONG).show();
				}
			//} catch (Exception e) {
			//    Toast.makeText(mContext, "Could not install app.", Toast.LENGTH_LONG).show();
			//}
			
			/*
i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
i.setAction(Intent.ACTION_VIEW);
i.setData(Uri.parse(_apk));
i.setType("application/vnd.android.package-archive");
startActivity(i);
*/
		}
		else {
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "apk file not found");
		}
	}
	
	
	public double _getVersionInt(final String _version) {
		return (Double.parseDouble(_version.replace(" ", "").replaceFirst(".", "#").replace(".", "").replace("#", ".").replace("beta", "").replace("alpha", "").toLowerCase()));
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