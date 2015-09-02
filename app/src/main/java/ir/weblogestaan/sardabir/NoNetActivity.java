package ir.weblogestaan.sardabir;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class NoNetActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

		setContentView(R.layout.activity_nonet);
		//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		TextView tHelp = (TextView) findViewById(R.id.txtUp);
		TextView tRefresh = (TextView) findViewById(R.id.txtRefresh);
		TextView tWifi = (TextView) findViewById(R.id.txtWifi);
		TextView tData = (TextView) findViewById(R.id.txtData);
		tHelp.setText(R.string.no_internet);
		tRefresh.setText(R.string.str_refresh);
		tWifi.setText(R.string.str_wifi);
		tData.setText(R.string.str_cell_data);
		if(Locale.getDefault().getLanguage().equals("fa"))
		{
			Typeface type = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
			tHelp.setTypeface(type);
			tRefresh.setTypeface(type);
			tWifi.setTypeface(type);
			tData.setTypeface(type);
		}
		
		ImageView imgWifi = (ImageView)findViewById(R.id.imgWifi);
		imgWifi.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				arg0.getContext().startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
			}
			
		});

		ImageView imgData = (ImageView)findViewById(R.id.imgData);
		imgData.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(setMobileDataEnabled(arg0.getContext(), true))
					Toast.makeText(arg0.getContext(), R.string.str_mobile_is_active, Toast.LENGTH_LONG).show();
			}
			
		});	
		
		ImageView imgRefresh = (ImageView)findViewById(R.id.imgRefresh);
		imgRefresh.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
				Intent intent = new Intent(arg0.getContext(),MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.getContext().startActivity(intent);
				
			}
			
		});		
	}
	private boolean setMobileDataEnabled(Context context, boolean enabled) {
		try
		{
		    final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    final Class conmanClass = Class.forName(conman.getClass().getName());
		    final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
		    connectivityManagerField.setAccessible(true);
		    final Object connectivityManager = connectivityManagerField.get(conman);
		    final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
		    final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		    setMobileDataEnabledMethod.setAccessible(true);
	
		    setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
		    return true;
		}
		catch(Exception ec)
		{
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);				
			context.startActivity(intent);
			return false;
		}
	}
	
}
