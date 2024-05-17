package com.example.fingerprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class Splashscreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Timer().schedule(new TimerTask(){
            public void run() {
                Splashscreen.this.runOnUiThread(new Runnable()
                {
                    public void run() {
                        startActivity(new Intent(Splashscreen.this, Register.class));
                    }
                });
            }
        }, 2000);
    }
}