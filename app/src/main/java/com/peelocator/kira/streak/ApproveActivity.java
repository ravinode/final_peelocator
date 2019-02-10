package com.peelocator.kira.streak;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.peelocator.kira.streak.fragment.ScoreActivity;
import com.peelocator.kira.streak.pojo.Point;
import com.peelocator.kira.streak.pojo.Rate;
import com.peelocator.kira.streak.util.LoadProperties;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveActivity extends AppCompatActivity {

    String id = null;
    String nameTxt = null;
    String descTxt = null;
    String distance = null;
    String status = null;
    String email = null;
    String maleS = null;
    String femaleS = null;
    String wheelS = null;
    String familyS = null;
    private String slat;
    private String slong;
    private String dlat;
    private String dlong;
    RatingBar cleanessR = null;
    RatingBar service_LevelR = null;
    RatingBar overallR = null;
    TextView click = null;
    Button submit = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>      PEELOCATOR</font>"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));


        setContentView(R.layout.activity_approve);
        TextView name = (TextView) findViewById(R.id.pending);
        TextView pyc = (TextView) findViewById(R.id.yourrating);
        TextView sl = (TextView) findViewById(R.id.servicelevelTxt);
        TextView ov = (TextView) findViewById(R.id.ova1);
        TextView cleantxt = (TextView) findViewById(R.id.cleanessTxt1);
        TextView desc = (TextView) findViewById(R.id.desc);
        TextView dista = (TextView) findViewById(R.id.distance);
        click = (TextView) findViewById(R.id.click);
        final RatingBar cleaness = (RatingBar)findViewById(R.id.cleaness1);
        final RatingBar service_Level = (RatingBar)findViewById(R.id.servicelevel1);
        final RatingBar overall = (RatingBar)findViewById(R.id.overall1);

        final CheckBox male = (CheckBox) findViewById(R.id.malec);
        final CheckBox female = (CheckBox) findViewById(R.id.femalec);
        final CheckBox wheel = (CheckBox) findViewById(R.id.wheel);
        final CheckBox family = (CheckBox) findViewById(R.id.familyc);

        cleanessR = (RatingBar)findViewById(R.id.cleaness);
        service_LevelR = (RatingBar)findViewById(R.id.servicelevel);
        overallR = (RatingBar)findViewById(R.id.overall);

        submit = (Button) findViewById(R.id.approve);
        Button rate = (Button) findViewById(R.id.update);

        Button navigate = (Button) findViewById(R.id.navigate);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id = extras.getString("id");
            nameTxt = extras.getString("name");
            descTxt = extras.getString("desc");
            distance = extras.getString("distance");
            status = extras.getString("status");
            email = extras.getString("userid");

            maleS = extras.getString("male");
            femaleS = extras.getString("female");
            wheelS = extras.getString("wheel");
            familyS = extras.getString("family");

            slat = extras.getString("slat");
            slong = extras.getString("slon");
            dlat = extras.getString("dlat");
            dlong = extras.getString("dlon");

            if(maleS.equalsIgnoreCase("1"))
            {
                male.setChecked(true);
            }
            if(femaleS.equalsIgnoreCase("1"))
            {
                female.setChecked(true);
            }
            if(wheelS.equalsIgnoreCase("1"))
            {
                wheel.setChecked(true);
            }
            if(familyS.equalsIgnoreCase("1"))
            {
                family.setChecked(true);
            }
            if(status.equalsIgnoreCase("1"))
            click.setText("Status: Not Approved");
            if(status.equalsIgnoreCase("2"))
                click.setText("Status: Verified");
            FirebaseUser user = null;
            user = FirebaseAuth.getInstance().getCurrentUser();
          //  Toast.makeText(getApplicationContext(), "Current location:"+extras.get("userid"), Toast.LENGTH_LONG).show();
//
          //  Toast.makeText(getApplicationContext(), "Current location:"+user.getEmail(), Toast.LENGTH_LONG).show();
//
            if(user.getEmail().equalsIgnoreCase(email))
            {
                cleaness.setVisibility(View.INVISIBLE);
                service_Level.setVisibility(View.INVISIBLE);
                overall.setVisibility(View.INVISIBLE);
                pyc.setVisibility(View.INVISIBLE);
                cleantxt.setVisibility(View.INVISIBLE);
                sl.setVisibility(View.INVISIBLE);
                ov.setVisibility(View.INVISIBLE);
                rate.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);

            }

            name.setText("Name: "+nameTxt);
            desc.setText("Description: "+descTxt);
            dista.setText("Distance: "+distance);
            getRating(id,getApplicationContext());
            if("2".equalsIgnoreCase(status))
            {
                submit.setVisibility(View.INVISIBLE);
            }
        }

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                approveFlush(id,getApplicationContext());
                AlertDialog alert = new AlertDialog.Builder(ApproveActivity.this).create();
                alert.setTitle("Approved");
                alert.setMessage("Thanks for approving");
                alert.show();

            }
        });


        navigate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String uri = "http://maps.google.com/maps?saddr=" + slat + "," + slong + "&daddr=" + dlat + "," + dlong;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

            }
        });

        rate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                rate(id,Double.toString(overall.getRating()),Double.toString(cleaness.getRating()), Double.toString(service_Level.getRating()),getApplicationContext());
                approveFlush(id,getApplicationContext());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ApproveActivity.this);
                // set title
                alertDialogBuilder.setTitle("Success");
                alertDialogBuilder.setCancelable(true);
                // set dialog message
                alertDialogBuilder
                        .setMessage("Thank you for submitting")
                        .setCancelable(true)
                        .setPositiveButton( "Okay",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                try {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    //Exception
                                }
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

    }

    public  void approveFlush(String id, final Context context) {
        try {

            String url = null;

            // Toast.makeText(context, "User ID" + user.getEmail(), Toast.LENGTH_LONG).show();
            JSONObject json = new JSONObject();
            try {
                json.put("id",id);
                json.put("point", "2");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                url = LoadProperties.getProperty("APPROVE", context);
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
                            click.setText("Status: Verified");
                            submit.setVisibility(View.INVISIBLE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   // Toast.makeText(context, "Error"+error, Toast.LENGTH_LONG).show();


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

    public  void rate(String id, String rating, String cRating, String sRating, final Context context) {
        try {

            String url = null;
            JSONObject json = new JSONObject();
            try {
                json.put("id",id);
                json.put("rating", rating);
                json.put("s_rating", sRating);
                json.put("c_rating", cRating);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                url = LoadProperties.getProperty("RATE", context);
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

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  //  Toast.makeText(context, "Error"+error, Toast.LENGTH_LONG).show();


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


    public void getRating(String id, final Context context) {
        try {
            String url = null;

            JSONObject json = new JSONObject();
            try {

                json.put("id", id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                url = LoadProperties.getProperty("GET_RATE", context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                    url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("rateResp");
                                List<Rate> rateList = new ArrayList<>();
                                for (int i = 0, size = jsonArray.length(); i < size; i++) {
                                    Log.i("Length", "" + jsonArray.length());
                                    JSONObject objectInArray = jsonArray.getJSONObject(i);

                                    Rate rate = new Rate();

                                    rate.setId(objectInArray.getString("id"));
                                    rate.setC_rating(objectInArray.getString("s_rating"));
                                    rate.setRating(objectInArray.getString("rating"));
                                    rate.setS_rating(objectInArray.getString("c_rating"));
                                  //    Toast.makeText(context, "Hello" + objectInArray.getString("id"), Toast.LENGTH_LONG).show();
                                    rateList.add(rate);


                                    if(rateList.size()!=0) {
                                        cleanessR.setRating(Float.parseFloat(rateList.get(0).getC_rating()));
                                        service_LevelR.setRating(Float.parseFloat(rateList.get(0).getS_rating()));
                                        overallR.setRating(Float.parseFloat(rateList.get(0).getRating()));
                                    }
                                    // Toast.makeText(context, "Hello" + objectInArray, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   // Toast.makeText(context, "Error"+error, Toast.LENGTH_LONG).show();
                   // Toast.makeText(context, "Error"+error, Toast.LENGTH_LONG).show();
                   // Toast.makeText(context, "Error"+error, Toast.LENGTH_LONG).show();


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
