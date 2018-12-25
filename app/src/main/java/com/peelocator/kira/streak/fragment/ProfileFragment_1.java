package com.peelocator.kira.streak.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.peelocator.kira.streak.R;
import com.peelocator.kira.streak.auth.LoginMainActivity;


public class ProfileFragment_1 extends Fragment {

    private Button btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    public ProfileFragment_1() {
        // Required empty public constructor
    }

    public static ProfileFragment_1 newInstance(String param1, String param2) {
        ProfileFragment_1 fragment = new ProfileFragment_1();
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
        // Inflate the layout for this fragment
        Intent intent = new Intent(getActivity(), LoginMainActivity.class);
        startActivity(intent);
        return inflater.inflate(R.layout.activity_main_auth, container, false);

    }
}
