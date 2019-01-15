package my.edu.taruc.assignment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


public class passengerfragment extends Fragment {
    TextView StudentName;
    String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passengerfragment, container, false);
    }

    @SuppressLint("ValidFragment")
    passengerfragment(){
    }

    @SuppressLint("ValidFragment")
    passengerfragment(String studentName){
        name = studentName;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StudentName=getView().findViewById(R.id.output_PF_Name);
        StudentName.setText(name);
    }
}
