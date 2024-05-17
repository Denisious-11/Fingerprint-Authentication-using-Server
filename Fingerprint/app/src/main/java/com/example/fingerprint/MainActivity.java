package com.example.fingerprint;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE = 101010;
    ImageView im1;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    Button login;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        im1=findViewById(R.id.imageView);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Put your finger on the fingerprint scanner & Capture Fingerprint", Toast.LENGTH_LONG).show();
            }
        });

        login=findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                StringRequest requ=new StringRequest(Request.Method.POST, "http://192.168.29.193:8000/login/", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response is: ", response.toString());
                        try {
                            JSONObject o = new JSONObject(response);
                            String dat = o.getString("msg");
                            if(dat.equals("success"))
                            {
                                Toast.makeText(MainActivity.this, "Authentication Successful!", Toast.LENGTH_SHORT).show();
                                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                                StringRequest requ=new StringRequest(Request.Method.POST, "http://192.168.29.193:8000/remove/", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.e("Response is: ", response.toString());
                                        try {
                                            JSONObject o = new JSONObject(response);
                                            String dat = o.getString("msg");
                                            if(dat.equals("yes"))
                                            {
                                                Intent i1=new Intent(MainActivity.this,HomePage.class);
                                                startActivity(i1);
                                            }
                                            else
                                            {
                                                Toast.makeText(MainActivity.this, "Error Happened!!!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
//                Log.e(TAG,error.getMessage());
                                        error.printStackTrace();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> m=new HashMap<>();
                                        m.put("val","val");
                                        return m;
                                    }
                                };
                                requestQueue.add(requ);
                            }
                            else if (dat.equals("fail"))
                            {
                                Toast.makeText(MainActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                                StringRequest requ=new StringRequest(Request.Method.POST, "http://192.168.29.193:8000/remove/", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.e("Response is: ", response.toString());
                                        try {
                                            JSONObject o = new JSONObject(response);
                                            String dat = o.getString("msg");
                                            if(dat.equals("yes"))
                                            {
                                                Intent i1=new Intent(MainActivity.this,MainActivity.class);
                                                startActivity(i1);
                                            }
                                            else
                                            {
                                                Toast.makeText(MainActivity.this, "Error Happened!!!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
//                Log.e(TAG,error.getMessage());
                                        error.printStackTrace();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> m=new HashMap<>();
                                        m.put("val","val");
                                        return m;
                                    }
                                };
                                requestQueue.add(requ);
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Scan Fingerprint for Login!!!", Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                Log.e(TAG,error.getMessage());
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> m=new HashMap<>();
                        m.put("val","val");

                        return m;
                    }
                };
                requestQueue.add(requ);
            }
        });
    }
}