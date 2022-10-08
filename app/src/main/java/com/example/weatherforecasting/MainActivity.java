package com.example.weatherforecasting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    String x,y;
    TextView descrip;
    TextView tempval;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        descrip=findViewById(R.id.textView);
        tempval=findViewById(R.id.textView2);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        final String[] x = new String[1];
//        final String[] y = new String[1];

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Locationlon",String.valueOf(location.getLongitude()));
                x =String.valueOf(location.getLatitude());
                Log.i("Location", String.valueOf(location.getLatitude()));
                y =String.valueOf(location.getLongitude());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        getWeather();
    }




        public void getWeather() {
            try {
                DownloadTask task = new DownloadTask();
//                Log.i("x",x);
//                String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                Log.i("lat", String.valueOf(userLocation.latitude));
                String lat=String.valueOf(userLocation.latitude);
                String lon=String.valueOf(userLocation.longitude);


//                task.execute("https://api.openweathermap.org/data/2.5/weather?lat=12.8203368&lon=80.0449014&appid=aa1ca6e3a3c3564b78a37a7c8b0e2f1a");

              task.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + lat +"&lon="+lon+ "&appid=aa1ca6e3a3c3564b78a37a7c8b0e2f1a");
//
//                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
            }
        }

        public class DownloadTask extends AsyncTask<String,Void,String> {

            @Override
            protected String doInBackground(String... urls) {
                String result = "";
                URL url;
                HttpURLConnection urlConnection = null;

                try {

                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();

                    while (data != -1) {
                        char current = (char) data;
                        result += current;
                        data = reader.read();
                    }

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);

                    String weatherInfo = jsonObject.getString("weather");

                    Log.i("Weather content", weatherInfo);

                    JSONArray arr = new JSONArray(weatherInfo);
                    JSONObject temp = jsonObject.getJSONObject("main");

                    String message = "";

                    for (int i=0; i < arr.length(); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);

                        String main = jsonPart.getString("main");
                        String description = jsonPart.getString("description");

                        if (!main.equals("") && !description.equals("")) {
                            message +=  description + "\r\n";
                        }
                    }
                    Double tempo= Double.valueOf(temp.getString("temp"))-273;
                    Log.i("temp", String.valueOf(tempo));
                    tempval.setText(String.valueOf(tempo.intValue()));


                    if (!message.equals("")) {
                       descrip.setText(message);
                        Log.i("message",message);
                    } else {
                        Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }

            }
        }
    }

