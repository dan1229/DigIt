package com.example.daniel.digit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Questions extends AppCompatActivity {

    private Spinner spinnerCost; //spinnerCost variable
    private static final String[] paths = {"Select Price Level", "$", "$$", "$$$", "$$$$"}; //cost spinner declaration

    private Spinner spinnerType; //spinnerType variable
    private static final String[] paths2 = {"Select Type", "item 1", "item 2", "item 3"}; //type spinner declaration

    private String txtCost;
    private String txtType;

    private TextView address; //txtViewAddress XML element variable
    private String url; //Zip code translated to string for URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions); //Set layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //Set toolbar
        setSupportActionBar(toolbar);

        //spinner cost populate
        spinnerCost = (Spinner) findViewById(R.id.spinnerQuestion1);
        ArrayAdapter<String> adapterCost = new ArrayAdapter<String>(Questions.this,
                android.R.layout.simple_spinner_item, paths);
        adapterCost.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCost.setAdapter(adapterCost);

        //spinner type populate
        spinnerType = (Spinner) findViewById(R.id.spinnerQuestion2);
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(Questions.this,
                android.R.layout.simple_spinner_item, paths2);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        //spinnerCost listener
        spinnerCost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = spinnerCost.getSelectedItem().toString();
                if (!text.contains("Select")) {
                    String txtCost = spinnerCost.getSelectedItem().toString();
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        //spinnerType listener
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = spinnerType.getSelectedItem().toString();
                if (!text.contains("Select")) {
                    String txtType = spinnerCost.getSelectedItem().toString();
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        //Assigns enter key to Search function
        address = (TextView) findViewById(R.id.txtViewAddress); //Assigns TextView to variable address
        address.setOnKeyListener(new View.OnKeyListener() { //Creates onKeyListener for txtViewAddress
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                        Search(v);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    //Searches for zip code on button press
    public void Search (View v) {

        //RequestQueue Variable
        RequestQueue mRequestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        //Instantiate the RequestQueue with the cache and network
        mRequestQueue = new RequestQueue(cache, network);

        //Start Queue
        mRequestQueue.start();

        String addressurl = "http://maps.googleapis.com/maps/api/geocode/json?address=33498";

        //Formulate the request and handle the response
        StringRequest addressRequest = new StringRequest(Request.Method.GET, addressurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String coordinates = parseLocation(response);
                        String lat = coordinates.split(",")[0]; //splits Latitude
                        String lng = coordinates.split(",")[1]; //splits longitude
                        if(lng.length() < 1||lat.length()<1) {
                            showAlert("Please Enter A Valid Zip Code");
                        }
                        showAlert("Latitude is: " + lat + "\n" + "Longitude is: " + lng);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showAlert("FUCK."); //Error code display
            }
        });
        mRequestQueue.add(addressRequest); //Add request to ResponseQueue

        Intent intentResults = new Intent(this, Results.class); //intent for results.xml
        startActivity(intentResults);
    }

    //Parsing function
    public String parseLocation(String res)
    {
        try {
            JSONObject reader = new JSONObject(res);
            JSONArray arr = reader.getJSONArray("results");
            JSONObject results = arr.getJSONObject(0);
            JSONObject geometry = results.getJSONObject("geometry");
            JSONObject loc = geometry.getJSONObject("location");
            String lat = loc.getString("lat");
            String lng = loc.getString("lng");

            return lat+","+lng;
        } catch (JSONException e) {
            return "Please enter a valid address";

        }

    }

    //Alert test function
    public void showAlert(String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(Questions.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}