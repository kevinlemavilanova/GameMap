package com.example.klemavilanova.mapgame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "gpslog";
    private final static int codigo = 0;
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = (long) 10;
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 5000;
    public Intent intento;
    private LocationManager mLocMgr;
    private TextView textViewGPS, textViewDist;
    private ImageView ImgTesoro;
    private GoogleMap mMap;
    private LatLngBounds VIGO = new LatLngBounds(new LatLng(42.236873, -8.717089), new LatLng(42.237139, -8.710813));
    // coordenadas del tesoro
    private LatLng tesoro;
    private Location tesoroLoc;
    // recibe notificaciones del LocationManager
    public LocationListener locListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.i(TAG, "Lat " + location.getLatitude() + " Long " + location.getLongitude());
            textViewGPS.setText("Lat " + (float) location.getLatitude() + " Long " + (float) location.getLongitude());

            // movemos la camara para la nueva posicion
            LatLng nuevaPosicion = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(nuevaPosicion)
                    .zoom(20)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //calculamos la distancia a la marca
            textViewDist.setText(String.valueOf((int) location.distanceTo(tesoroLoc)) + "m");
        }

        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled()");
        }

        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled()");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged()");
        }
    };
    private double lat = 42.236026, lng = -8.712278;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        textViewGPS = (TextView) findViewById(R.id.lat);
        textViewDist = (TextView) findViewById(R.id.dist);
        ImgTesoro = (ImageView) findViewById(R.id.star);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intento = new Intent(MapsActivity.this, QRactivity.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intento);
            }
        });


        mLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requiere permisos para Android 6.0
            Log.e(TAG, "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            return;
        } else {
            Log.i(TAG, "Permisos necesarios OK!.");
            // registra el listener para obtener actualizaciones
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, locListener, Looper.getMainLooper());
        }
        textViewGPS.setText("Lat " + " Long ");
        // creamos objetos para determinar el tesoro
        tesoro = new LatLng(lat, lng);
        tesoroLoc = new Location(LocationManager.GPS_PROVIDER);
        tesoroLoc.setLatitude(lat);
        tesoroLoc.setLongitude(lng);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //engadimos controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Zoom en coordenadas de vigo para no ampliar desd el mapa completo
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VIGO.getCenter(), 15));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == codigo) {
                String text = data.getExtras().getString("retorno");
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                if (text.equals("Tesoro de las Torres")){
                    ImgTesoro.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}