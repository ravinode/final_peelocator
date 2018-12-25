package com.peelocator.kira.streak.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.peelocator.kira.streak.R;
import com.peelocator.kira.streak.auth.LoginActivity;
import com.peelocator.kira.streak.pojo.AddFlush;
import com.peelocator.kira.streak.pojo.Point;
import com.peelocator.kira.streak.util.FlushUtil;
import com.peelocator.kira.streak.util.LoadProperties;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;

public class AddActivity extends Fragment{

    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private FusedLocationProviderClient client;
    EditText loc = null;
    FirebaseUser user = null;
    static String lat;
    static String lon;
    public AddActivity() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(null==user)
        {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(null==user)
        {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        View view = inflater.inflate(R.layout.activity_add, container, false);

        final EditText name = (EditText)view.findViewById(R.id.name);
        loc = (EditText)view.findViewById(R.id.locationEdit);
        final EditText desc = (EditText)view.findViewById(R.id.desc);
        RadioGroup service = (RadioGroup)view.findViewById(R.id.service);

        final RatingBar cleaness = (RatingBar)view.findViewById(R.id.cleaness);
        final RatingBar service_Level = (RatingBar)view.findViewById(R.id.servicelevel);
        final RatingBar overall = (RatingBar)view.findViewById(R.id.overall);
        Button submit = (Button) view.findViewById(R.id.submit);
        final RadioButton service_button = (RadioButton)view.findViewById(service.getCheckedRadioButtonId());

final CheckBox male = (CheckBox) view.findViewById(R.id.malec);
        final CheckBox female = (CheckBox) view.findViewById(R.id.femalec);
        final CheckBox wheel = (CheckBox) view.findViewById(R.id.wheel);
        final CheckBox family = (CheckBox) view.findViewById(R.id.familyc);
        loc.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }

                }
            }
        });




        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null) {
                            Address returnedAddress = addresses.get(0);
                            StringBuilder strReturnedAddress = new StringBuilder("");

                            for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                            }

                            loc.setText(strReturnedAddress.toString().trim());
                            lat = String.valueOf(location.getLatitude());
                            lon = String.valueOf(location.getLongitude());

                           // Log.w("My Current loction address", strReturnedAddress.toString());
                        } else {
                          //  Log.w("My Current loction address", "No Address returned!");
                        }



                    } catch (Exception e) {

                    }
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                client = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            AddFlush addReq = new AddFlush();
                            if(male.isChecked()) {
                                addReq.setMale("1");
                            }
                            if(female.isChecked()) {
                                addReq.setFemale("1");
                            }
                            if(wheel.isChecked()) {
                                addReq.setWheel("1");
                            }
                            if(family.isChecked()) {
                                addReq.setFamily("1");
                            }
                            addReq.setName(name.getText().toString());
                            addReq.setDescription(desc.getText().toString());
                            addReq.setServiceType(service_button.getText().toString());

                             addReq.setC_rating(Double.toString(cleaness.getRating()));
                             addReq.setS_rating(Double.toString(service_Level.getRating()));
                             addReq.setRating(Double.toString(overall.getRating()));
                            addReq.setLatitude(String.valueOf(location.getLatitude()));
                            addReq.setLongitude(String.valueOf(location.getLongitude()));
                            addFlush(addReq,getContext());
                        }
                    }
                });



            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                Log.i(TAG, "Place:+++ " +  place.getLatLng().latitude);
                lon = String.valueOf(place.getLatLng().longitude);
                lat = String.valueOf(place.getLatLng().latitude);
                loc.setText(place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public  void addFlush(AddFlush addFlush, final Context context) {
        try {
            FirebaseAuth auth;
            String url = null;

            // Toast.makeText(context, "User ID" + user.getEmail(), Toast.LENGTH_LONG).show();
            JSONObject json = new JSONObject();
            try {
                json.put("id",FlushUtil.getID());
                json.put("userid", user.getEmail());
                json.put("name", addFlush.getName());
                json.put("description", addFlush.getDescription());
                json.put("rating", addFlush.getRating());
                json.put("s_rating", addFlush.getS_rating());
                json.put("c_rating", addFlush.getC_rating());
                json.put("latitude", lat);
                json.put("longitude", lon);
                json.put("male", addFlush.getMale());
                json.put("female", addFlush.getFemale());
                json.put("wheel", addFlush.getWheel());
                json.put("family", addFlush.getFamily());

                json.put("serviceType", addFlush.getServiceType());
                json.put("status","1");
                json.put("point","5");
                json.put("point_status","P");
                json.put("count","1");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                url = LoadProperties.getProperty("ADD_LOCATION", context);
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
                            List<Point> pointList = new ArrayList<>();
                            try {
                                JSONArray jsonArray = response.getJSONArray("pointResp");

                                for (int i = 0, size = jsonArray.length(); i < size; i++) {
                                    Point point = new Point();
                                    JSONObject objectInArray = jsonArray.getJSONObject(i);
                                    point.setPoints(objectInArray.getString("points"));
                                    point.setPointStatus(objectInArray.getString("pointStatus"));
                                    //   Toast.makeText(context, "Hello" + objectInArray.getString("points"), Toast.LENGTH_LONG).show();
                                    pointList.add(point);
                                }
                            } catch (JSONException e) {

                            }

                            Intent intent = new Intent(getActivity(), ScoreActivity.class);
                            intent.putExtra("list",(ArrayList<Point>)pointList);
                            startActivity(intent);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error"+error, Toast.LENGTH_LONG).show();


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

}
