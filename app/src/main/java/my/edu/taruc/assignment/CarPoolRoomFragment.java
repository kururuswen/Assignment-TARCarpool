package my.edu.taruc.assignment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


public class CarPoolRoomFragment extends Fragment {
    private TextView Driver,Slot,Charge,Note,DepartTime;
    private String RoomID,SDriver,SSlot,SCharge,SNote,DTime;
    private int color;
    private String GET_URL = "https://yaptw-wa16.000webhostapp.com/select_carpool_where.php";
    private String UPDATE_URL = "https://yaptw-wa16.000webhostapp.com/update_carpool_add.php";
    private String StudentName,StudentID;
    private String GETrating_URL = "https://yaptw-wa16.000webhostapp.com/select_account.php";
    private TextView ratingPercent;
    private TextView driver;

    private JsonArrayRequest jsoncheckSlotRequest;
    private JsonObjectRequest jsoninsertRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_pool_room, container, false);

        SharedPreferences prefs =   getActivity().getSharedPreferences("PrefText", MODE_PRIVATE);
        StudentName = prefs.getString("StudentName", "No name defined");//"No name defined" is the default value.
        StudentID = prefs.getString("StudentID", "No ID defined"); //0 is the default value.


        jsoncheckSlotRequest = new JsonArrayRequest(
                GET_URL+"?RoomID="+RoomID,
                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            int CurrentSlot = 0,MaxSlot = 0;
                                            for (int i = 0; i < response.length(); i++) {
                                                JSONObject RoomRequest = (JSONObject) response.get(i);
                                                MaxSlot = RoomRequest.getInt("Slot");
                                                CurrentSlot = RoomRequest.getInt("CurrentSlot");
                            }
                            if(CurrentSlot<MaxSlot) {
                                NetworkCalls.getInstance().addToRequestQueue(jsoninsertRequest);
                                Intent intent = new Intent(getActivity(), CarPoolRoom.class);
                                intent.putExtra("RoomID",RoomID);
                                intent.putExtra("isHost", false);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(), "The room is full", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Set the tag on the request.
        jsoncheckSlotRequest.setTag("carpooltarc");

        jsoninsertRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_URL + "?RoomID=" + RoomID+"&StudentID="+StudentID+"&StudentName="+StudentName.replaceAll("\\s+","+"), null,
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

        Button button = (Button) rootView.findViewById(R.id.input_joinRoom);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Add the request to the RequestQueue.
                NetworkCalls.getInstance().addToRequestQueue(jsoncheckSlotRequest);
            }
        });
        return rootView;
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_car_pool_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Driver =  getView().findViewById(R.id.output_F_DriverName);
        Slot = getView().findViewById(R.id.output_F_Slot);
        Charge = getView().findViewById(R.id.output_F_Charge);
        Note = getView().findViewById(R.id.output_F_Note);
        DepartTime = getView().findViewById(R.id.output_F_DTime);
        ratingPercent = getView().findViewById(R.id.ratePercentage);

        Driver.setText(SDriver);
        Slot.setText(SSlot);
        Charge.setText(SCharge);
        Note.setText(SNote);
        DepartTime.setText(DTime);
        getView().setBackgroundColor(color);

        percentage();
    }

    public void setColor(int bgColor){
        color = bgColor;
    }

    public CarPoolRoomFragment(){

    }

    @SuppressLint("ValidFragment")
    public CarPoolRoomFragment(String IRoomID, String IDriver, String ISlot, String ICharge, String INote,String IDTime){
        RoomID = IRoomID;
        SDriver = IDriver;
        SSlot = ISlot;
        SCharge = ICharge;
        SNote = INote;
        DTime = IDTime;
        color = Color.WHITE;
    }


    public void percentage(){
        JsonArrayRequest jsonPercentageRequest = new JsonArrayRequest(
                GETrating_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject accountRequest = (JSONObject) response.get(i);
                                String ID = accountRequest.getString("StudentID");
                                double Rating = accountRequest.getDouble("Rating");
                                int RatingCount = accountRequest.getInt("RatingCount");
                                if(ID.equals(RoomID)) {
                                    double percentage = Rating/(RatingCount*5)*100;
                                    int RoundPercentage = (int)percentage;
                                    ratingPercent.setText(String.valueOf(RoundPercentage)+"%");
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
        NetworkCalls.getInstance().addToRequestQueue(jsonPercentageRequest);
    }
}
