package my.edu.taruc.assignment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class PassengerFoundPath extends AppCompatActivity {

    private TextView From,To;
    private String GET_URL = "https://yaptw-wa16.000webhostapp.com/select_carpool_where_location.php";
    private String FromLocation,ToLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_found_path);

        Bundle bundle = getIntent().getExtras();
        From = findViewById(R.id.output_Starting);
        To = findViewById(R.id.output_Destination);
        FromLocation = bundle.getString("From");
        ToLocation = bundle.getString("To");
        From.setText(FromLocation);
        To.setText(ToLocation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Refresh(this.getCurrentFocus());
    }

    ProgressDialog progressDialog;
    public void Refresh(View view){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Refreshing"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog

        LinearLayout LL = findViewById(R.id.output_carpoolListContainer);
        LL.removeAllViews();

        JsonArrayRequest jsonObjectRequest;
        jsonObjectRequest = new JsonArrayRequest(
                GET_URL+"?FromLocation="+FromLocation+"&ToLocation="+ToLocation.replaceAll("\\s+","+"),
                new Response.Listener<JSONArray>() {
                    String RoomID,Driver,Charges,Slot,CreatedTime,Note,CurrentSlot;
                    FragmentManager fragmentManager;
                    FragmentTransaction fragmentTransaction;
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            fragmentManager = getSupportFragmentManager();
                            fragmentTransaction = fragmentManager.beginTransaction();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject RoomRequest = (JSONObject) response.get(i);
                                RoomID = RoomRequest.getString("RoomID");
                                Driver = RoomRequest.getString("Driver");
                                Charges = RoomRequest.getString("Charges");
                                Slot = RoomRequest.getString("Slot");
                                CurrentSlot = RoomRequest.getString("CurrentSlot");
                                CreatedTime = RoomRequest.getString("CreatedTime");
                                Note = RoomRequest.getString("Note");

                                Fragment fragment = new CarPoolRoomFragment(RoomID,Driver,CurrentSlot + "/" + Slot,"RM" + Charges,Note,CreatedTime);

                                if(i%2==0)
                                    ((CarPoolRoomFragment) fragment).setColor(Color.GRAY);
                                else
                                    ((CarPoolRoomFragment) fragment).setColor(Color.WHITE);
                                fragmentTransaction.add(R.id.output_carpoolListContainer,fragment,RoomID);
                            }
                            fragmentTransaction.commit();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
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
        // Add the request to the RequestQueue.
        NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
