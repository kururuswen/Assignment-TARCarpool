package my.edu.taruc.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.net.URI;
import java.sql.Driver;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void toDriver(View view){
        Intent goDriver = new Intent(this,DriverCreate.class);
        this.startActivity(goDriver);
    }

    public void toPassenger(View view){
        Intent goPassenger = new Intent(this,PassengerSearchPath.class);
        this.startActivity((goPassenger));
    }

    public void Logout(View view){
        SharedPreferences prefs =   getApplicationContext().getSharedPreferences("PrefText", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        NavUtils.navigateUpFromSameTask(this);
    }

    public void goAbout(View view){
        Intent newIntent = new Intent(this,AboutPage.class);
        this.startActivity(newIntent);
    }

    public void goContactUs(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"yaptw-wa16@student.tarc.edu.my"});
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAffinity();
                break;
        }
        return true;
    }
}
