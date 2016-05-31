package com.example.allu.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    static final LatLng LLEIDA = new LatLng(41.619102, 0.619217);
    private LatLng markers;
    private double latitude, longitude;

    private LocationManager locManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.activity_maps_fragment, container, false);

        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())!= ConnectionResult.SUCCESS){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), getActivity(),1 );
            dialog.show();
        }

        setUpMapIfNeeded();
        comenzarLocalizacion();

        return ll;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        BaseSQLiteHelper padbh =
                new BaseSQLiteHelper(getContext(), "DBBase", null, 1);
        SQLiteDatabase db = padbh.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Weather", null);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    latitude = Double.parseDouble(c.getString(c.getColumnIndex("latitude")));
                    longitude = Double.parseDouble(c.getString(c.getColumnIndex("longitude")));
                    markers = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(markers).title(c.getString(c.getColumnIndex("optional"))).snippet("Temperatura: "+ c.getString(c.getColumnIndex("degrees")) +" Humedad: "+c.getString(c.getColumnIndex("humidity"))).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_green)));
                } while (c.moveToNext());
            }
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LLEIDA, 10));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
    }

    @Override
    public void onLocationChanged(Location location) {
        mostrarPosicion(location);
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

    private void mostrarPosicion(Location loc) {
        if(loc != null)
        {
            final LatLng myPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10));
            Log.i("", String.valueOf(loc.getLatitude() + " - " + String.valueOf(loc.getLongitude())));
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
        mostrarPosicion(loc);

        //Nos registramos para recibir actualizaciones de la posici�n




    }

    @Override
    public void onDestroy() {
        super.onPause();
        locManager.removeUpdates(this);
    }
}
