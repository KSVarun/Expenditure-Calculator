package com.example.idks.expenditurecalc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by idks on 3/15/2018.
 */

public class popSal extends Activity {

    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    TextView salary,salaryDescription;
    private String salaryString,salaryDescString;
    SharedPreferences.Editor editor;
    private int salaryInt;
    static int flag=0;
    static int i = 1;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_salary);


        editor = getSharedPreferences("com.expenditureCalcInc", MODE_PRIVATE).edit();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Salaries").child(uid);

        FirebaseDatabase.getInstance().getReference().child("Salaries").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() == null){
                    //  Toast.makeText(getApplicationContext(),"it null",Toast.LENGTH_SHORT).show();
                    editor.putInt("flag",0);
                    editor.putInt("i",0);
                    editor.apply();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        SharedPreferences prefs = getSharedPreferences("com.expenditureCalcInc", MODE_PRIVATE);
        flag = prefs.getInt("flag", 0); //0 is the default value.
        i = prefs.getInt("i",0);

        salary = (TextView) findViewById(R.id.salaryFieldTextView);
        salaryDescription = (TextView) findViewById(R.id.salaryDescFieldTextView);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.45));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Salaries").child(uid);

        if(flag == 0){
            salaryDescription.setText("Primary Income");
        }

    }

    public String randomNoGen(){
        String iS="income";
        i = ++i;
      //  SharedPreferences.Editor editor = getSharedPreferences("com.expenditureCalc", MODE_PRIVATE).edit();
        editor.putInt("i",i);
        editor.apply();
        return iS+(i);
    }

    public void addSal(View view){

        String iString = randomNoGen();

        salaryDescString = salaryDescription.getText().toString();
        salaryString = salary.getText().toString();

        if(flag == 0){
            if(TextUtils.isEmpty(salaryDescString)){
                salaryDescription.setError("Required");
            }
            else if(TextUtils.isEmpty(salaryString)){
                salary.setError("Required");
            }
            else {
                mDatabase.child(iString).child("description").setValue(salaryDescString);
                mDatabase.child(iString).child("value").setValue(salaryString);
                mDatabase.child(iString).child("timestamp").setValue(ServerValue.TIMESTAMP);
                flag = 1;
                SharedPreferences.Editor editor = getSharedPreferences("com.expenditureCalcInc", MODE_PRIVATE).edit();

                editor.putInt("flag", flag);
                editor.commit();
                finish();
            }
        }
        else{
            if(TextUtils.isEmpty(salaryDescString)){
                salaryDescription.setError("Required");
            }
            else if(TextUtils.isEmpty(salaryString)){
                salary.setError("Required");
            }
            else {
                mDatabase.child(iString).child("description").setValue(salaryDescString);
                mDatabase.child(iString).child("value").setValue(salaryString);
                mDatabase.child(iString).child("timestamp").setValue(ServerValue.TIMESTAMP);
                finish();
            }
        }

    }
}
