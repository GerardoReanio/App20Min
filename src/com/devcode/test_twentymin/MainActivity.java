package com.devcode.test_twentymin;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends ActionBarActivity {
	
	Button btn_acreditar;
	TextView DxUsuario, MsjAcreditado;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		this.btn_acreditar = (Button)findViewById(R.id.Btn_Acreditar);
		this.DxUsuario = (TextView) findViewById(R.id.DxUsuario);
		this.MsjAcreditado = (TextView) findViewById(R.id.MsjAcreditado);
		
		
		btn_acreditar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
				integrator.initiateScan();
			}
		});
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
				
		if (scanResult.getContents() != null){
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			String Email = scanResult.getContents().toString();
			params.add(new BasicNameValuePair("Email",Email));
			String url="";
			Toast.makeText(getApplicationContext(), Email, Toast.LENGTH_LONG).show();
			consultaBdx(params,url);
		}
		else{
			Toast.makeText(getApplicationContext(), "No se logró escanear el código, por favor intentar nuevamenta", Toast.LENGTH_LONG).show();
		}     
    }
	
	private void consultaBdx(List<NameValuePair> params, String url) {
		try {
			HttpPost httppost = new HttpPost(url);	
			httppost.setEntity(new UrlEncodedFormEntity(params));
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse resp;
				resp = httpclient.execute(httppost);
				
				JSONObject j=new JSONObject(EntityUtils.toString(resp.getEntity()));
				String r=j.getString("Rpta");
				if(r.equals("1"))
				{
					this.DxUsuario.setText(j.getString("Nombre"));
					this.MsjAcreditado.setTextColor(Color.parseColor("#00AF00"));
					this.MsjAcreditado.setText("Usuario Acreditado Correctamente");					
				}else{
					this.DxUsuario.setText("");
					this.MsjAcreditado.setTextColor(Color.parseColor("#00AF00"));
					this.MsjAcreditado.setText("Usuario No Registrado");	
				}
				
			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "error HttpResponse resp = httpclient.execute(httppost);", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "No ha sido posible la conexión al servidor, verifique su conexión a internet", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(getApplicationContext(), "error httppost.setEntity(new UrlEncodedFormEntity(params));", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}		
	}


}
