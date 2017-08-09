package com.example.root.mapsapp;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleMap map;
    GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (googlePalyserviceavilable()) {
            Toast.makeText(this, "Perfect!!!!....", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        } else {

        }
    }

    private void initMap() {
        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        fragment.getMapAsync(this);
    }

    public boolean googlePalyserviceavilable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isavailable = api.isGooglePlayServicesAvailable(this);
        if (isavailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isavailable)) {
            Dialog dialog = api.getErrorDialog(this, isavailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "can not connect play service", Toast.LENGTH_LONG).show();
        }
        return false;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if(map != null)
        {
           map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
               @Override
               public void onMapLongClick(LatLng latLng) {
                   MainActivity.this.setMarker("Local",latLng.latitude,latLng.longitude);
               }
           });


            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(MainActivity.this);
                    LatLng li = marker.getPosition();
                    double lat = li.latitude;
                    double lang = li.longitude;
                    List<android.location.Address> list = null;
                    try {
                        list = gc.getFromLocation(lat,lang,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    android.location.Address a = list.get(0);
                    marker.setTitle(a.getLocality());
                    marker.showInfoWindow();

                }
            });
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window,null);
                    TextView locality = (TextView)v.findViewById(R.id.tv_locality);
                    TextView lat = (TextView)v.findViewById(R.id.tv_lat);
                    TextView lang = (TextView)v.findViewById(R.id.tv_lang);
                    TextView snippet = (TextView)v.findViewById(R.id.tv_snippet);
                    LatLng li = marker.getPosition();
                    locality.setText(marker.getTitle());
                    lat.setText("Lantitude: "+li.latitude);
                    lang.setText("Longtitude:" +li.longitude);
                    snippet.setText(marker.getSnippet());
                    return v;
                }
            });
        }

        //goToLocationZoom(23.830620,90.416174,19);
       if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
          return;
       }
        map.setMyLocationEnabled(true);
//        apiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).
//                addConnectionCallbacks(this).addOnConnectionFailedListener(this).
//                build();
//        apiClient.connect();
    }

    private void goToLocation(double lat, double lng) {
        LatLng li = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(li);
        map.moveCamera(update);
    }
    Marker marker;

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng li = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(li, zoom);
        map.moveCamera(update);
    }

    public void geoLocate(View view) throws IOException {
        EditText editText = (EditText) findViewById(R.id.editText2);
        String location = editText.getText().toString();
        Geocoder gc = new Geocoder(this);
        List<android.location.Address> list = gc.getFromLocationName(location, 1);
        android.location.Address address = list.get(0);
        String locality = address.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lang = address.getLongitude();
        goToLocationZoom(lat, lang, 19);

        setMarker(locality, lat, lang);

        }

//        Circle circle;
//        Marker marker1;
//        Marker marker2;
//        Polyline line;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    static final int Polygon_points = 5;
    Polygon shape;
    private void setMarker(String locality, double lat, double lang) {
//        if(marker != null)
//            //marker.remove();
//            removeEverything();
        if(markers.size() == Polygon_points)
            removeEverythings();


        MarkerOptions options = new MarkerOptions().title(locality).draggable(true).
                                    icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).
                                   // icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).
                                    position(new LatLng(lat,lang))
                                    .snippet("I am in here");
        markers.add(map.addMarker(options));
        if(markers.size() == Polygon_points)
            drawpolygons();
//        if(marker1 == null)
//             marker1 = map.addMarker(options);
//        else if(marker2 == null)
//        {
//            marker2 = map.addMarker(options);
//            drawLine();
//        }
//        else
//        {
//            removeEverything();
//            marker1 = map.addMarker(options);
//        }
//
       // circle = drawcircle(new LatLng(lat,lang));
    }

    private void drawpolygons() {
        PolygonOptions options = new PolygonOptions()
                    .fillColor(0x330000FF).strokeWidth(3)
                    .strokeColor(Color.RED);
        for(int i = 0; i<Polygon_points;i++)
            options.add(markers.get(i).getPosition());
        shape = map.addPolygon(options);

    }

    private void removeEverythings() {
        for(Marker marker:markers)
            marker.remove();
        markers.clear();
        shape.remove();
        shape = null;
    }

//    private void drawLine() {
//        PolylineOptions options = new PolylineOptions()
//                .add(marker1.getPosition()).add(marker2.getPosition())
//                .color(Color.BLUE).width(3);
//        line = map.addPolyline(options);
//
//    }

  private void removeEverything() {
//        marker1.remove();
//          marker1 = null;
//        marker2.remove();
//        marker2 = null;
//        line.remove();
//        marker.remove();
//        marker = null;
//        circle.remove();
//        circle = null;
    }

//    private Circle drawcircle(LatLng latLng) {
//        CircleOptions options = new CircleOptions().center(latLng)
//                                .radius(1000).fillColor(0x33FF0000)
//                                .strokeColor(Color.BLUE).strokeWidth(3);
//        return map.addCircle(options);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    LocationRequest request;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null)
        {
            Toast.makeText(this, "can not get current Location", Toast.LENGTH_LONG).show();
        }
        else
        {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,19);
            map.animateCamera(cameraUpdate);
        }

    }
}
