package com.example.fingerprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText registerEmailEditText;
    private TextView goToLoginTextView;
    private EditText registerPasswordEditText;
    private Button registerButton;
    private ImageView f1;
    String hashedPassword_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmailEditText = findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        f1 = findViewById(R.id.fingerprint);
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Register.this, "Put your finger on the fingerprint scanner & Capture Fingerprint", Toast.LENGTH_LONG).show();
            }
        });

        goToLoginTextView = findViewById(R.id.goToLoginTextView);

        // Rest of your code (registerButton.setOnClickListener, hashWithSHA256, etc.)

        goToLoginTextView.setOnClickListener(view -> {
            // Handle the "Go to Login Page" action
            startActivity(new Intent(Register.this, MainActivity.class));
        });

        registerButton.setOnClickListener(view -> {
            String email = registerEmailEditText.getText().toString();
            String password = registerPasswordEditText.getText().toString();


            if (email.equals("")||password.equals("")){
                Toast.makeText(getApplicationContext(),"Please provide full details",Toast.LENGTH_SHORT).show();
            }

            else{
                Log.e("Entered here","Entered here");
                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                StringRequest requ=new StringRequest(Request.Method.POST, "http://192.168.29.193:8000/register/", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response is: ", response.toString());
                        try {
                            JSONObject o = new JSONObject(response);
                            String dat = o.getString("msg");
                            if(dat.equals("yes"))
                            {
                                Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
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
                                                Intent i1=new Intent(Register.this,MainActivity.class);
                                                startActivity(i1);
                                            }
                                            else
                                            {
                                                Toast.makeText(Register.this, "Error Happened!!!", Toast.LENGTH_LONG).show();
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
                                        m.put("email",email);
                                        m.put("password",password);

                                        return m;
                                    }
                                };
                                requestQueue.add(requ);

                            }
                            else
                            {
                                Toast.makeText(Register.this, "Scan Fingerprint before Register!!!", Toast.LENGTH_LONG).show();
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
                        m.put("email",email);
                        m.put("password",password);

                        return m;
                    }
                };
                requestQueue.add(requ);
            }
        });
    }



}