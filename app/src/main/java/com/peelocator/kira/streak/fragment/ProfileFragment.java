package com.peelocator.kira.streak.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.peelocator.kira.streak.R;
import com.peelocator.kira.streak.auth.LoginActivity;
import com.peelocator.kira.streak.pojo.Point;
import com.peelocator.kira.streak.pojo.PointReq;
import com.peelocator.kira.streak.util.LoadProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private Button btnSendResetEmail,
         sendEmail, signOut;

    private EditText oldEmail, password;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    TextView available = null;
    TextView pending = null;


    public ProfileFragment() {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main_auth, container, false);

        available = (TextView) view.findViewById(R.id.availalbe);
        pending = (TextView) view.findViewById(R.id.pending);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(null==user)
        {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            PointReq req = new PointReq();
            req.setUserID(user.getEmail());
            getLocation(req,getContext());
        }


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    //startActivity(new Intent(getContext(), LoginActivity.class));

                }
            }
        };


        btnSendResetEmail = (Button) view.findViewById(R.id.sending_pass_reset_button);
        sendEmail = (Button) view.findViewById(R.id.send);
        signOut = (Button) view.findViewById(R.id.sign_out);

        oldEmail = (EditText) view.findViewById(R.id.old_email);
        password = (EditText) view.findViewById(R.id.password);



        sendEmail.setVisibility(View.GONE);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }







        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);

                password.setVisibility(View.GONE);

                sendEmail.setVisibility(View.VISIBLE);

            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        return view;

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    public void getLocation(PointReq point, final Context context) {
        try {
            String url = null;

            JSONObject json = new JSONObject();
            try {

                json.put("userID", point.getUserID().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                url = LoadProperties.getProperty("GET_POINT", context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
                    url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("pointResp");
                                List<Point> pointList = new ArrayList<>();
                                for (int i = 0, size = jsonArray.length(); i < size; i++) {
                                    Log.i("Length", "" + jsonArray.length());
                                    JSONObject objectInArray = jsonArray.getJSONObject(i);

                                    Point point = new Point();
                                    point.setPoints(objectInArray.getString("points"));
                                    point.setPointStatus(objectInArray.getString("pointStatus"));
                                    //   Toast.makeText(context, "Hello" + objectInArray.getString("points"), Toast.LENGTH_LONG).show();
                                    pointList.add(point);

                                    // Toast.makeText(context, "Hello" + objectInArray, Toast.LENGTH_LONG).show();
                                }
if(pointList.size()!=0) {
    pending.setText("Pending Points is: " + pointList.get(0).getPoints());
    if(pointList.size() == 2)
    available.setText("Available Points is: " + pointList.get(1).getPoints());
}
                            } catch (JSONException e) {

                            }
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
