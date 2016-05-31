package com.example.allu.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Allu on 14/03/2016.
 */
public class WelcomeActivity extends Activity {
    // Duration welcome activity
    private final int TIME_SPLASH = 3000; // 3 seconds
    private TextView txtUser, txtEmail;
    private Button btnLogout;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We can logout during the welcome window
        setContentView(R.layout.activity_welcome);

        txtUser = (TextView) findViewById(R.id.user);
        txtEmail = (TextView) findViewById(R.id.email);

        Intent intent = getIntent();
        txtUser.setText(intent.getStringExtra("user"));
        txtEmail.setText(intent.getStringExtra("email"));

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        handler.postDelayed(new Runnable(){
            public void run(){
                // Start main activity after 3 seconds
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("user", txtUser.getText().toString());
                intent.putExtra("email", txtEmail.getText().toString());
                startActivity(intent);
                finish();
            };
        }, TIME_SPLASH);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("user", txtUser.getText().toString());
        outState.putString("email", txtEmail.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        txtUser.setText(savedInstanceState.getString("user"));
        txtEmail.setText(savedInstanceState.getString("email"));
    }
}
