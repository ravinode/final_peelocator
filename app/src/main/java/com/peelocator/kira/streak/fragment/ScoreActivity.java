package com.peelocator.kira.streak.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.peelocator.kira.streak.MainActivity;
import com.peelocator.kira.streak.R;
import com.peelocator.kira.streak.pojo.Point;

import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#746E66'>      Pee Locator</font>"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_score);
        final TextView available = (TextView) findViewById(R.id.availalbe);
        final TextView pending = (TextView) findViewById(R.id.pending);
        Button button = (Button) findViewById(R.id.btn_reset_password);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            List<Point> customer = ( List<Point>)extras.getSerializable("list");

            if(customer != null && !customer.isEmpty() ) {

                // Toast.makeText(ScoreActivity.this, "Hello" + customer.get(0).getPoints(), Toast.LENGTH_LONG).show();
                pending.setText("Pending Points is: " + customer.get(0).getPoints());
            }
                if(customer.size() == 2)
                available.setText("Available Points is: " + customer.get(1).getPoints());


        }
        else
        {

        }

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }

}
