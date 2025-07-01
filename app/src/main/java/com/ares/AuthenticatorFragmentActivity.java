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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.regex.*;
import org.json.*;

public class AuthenticatorFragmentActivity extends Fragment {
	
	private ArrayList<HashMap<String, Object>> accounts = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear3;
	private SwipeRefreshLayout swiperefreshlayout1;
	private TextView textview1;
	private ImageView imageview1;
	private LinearLayout linear4;
	private ListView listview1;
	private LinearLayout noAccView;
	private TextView textview2;
	private TextView textview3;
	
	private SharedPreferences sp;
	private AlertDialog.Builder d;
	private Intent i = new Intent();
	private Vibrator v;
	
	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.authenticator_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		linear1 = _view.findViewById(R.id.linear1);
		linear3 = _view.findViewById(R.id.linear3);
		swiperefreshlayout1 = _view.findViewById(R.id.swiperefreshlayout1);
		textview1 = _view.findViewById(R.id.textview1);
		imageview1 = _view.findViewById(R.id.imageview1);
		linear4 = _view.findViewById(R.id.linear4);
		listview1 = _view.findViewById(R.id.listview1);
		noAccView = _view.findViewById(R.id.noAccView);
		textview2 = _view.findViewById(R.id.textview2);
		textview3 = _view.findViewById(R.id.textview3);
		sp = getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);
		d = new AlertDialog.Builder(getActivity());
		v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		swiperefreshlayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				_loadAccs();
				swiperefreshlayout1.setRefreshing(false);
			}
		});
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setClass(getContext().getApplicationContext(), AddaccActivity.class);
				startActivity(i);
			}
		});
		
		listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				try{
					v.vibrate((long)(30));
					d.setTitle("delete account?");
					d.setMessage("after this you will need to reconnect your phone with the account to be able to use it again");
					d.setPositiveButton("delete", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							accounts.remove((int)(_position));
							sp.edit().putString("accounts", new Gson().toJson(accounts)).commit();
							SketchwareUtil.showMessage(getContext().getApplicationContext(), "success");
							_loadAccs();
						}
					});
					d.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							
						}
					});
					d.create().show();
				}catch(Exception e){
					SketchwareUtil.showMessage(getContext().getApplicationContext(), "couldn't delete this account");
				}
				return true;
			}
		});
		
		textview3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				i.setAction(Intent.ACTION_VIEW);
				i.setClass(getContext().getApplicationContext(), AddaccActivity.class);
				startActivity(i);
			}
		});
	}
	
	private void initializeLogic() {
		_loadAccs();
	}
	
	public void _loadAccs() {
		accounts.clear();
		try{
			if (sp.contains("accounts")) {
				accounts = new Gson().fromJson(sp.getString("accounts", ""), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
			}
			else {
				accounts.clear();
				sp.edit().putString("accounts", new Gson().toJson(accounts)).commit();
			}
			if (accounts.size() > 0) {
				noAccView.setVisibility(View.GONE);
				listview1.setVisibility(View.VISIBLE);
			}
			else {
				noAccView.setVisibility(View.VISIBLE);
				listview1.setVisibility(View.GONE);
			}
			listview1.setAdapter(new Listview1Adapter(accounts));
			((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
		}catch(Exception e){
			SketchwareUtil.showMessage(getContext().getApplicationContext(), "could not retrieve stored accounts");
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
				_view = _inflater.inflate(R.layout.accountview, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView imageview1 = _view.findViewById(R.id.imageview1);
			final TextView textview1 = _view.findViewById(R.id.textview1);
			final TextView textview2 = _view.findViewById(R.id.textview2);
			
			try{
				textview1.setText(_data.get((int)_position).get("username").toString());
				textview2.setText(_data.get((int)_position).get("type").toString());
			}catch(Exception e){
				SketchwareUtil.showMessage(getContext().getApplicationContext(), "could not display stored accounts, try cleaning app data");
			}
			
			return _view;
		}
	}
}