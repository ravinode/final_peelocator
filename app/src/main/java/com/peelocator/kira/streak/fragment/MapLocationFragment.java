package com.peelocator.kira.streak.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.peelocator.kira.streak.ApproveActivity;
import com.peelocator.kira.streak.CustomInfoWindowGoogleMap;
import com.peelocator.kira.streak.R;
import com.peelocator.kira.streak.auth.LoginActivity;
import com.peelocator.kira.streak.pojo.InfoWindowData;
import com.peelocator.kira.streak.pojo.LatLongReq;
import com.peelocator.kira.streak.util.LoadProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapLocationFragment extends Fragment implements GoogleMap.OnMarkerClickListener {



    MapView mMapView;
    private static GoogleMap googleMap;
    static Marker marker;

    private FusedLocationProviderClient client;
    public FirebaseAuth auth;

    ArrayList<LatLng> markerPoints;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

      return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle                                                                          savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.activity_maps, container, false);
        mMapView= (MapView) rootView.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        checkLocationPermission();
        getFlush();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                if (checkLocationPermission()) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                    }
                }


                markerPoints = new ArrayList<LatLng>();

                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);

            }
        });
        return rootView;
    }

    private void getFlush() {
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    LatLongReq req = new LatLongReq();
                    req.setLat(location.getLatitude());
                    req.setLog(location.getLongitude());
                    getLocation(req, getActivity());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),new String[]
                                        {ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        googleMap.setMyLocationEnabled(true);
                        getFlush();

                    }

                }
                return;
            }

        }
    }
    public  void getLocation(LatLongReq latLong, final Context context) {
        try {
            String url = null;

            JSONObject json = new JSONObject();
            try {

                json.put("latitude", latLong.getLat().toString());
                json.put("longtitude", latLong.getLog().toString());
                json.put("distance", "40");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                url = LoadProperties.getProperty("GET_LOCATION", context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Toast.makeText(context, "This is my Toast message!" + url,
            //       Toast.LENGTH_LONG).show();
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                    url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("flushLoc");

                                for (int i = 0, size = jsonArray.length(); i < size; i++) {

                                    JSONObject objectInArray = jsonArray.getJSONObject(i);
                                    Double lat = Double.parseDouble(objectInArray.getString("latitude"));
                                    Double log = Double.parseDouble(objectInArray.getString("longitude"));
                                    String service = objectInArray.getString("serviceType");
                                    String status = objectInArray.getString("status");
                                    String id = objectInArray.getString("id");
                                    String emailID = objectInArray.getString("addedBy");
                                    String male = objectInArray.getString("male");
                                    String female = objectInArray.getString("female");
                                    String wheel = objectInArray.getString("wheel");
                                    String family = objectInArray.getString("family");

                                   // Toast.makeText(context, "Hello" + wheel, Toast.LENGTH_LONG).show();
                                    drawMarker(context, new LatLng(lat, log), objectInArray.getString("male"),objectInArray.getString("female"),objectInArray.getString("wheel"),objectInArray.getString("family"),objectInArray.getString("name"), objectInArray.getString("description"), objectInArray.getString("distance"),service,status,id,emailID);

                                }
                            } catch (JSONException e) {

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {



                }
            })

            {

                @Override
                public Map getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    return volleyError;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjRequest);
        } catch (Exception e)

        {
            e.printStackTrace();
        }
    }

    private  void drawMarker(final Context context, final LatLng point, final String male,final String female, final String wheel, final String family,final String text, final String description, final String distance, final String serviceType, final String status,final String id,final String emailID) {
System.out.print("Value"+wheel);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,12f));
        marker = googleMap.addMarker(new MarkerOptions().position(point).title(text).alpha(0.7f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        final InfoWindowData info = new InfoWindowData();
        info.setImage(id);
        info.setPrice(serviceType);
        if(distance.length()>3) {
            info.setDistance(distance.substring(0, 3) + " Miles");
        }
        else
        {
            info.setDistance(distance + " Miles");
        }

        info.setStatus(status);
        info.setId(text);
        info.setEmailID(emailID);
        info.setMale(male);
        info.setFemale(female);
        info.setWheel(wheel);
        info.setFamily(family);
        info.setDescription(description);
        marker.setTag(info);
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(context);
        googleMap.setInfoWindowAdapter(customInfoWindow);





        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                FirebaseUser user = null;
                user = FirebaseAuth.getInstance().getCurrentUser();
                InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
                if(null == user)
                {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                else
                {


                    Intent intent = new Intent(getActivity(), ApproveActivity.class);
                    intent.putExtra("title",marker.getTitle());

                    intent.putExtra("id",infoWindowData.getImage());
                    intent.putExtra("name",infoWindowData.getId());
                    intent.putExtra("desc",infoWindowData.getDescription());
                    intent.putExtra("distance",infoWindowData.getDistance());
                    intent.putExtra("status",infoWindowData.getStatus());
                    intent.putExtra("userid",emailID);

                    intent.putExtra("male",infoWindowData.getMale());
                    intent.putExtra("female",infoWindowData.getFemale());
                    intent.putExtra("wheel",infoWindowData.getWheel());
                    intent.putExtra("family",infoWindowData.getFamily());





                    startActivity(intent);

                }
//                else if(user.getEmail().equalsIgnoreCase(infoWindowData.getEmailID()))
//                {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//                  //  final RatingBar rating = new RatingBar(context);
//                    dialog.setTitle("Alert")
//
//                            .setMessage("You can rate your own post" )
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialoginterface, int i) {
//                            }
//                            }).show();
//                    //rating.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
//
//                }


            }
        });

    }


}

