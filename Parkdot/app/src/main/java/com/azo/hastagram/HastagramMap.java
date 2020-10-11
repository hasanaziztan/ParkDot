package com.azo.hastagram;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.azo.hastagram.Models.IparkNameUpdate;
import com.azo.hastagram.Models.Park;
import com.azo.hastagram.Models.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class HastagramMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    Park park;
    GoogleMap mMap;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    Button dialogButton;
    final Context context = this;
    ProgressBar progressBar;
    boolean isCreate;
    TextView editName;
    Button editNameButton;
    BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hastagram_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        editName = findViewById(R.id.edit_name_textview);
        editNameButton = findViewById(R.id.edit_name_save);


        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        park = (Park) getIntent().getSerializableExtra("park");
        isCreate = getIntent().getBooleanExtra("isCreate", true);
    }


    public void customAlert(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.custom_alert_dialog);
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogButton = alertDialog.findViewById(R.id.customButton);
        final EditText content = alertDialog.findViewById(R.id.customText);
        if (park != null)
            content.setText(park.getParkName());
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name;
                Double latit = latLng.latitude;
                Double longi = latLng.longitude;
                name = content.getText().toString();
                new Park().setParkName(content.getText().toString()).setLatit(latit.toString()).setLongi(longi.toString());
                if (!TextUtils.isEmpty(name) && (!TextUtils.isEmpty(mAuth.getUid()))) {
                    databaseReference = databaseReference.child(mAuth.getUid());
                    databaseReference = databaseReference.child("parkList");
                    String parkid = "";
                    if (isCreate) {
                        parkid = databaseReference.push().getKey();
                    } else {
                        parkid = park.getId(); }
                    databaseReference.child(parkid).setValue(new Park().setId(parkid).setParkName(content.getText().toString()).setLatit(latit.toString()).setLongi(longi.toString()));
                    Toast.makeText(HastagramMap.this, getString(R.string.added), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    content.setText("");
                    finish();

                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        if (park != null) {
            LatLng latLng = new LatLng(Double.parseDouble(park.getLatit()), Double.parseDouble(park.getLongi()));
            mMap.addMarker(new MarkerOptions().title(park.getParkName()).position(latLng).icon(BitmapDescriptorFactory.fromBitmap(getIcon()))).showInfoWindow();
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    bottomSheetDialog = new BottomSheetDialog().setPark(park).setOnclickListener(new IparkNameUpdate() {
                        @Override
                        public void parkNameUpdate(String name) {
                            databaseReference.child(mAuth.getUid()).child("parkList").child(park.getId()).child("parkName").setValue(name);
                            bottomSheetDialog.dismiss();
                            finish();

                        }
                    });
                     bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheetDialog");

                    return false;
                }
            });
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            zoomCurrentLocation();
        }


    }


    private Bitmap getIcon() {
        int height = 96;
        int width = 60;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    private Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(HastagramMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HastagramMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HastagramMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return null;
        } else {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            return location;
        }
    }

    private void zoomCurrentLocation() {
        //izin istemek
        Location location = getCurrentLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 14);
            mMap.animateCamera(yourLocation);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 1) {
                zoomCurrentLocation();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        customAlert(latLng);
        String address = "";
        if (park != null && !TextUtils.isEmpty(park.getParkName()))
            address = park.getParkName();
        mMap.addMarker(new MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.fromBitmap(getIcon()))).showInfoWindow();
        Toast.makeText(getApplicationContext(), getString(R.string.New_place_ok), Toast.LENGTH_SHORT).show();

    }
}
