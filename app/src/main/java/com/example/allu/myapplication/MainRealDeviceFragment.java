package com.example.allu.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allu.myapplication.backend.messaging.Messaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Allu on 15/03/2016.
 */
public class MainRealDeviceFragment extends Fragment implements SensorEventListener,LocationListener {
    private SensorManager sensorManager;
    private TextView temperature,humidity,pressure,altitude,hora;
    private Button subirDatos;
    private String sPref;
    private String temperaturetype="Fahrenheit ºF";
    public DataEndpointImpl dataImpl = new DataEndpointImpl();
    private SQLiteDatabase db;
    final Messaging message;
    private LocationManager locManager;
    private String latitude,longitude;
    private EditText town;

    public MainRealDeviceFragment(){
        Messaging.Builder messagebuilder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("https://weathernow-b8825.appspot.com/_ah/api/");

        this.message = messagebuilder.build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.main_realdevice_fragment, container, false);
        temperature = (TextView) ll.findViewById(R.id.temperatureInfo);
        humidity = (TextView) ll.findViewById(R.id.humidityInfo);
        pressure = (TextView) ll.findViewById(R.id.pressureInfo);
        altitude = (TextView) ll.findViewById(R.id.altitudeInfo);
        hora = (TextView) ll.findViewById(R.id.hora);
        subirDatos = (Button) ll.findViewById(R.id.subirDatos);
        town = (EditText) ll.findViewById(R.id.town);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)==null){
            temperature.setText("Sorry, dont have temperature sensor");
        }if(sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)==null){
            humidity.setText("Sorry, dont have humidity sensor");
        }if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)==null){
            pressure.setText("Sorry, dont have humidity sensor");
            altitude.setText("Sorry, you need humidity sensor");
        }

        comenzarLocalizacion();

        subirDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSQLiteHelper padbh =
                        new BaseSQLiteHelper(getActivity().getApplication(), "DBBase", null, 1);
                db = padbh.getWritableDatabase();
                if (db != null) {
                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("town", town.getText().toString());
                    nuevoRegistro.put("degrees", temperature.getText().toString());
                    nuevoRegistro.put("humidity", humidity.getText().toString());
                    nuevoRegistro.put("latitude", latitude);
                    nuevoRegistro.put("longitude", longitude);
                    nuevoRegistro.put("optional", "");
                    db.insert("Weather", null, nuevoRegistro);
                }
                Calendar calendar = Calendar.getInstance();
                hora.setText("Última subida: "+calendar.getTime());
                new dataStoreTask().execute();
                new messageTask().execute();

            }
        });
        return ll;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        getSensorEvent(event);
    }

    private void getSensorEvent(SensorEvent event) {
        float value = event.values[0];
        if(event.sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE){
            if(sPref.equals(temperaturetype)) temperature.setText((value*1800+32)+"ºF");
            else temperature.setText(value+"ºC");
        }
        else if(event.sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY){
            humidity.setText(value+"%");
        }
        else if(event.sensor.getType()==Sensor.TYPE_PRESSURE){
            pressure.setText(value+"hPa");
            altitude.setText((SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value))+"m");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(sPref.equals(temperaturetype)&&sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null) temperature.setText("0.0ºF");
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("Temperature", "Celsius ºC");
    }

    @Override
    public void onPause() {
        // unregister listener
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        guardarPosicion(location);
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

    private void guardarPosicion(Location loc) {
        if(loc != null)
        {
            latitude = String.valueOf(loc.getLatitude());
            longitude = String.valueOf(loc.getLongitude());
            Log.i("", String.valueOf(loc.getLatitude() + " - " + String.valueOf(loc.getLongitude())));
        }
    }

    private class dataStoreTask extends AsyncTask<Void, Integer, Void> {

        /*
        Se hace visible el botón "Cancelar" y se desactiva
        el botón "Ordenar"
         */
        @Override
        protected void onPreExecute() {

        }

        /*
        Ejecución del ordenamiento y transmision de progreso
         */
        @Override
        protected Void doInBackground(Void... params) {

            dataImpl.pushToRemote(getActivity().getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private class messageTask extends AsyncTask<Void, Integer, Void> {

        /*
        Se hace visible el botón "Cancelar" y se desactiva
        el botón "Ordenar"
         */
        @Override
        protected void onPreExecute() {

        }

        /*
        Ejecución del ordenamiento y transmision de progreso
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                message.sendMessageGCM("Nuevos datos en el servidor.").execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private void comenzarLocalizacion()
    {
        //Obtenemos una referencia al LocationManager
        locManager =
                (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);}
        if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, this);}
        if(!locManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 30000, 0, this);
        }


        //Obtenemos la �ltima posici�n conocida
        Location loc =
                locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);



        //Mostramos la �ltima posici�n conocida
        guardarPosicion(loc);

        //Nos registramos para recibir actualizaciones de la posici�n




    }

    @Override
    public void onDestroy() {
        super.onPause();
        locManager.removeUpdates(this);
    }

}
