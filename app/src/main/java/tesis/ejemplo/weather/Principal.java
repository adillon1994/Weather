package tesis.ejemplo.weather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.DisplayContext;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Type;

import dagger.Provides;
import tesis.ejemplo.weather.Common.Common;
import tesis.ejemplo.weather.helper.helper;
import tesis.ejemplo.weather.model.OpenWeatherMap;

public class Principal extends AppCompatActivity implements LocationListener {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;

    ImageView imageView;



    LocationManager locationManager;

    String provider;

    static double lat, lng;

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();



    int MY_PERMISSION = 0;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_principal);



        //Control

        txtCity = (TextView) findViewById(R.id.txtcity);

        txtLastUpdate = (TextView) findViewById(R.id.txtlastupdate);

        txtDescription = (TextView) findViewById(R.id.txtdescription);

        txtHumidity = (TextView) findViewById(R.id.txtHumidity);

        txtTime = (TextView) findViewById(R.id.txtTime);

        txtCelsius = (TextView) findViewById(R.id.txtCelcius);

        imageView = (ImageView) findViewById(R.id.imageView);





        //Get Coordinates

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(Principal.this, new String[] {

                    Manifest.permission.INTERNET,

                    Manifest.permission.ACCESS_COARSE_LOCATION,

                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_NETWORK_STATE,

                    Manifest.permission.SYSTEM_ALERT_WINDOW,

                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);

        }

        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null)

            Log.e("TAG","No Location");

    }



    @Override

    protected void onPause() {

        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Principal.this, new String[]{

                    Manifest.permission.INTERNET,

                    Manifest.permission.ACCESS_COARSE_LOCATION,

                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_NETWORK_STATE,

                    Manifest.permission.SYSTEM_ALERT_WINDOW,

                    Manifest.permission.WRITE_EXTERNAL_STORAGE





            }, MY_PERMISSION);

        }

        locationManager.removeUpdates(this);

    }



    @Override

    protected void onResume() {

        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Principal.this, new String[]{

                    Manifest.permission.INTERNET,

                    Manifest.permission.ACCESS_COARSE_LOCATION,

                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_NETWORK_STATE,

                    Manifest.permission.SYSTEM_ALERT_WINDOW,

                    Manifest.permission.WRITE_EXTERNAL_STORAGE





            }, MY_PERMISSION);

        }

        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }



    @Override

    public void onLocationChanged(Location location) {

        lat = location.getLatitude();

        lng = location.getLongitude();



        new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));

    }



    @Override

    public void onStatusChanged(String provider, int status, Bundle extras) {



    }



    @Override

    public void onProviderEnabled(String provider) {



    }



    @Override

    public void onProviderDisabled(String provider) {



    }



    private class GetWeather extends AsyncTask<String,Void,String>{

        ProgressDialog pd = new ProgressDialog(Principal.this);





        @Override

        protected void onPreExecute() {

            super.onPreExecute();

            pd.setTitle("Please wait...");

            pd.show();



        }





        @Override

        protected String doInBackground(String... params) {

            String stream = null;

            String urlString = params[0];



            helper http = new helper();

            stream = http.getHTTPData(urlString);

            return stream;

        }



        @Override

        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            if(s.contains("Error: Not found city")){

                pd.dismiss();

                return;

            }

            Gson gson = new Gson();

            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();

            openWeatherMap = gson.fromJson(s,mType);

            pd.dismiss();



            txtCity.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));

            txtLastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));

            txtDescription.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));

            txtHumidity.setText(String.format("%d%%",openWeatherMap.getMain().getHumidity()));

            txtTime.setText(String.format("%s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));

            txtCelsius.setText(String.format("%.2f °C",openWeatherMap.getMain().getTemp()));

            Picasso.with(Principal.this)

                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))

                    .into(imageView);



        }



    }

}