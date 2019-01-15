package my.edu.taruc.assignment;

import android.content.SharedPreferences;
import android.media.Rating;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class CarPoolRoom extends AppCompatActivity {

    private TextView textViewDriver, textViewCharges, textViewSlot, textViewFrom, textViewTo, textViewCreatedTime, textViewNote, tvModel, tvPlateNo, tvColour;
    private String GET_URL = "https://yaptw-wa16.000webhostapp.com/select_carpool_where.php";
    private String DELETE_URL_HOST = "https://yaptw-wa16.000webhostapp.com/delete_carpool_where.php";
    private String DELETE_URL_PASSENGER = "https://yaptw-wa16.000webhostapp.com/update_carpool_minus.php";
    private String GET_PASSENGER_URL = "https://yaptw-wa16.000webhostapp.com/select_passengers_where.php";
    private String SET_AVAILABILITY_URL = "https://yaptw-wa16.000webhostapp.com/update_carpool_availability.php";
    private String RoomID, Driver, Charges, Slot, FromLocation, ToLocation, CreatedTime, Note, isStart;
    private View ratingview;
    private String URL_UPDATE_RATING = "https://yaptw-wa16.000webhostapp.com/update_account_rating.php";
    private String URL_CARS = "https://yaptw-wa16.000webhostapp.com/select_driver_car.php";
    private RatingBar ratingbar;

    LinearLayout LL;

    private String CurrentSlot;

    private boolean isHost;

    private String StudentName, StudentID;

    private boolean checkTrafficInfo, checkTrafficPassenger;

    private JsonArrayRequest jsonInfoRequest;
    private JsonArrayRequest jsonPassengerRequest;
    private JsonArrayRequest jsonCarInfoRequest;
    private JsonObjectRequest jsonratingRequest;

    private Button startButton;
    private Button confirmbutton;

    private boolean showStartJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_pool_room);
        Bundle bundle = getIntent().getExtras();
        RoomID = bundle.getString("RoomID");
        isHost = bundle.getBoolean("isHost");
        textViewDriver = findViewById(R.id.output_F_DriverName);
        textViewCharges = findViewById(R.id.output_Charges);
        textViewSlot = findViewById(R.id.output_F_Slot);
        textViewFrom = findViewById(R.id.output_From);
        textViewTo = findViewById(R.id.output_To);
        textViewCreatedTime = findViewById(R.id.output_DepartTime);
        textViewNote = findViewById(R.id.output_Note);
        LL = findViewById(R.id.output_PassengerList);
        startButton = findViewById(R.id.input_StartTrip);
        ratingbar = findViewById(R.id.ratingBar);
        ratingview = findViewById((R.id.ratingView));
        confirmbutton = findViewById((R.id.ConfirmRating));
        isStart = "false";
        showStartJourney = false;
        SharedPreferences prefs = getSharedPreferences("PrefText", MODE_PRIVATE);
        StudentName = prefs.getString("StudentName", "No name defined");//"No name defined" is the default value.
        StudentID = prefs.getString("StudentID", "No ID defined"); //0 is the default value.

        tvColour = findViewById((R.id.textViewColour));
        tvPlateNo = findViewById((R.id.textViewPlateNo));
        tvModel = findViewById((R.id.textViewModel));

        checkTrafficInfo = true;
        checkTrafficPassenger = true;

        if (!isHost)
            startButton.setEnabled(false);

        jsonInfoRequest = new JsonArrayRequest(
                GET_URL + "?RoomID=" + RoomID,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() != 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject RoomRequest = (JSONObject) response.get(i);
                                    Driver = RoomRequest.getString("Driver");
                                    Charges = RoomRequest.getString("Charges");
                                    Slot = RoomRequest.getString("Slot");
                                    CurrentSlot = RoomRequest.getString("CurrentSlot");
                                    FromLocation = RoomRequest.getString("FromLocation");
                                    ToLocation = RoomRequest.getString("ToLocation");
                                    CreatedTime = RoomRequest.getString("CreatedTime");
                                    Note = RoomRequest.getString("Note");
                                    isStart = RoomRequest.getString("isStart");
                                }
                                textViewDriver.setText(Driver);
                                textViewCharges.setText("RM" + Charges);
                                textViewSlot.setText(CurrentSlot + "/" + Slot);
                                textViewFrom.setText(FromLocation);
                                textViewTo.setText(ToLocation);
                                textViewNote.setText(Note);
                                textViewCreatedTime.setText(CreatedTime);
                            } else {
                                finish();
                                Toast.makeText(getApplicationContext(), "Room Closed", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        checkTrafficInfo = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        jsonInfoRequest.setTag("Info");

        jsonPassengerRequest = new JsonArrayRequest(
                GET_PASSENGER_URL + "?RoomID=" + RoomID,
                new Response.Listener<JSONArray>() {
                    String StudentName;
                    FragmentManager fragmentManager;
                    FragmentTransaction fragmentTransaction;

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            LL.removeAllViews();
                            if (response.length() != 0) {
                                fragmentManager = getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject RoomRequest = (JSONObject) response.get(i);
                                    StudentName = RoomRequest.getString("StudentName");

                                    Fragment fragment = new passengerfragment(StudentName);

                                    fragmentTransaction.add(R.id.output_PassengerList, fragment, StudentName);
                                }
                                fragmentTransaction.commit();
                            }
                        } catch (Exception e) {
                        }
                        checkTrafficPassenger = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });

        jsonPassengerRequest.setTag("Passenger");

        jsonCarInfoRequest = new JsonArrayRequest(
                URL_CARS + "?DriverID=" + RoomID,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            if (response.length() != 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject CarRequest = (JSONObject) response.get(i);
                                    tvPlateNo.setText(CarRequest.getString("CarPlateNo"));
                                    tvModel.setText(CarRequest.getString("CarModel"));
                                    tvColour.setText(CarRequest.getString("Colour"));
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        jsonCarInfoRequest.setTag("CarInfo");
    }

    public void startJourney(View view) {
        startButton.setEnabled(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SET_AVAILABILITY_URL + "?RoomID=" + RoomID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        // Add the request to the RequestQueue.
        NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkTrafficInfo && checkTrafficPassenger && isStart.equals("false")) {
                    checkTrafficInfo = false;
                    checkTrafficPassenger = false;
                    updateInfo();
                }
                if (!isStart.equals("false") && showStartJourney == false) {

                    Toast.makeText(getApplicationContext(), "The trip has started", Toast.LENGTH_LONG).show();
                    showStartJourney = true;
                    if(!isHost) {
                        ratingview.setVisibility(View.VISIBLE);
                    }

                }
                handler.postDelayed(this, 300);
            }
        }, 300);

    }

    private void updateInfo() {

        NetworkCalls.getInstance().addToRequestQueue(jsonInfoRequest);
        NetworkCalls.getInstance().addToRequestQueue(jsonCarInfoRequest);
        NetworkCalls.getInstance().addToRequestQueue(jsonPassengerRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTrafficInfo = true;
        checkTrafficPassenger = true;
    }

    private void deleteData() {
        if (isHost) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DELETE_URL_HOST + "?RoomID=" + RoomID, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

            // Add the request to the RequestQueue.
            NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DELETE_URL_PASSENGER + "?RoomID=" + RoomID + "&StudentID=" + StudentID, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

            // Add the request to the RequestQueue.
            NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteData();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                deleteData();
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkCalls.getInstance().getRequestQueue().cancelAll("Info");
        NetworkCalls.getInstance().getRequestQueue().cancelAll("Passenger");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteData();
    }

    public void ratingConfirmButton(View view) {
        jsonratingRequest = new JsonObjectRequest(Request.Method.POST, URL_UPDATE_RATING + "?RoomID=" + RoomID + "&Rating=" + ratingbar.getRating(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        NetworkCalls.getInstance().addToRequestQueue(jsonratingRequest);
        ratingview.setVisibility(View.INVISIBLE);
    }
}
