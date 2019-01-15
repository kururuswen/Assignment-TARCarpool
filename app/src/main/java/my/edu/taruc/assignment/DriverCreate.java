package my.edu.taruc.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DriverCreate extends AppCompatActivity {

    private Spinner StartPlace, DestinationPlace;
    private TextView AvailableSlot, WaitTime, Charge, Note, CarDetails;
    private String URL_SAVE = "https://yaptw-wa16.000webhostapp.com/insert_carpool.php";
    private String DELETE_URL_HOST = "https://yaptw-wa16.000webhostapp.com/delete_carpool_where.php";
    private String URL_CARS = "https://yaptw-wa16.000webhostapp.com/select_driver_car.php";
    private String DELETE_CAR = "https://yaptw-wa16.000webhostapp.com/delete_car.php";
    private Button RegisterButton, DeleteButton;
    private String StudentName = "";
    private String StudentID = "";
    private boolean carRegistered = false;
    private JsonArrayRequest jsonInfoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_create);
        carRegistered = false;
        StartPlace = findViewById(R.id.Input_SpinnerStart);
        DestinationPlace = findViewById(R.id.Input_SpInnerDestination);
        AvailableSlot = findViewById(R.id.Input_TextSlot);
        WaitTime = findViewById(R.id.Input_TextWaitTime);
        Charge = findViewById(R.id.Input_TextCharge);
        Note = findViewById(R.id.Input_StringNote);
        CarDetails = findViewById(R.id.textViewCar);
        RegisterButton = findViewById(R.id.buttonRegister);
        DeleteButton = findViewById(R.id.buttonDelete);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PrefText", MODE_PRIVATE);
        StudentName = prefs.getString("StudentName", "No name defined");//"No name defined" is the default value.
        StudentID = prefs.getString("StudentID", "No ID defined"); //0 is the default value.
        DeleteButton.setVisibility(View.INVISIBLE);

        jsonInfoRequest = new JsonArrayRequest(
                URL_CARS + "?DriverID=" + StudentID,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            if (response.length() != 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject CarRequest = (JSONObject) response.get(i);
                                    String var = CarRequest.getString("CarPlateNo");
                                    CarDetails.setText(var);
                                    carRegistered = true;
                                    RegisterButton.setVisibility(View.INVISIBLE);
                                    DeleteButton.setVisibility(View.VISIBLE);
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

        jsonInfoRequest.setTag("Info");

    }

    public void goRegisterCar(View view) {
        Intent newIntent = new Intent(this, registerCar.class);
        this.startActivity(newIntent);
    }

    public void deleteCar(View view) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DELETE_CAR + "?DriverID=" + StudentID, null,
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
        finish();
    }

    protected void onStart() {
        super.onStart();
        NetworkCalls.getInstance().addToRequestQueue(jsonInfoRequest);
    }


    private Intent newIntent;
    Toast toast;

    public void createCarPool(View view) {
        if (StartPlace.getSelectedItemPosition() != 0 && DestinationPlace.getSelectedItemPosition() != 0 && DestinationPlace.getSelectedItemPosition() != StartPlace.getSelectedItemPosition() && Integer.parseInt(AvailableSlot.getText().toString()) >= 1 && Integer.parseInt(WaitTime.getText().toString()) >= 1 && Integer.parseInt(Charge.getText().toString()) >= 0 && carRegistered) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setMessage("Loading...");
            mDialog.show();

            saveData();

            newIntent = new Intent(this, CarPoolRoom.class);
            newIntent.putExtra("RoomID", StudentID);
            newIntent.putExtra("isHost", true);
        } else {
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG);
            }

            if (Integer.parseInt(AvailableSlot.getText().toString()) < 1) {
                toast.setText("Please enter a proper  available slot number");
            } else if (Integer.parseInt(WaitTime.getText().toString()) < 1) {
                toast.setText("Please enter a proper  waiting time");
            } else if (Integer.parseInt(Charge.getText().toString()) < 0) {
                toast.setText("Please enter a proper  charge");
            } else if (StartPlace.getSelectedItemPosition() == 0 || DestinationPlace.getSelectedItemPosition() == 0 || DestinationPlace.getSelectedItemPosition() == StartPlace.getSelectedItemPosition()) {
                toast.setText("Please choose a proper location.");
            } else {
                toast.setText("Please register your car.");
            }
            toast.show();
        }
    }

    private void saveData() {
        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    URL_SAVE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {
                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DELETE_URL_HOST + "?RoomID=" + StudentID, null,
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

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    startActivity(newIntent);
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("RoomID", StudentID);
                    params.put("Driver", StudentName);
                    params.put("Charges", Charge.getText().toString());
                    params.put("Slot", AvailableSlot.getText().toString());
                    params.put("CurrentSlot", "0");
                    params.put("FromLocation", StartPlace.getSelectedItem().toString());
                    params.put("ToLocation", DestinationPlace.getSelectedItem().toString());
                    int temp = Integer.parseInt(WaitTime.getText().toString());
                    ;
                    Date currentTime = new Date(Calendar.getInstance().getTimeInMillis() + (temp * 60000));
                    params.put("CreatedTime", new SimpleDateFormat("HH:mm:ss").format(currentTime));
                    params.put("Note", Note.getText().toString());
                    params.put("isStart", "false");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            NetworkCalls.getInstance().addToRequestQueue(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}