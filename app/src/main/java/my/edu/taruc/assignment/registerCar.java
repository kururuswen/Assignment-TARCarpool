package my.edu.taruc.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class registerCar extends AppCompatActivity {
    private TextView plateNumber, model, colour;
    private String URL_SAVE = "https://yaptw-wa16.000webhostapp.com/add_new_car.php";
    private String DriverID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        plateNumber = findViewById(R.id.editTextPlateNumber);
        model = findViewById(R.id.editTextModel);
        colour = findViewById(R.id.editTextColour);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PrefText", MODE_PRIVATE);
        DriverID = prefs.getString("StudentID", "No ID defined"); //0 is the default value.
    }

    private Intent newIntent;

    public void registerNewCar(View view) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setMessage("Registering Car...");
        mDialog.show();

        saveData();

        newIntent = new Intent(this, DriverCreate.class);
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
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                                finish();
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
                    params.put("DriverID", DriverID);
                    params.put("CarPlateNo", plateNumber.getText().toString());
                    params.put("CarModel", model.getText().toString());
                    params.put("Colour", colour.getText().toString());
                    params.put("Availability", "1");
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
