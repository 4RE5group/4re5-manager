package com.ares;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
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
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
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
import java.util.regex.*;
import org.json.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

public class ModsFragmentActivity extends Fragment {
	
	private String working_dir = "";
	private boolean downloading = false;
	
	private ArrayList<HashMap<String, Object>> appsList = new ArrayList<>();
	
	private LinearLayout linear2;
	private LinearLayout linear3;
	private TextView textview2;
	private GridView gridview1;
	private TextView textview1;
	private ListView listview1;
	private EditText edittext1;
	private ImageView imageview1;
	
	private RequestNetwork repoDownload;
	private RequestNetwork.RequestListener _repoDownload_request_listener;
	private Intent i = new Intent();
	private SharedPreferences sp;
	
	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.mods_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		linear2 = _view.findViewById(R.id.linear2);
		linear3 = _view.findViewById(R.id.linear3);
		textview2 = _view.findViewById(R.id.textview2);
		gridview1 = _view.findViewById(R.id.gridview1);
		textview1 = _view.findViewById(R.id.textview1);
		listview1 = _view.findViewById(R.id.listview1);
		edittext1 = _view.findViewById(R.id.edittext1);
		imageview1 = _view.findViewById(R.id.imageview1);
		repoDownload = new RequestNetwork((Activity) getContext());
		sp = getContext().getSharedPreferences("sharedData", Activity.MODE_PRIVATE);
		
		listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				sp.edit().putString("sharedModMap", new Gson().toJson(appsList.get((int)(_position)))).commit();
				i.setAction(Intent.ACTION_VIEW);
				i.setClass(getContext().getApplicationContext(), ModmanagerActivity.class);
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
					FileUtil.writeFile(working_dir.concat("mods/mods.json"), _response);
					appsList = new Gson().fromJson(_response, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
					gridview1.setAdapter(new Gridview1Adapter(appsList));
					listview1.setAdapter(new Listview1Adapter(appsList));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}catch(Exception e){
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "error in mods list's json");
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
		FileUtil.makeDir(working_dir.concat("mods/"));
		_fetch_repo(false);
	}
	
	public void _fetch_repo(final boolean _forceUpdate) {
		appsList.clear();
		if (!FileUtil.isExistFile(working_dir.concat("mods/mods.json")) || _forceUpdate) {
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "updating mods list...");
			repoDownload.startRequestNetwork(RequestNetworkController.GET, "https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/mods/mods.json", "", _repoDownload_request_listener);
		}
		else {
			try{
				appsList = new Gson().fromJson(FileUtil.readFile(working_dir.concat("mods/mods.json")), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
				listview1.setAdapter(new Listview1Adapter(appsList));
				gridview1.setAdapter(new Gridview1Adapter(appsList));
				((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
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
}
*/
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
			
			if (_data.get((int)_position).containsKey("trending")) {
				textview1.setText(_data.get((int)_position).get("name").toString());
				imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_load_image(_data.get((int)_position).get("image").toString()), 1024, 1024));
			}
			else {
				linear1.setVisibility(View.GONE);
			}
			
			return _view;
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
			LayoutInflater _inflater = getActivity().getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.modlistview, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView appicon = _view.findViewById(R.id.appicon);
			final LinearLayout linear2 = _view.findViewById(R.id.linear2);
			final TextView modname = _view.findViewById(R.id.modname);
			final TextView moddesc = _view.findViewById(R.id.moddesc);
			
			modname.setText(_data.get((int)_position).get("name").toString());
			moddesc.setText(_data.get((int)_position).get("description").toString());
			appicon.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_load_image(_data.get((int)_position).get("image").toString()), 1024, 1024));
			
			return _view;
		}
	}
}