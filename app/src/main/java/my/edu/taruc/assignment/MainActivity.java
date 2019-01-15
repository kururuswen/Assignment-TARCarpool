package my.edu.taruc.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Stack;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private String GET_URL = "https://yaptw-wa16.000webhostapp.com/select_account.php";
    private String UserID;
    private String Password;
    private String Name;
    private boolean validate = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        NetworkCalls.getInstance().setContext(getApplicationContext());
        NetworkCalls.getInstance().getRequestQueue();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs =   getApplicationContext().getSharedPreferences("PrefText", MODE_PRIVATE);
        String SavedName = prefs.getString("StudentName", null);//"No name defined" is the default value.
        String SavedPassword = prefs.getString("StudentID", null); //0 is the default value.
        if(SavedName != null && SavedPassword != null){
            validate = true;
            validateACC();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public void Login(View view){
        TextView temp = findViewById(R.id.inputUserID);
        UserID = temp.getText().toString();
        temp = findViewById(R.id.inputPassword);
        Password = temp.getText().toString();

        validateACC();
    }

    private void Proceed(){
        progressDialog.dismiss();
        if(validate) {
            Intent Menu = new Intent(this, MainMenu.class);
            this.startActivity(Menu);
        }else{
            Toast.makeText(getApplicationContext(),"Incorrect ID or Password!",Toast.LENGTH_LONG).show();
        }
    }

    private void validateACC(){
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Logging in"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                GET_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject accountRequest = (JSONObject) response.get(i);
                                String ID = accountRequest.getString("StudentID");
                                String Pw = accountRequest.getString("Password");
                                String Namae = accountRequest.getString("StudentName");
                                if(ID.equals(UserID) && Pw.equals(Password)) {
                                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("PrefText", MODE_PRIVATE).edit();
                                    editor.putString("StudentName", Namae);
                                    editor.putString("StudentID", ID);
                                    editor.commit();
                                    Name = Namae;
                                    validate = true;
                                }
                            }
                            Proceed();
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

        // Set the tag on the request.
        jsonObjectRequest.setTag("carpooltarc");

        // Add the request to the RequestQueue.
        NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}
