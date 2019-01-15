package my.edu.taruc.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PassengerSearchPath extends AppCompatActivity {

    Spinner From,To;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_search_path);

        From = findViewById(R.id.input_FromP);
        To = findViewById(R.id.input_ToP);
    }

    public void toFoundPath(View view){
        if(From.getSelectedItemPosition()!=0 && To.getSelectedItemPosition()!=0 && From.getSelectedItemPosition()!= To.getSelectedItemPosition()) {
            Intent newIntent = new Intent(this, PassengerFoundPath.class);

            newIntent.putExtra("From", From.getSelectedItem().toString());
            newIntent.putExtra("To", To.getSelectedItem().toString());

            this.startActivity(newIntent);
        }else{
            Toast.makeText(getApplicationContext(),"Please select correct location",Toast.LENGTH_LONG).show();
        }
    }
}
