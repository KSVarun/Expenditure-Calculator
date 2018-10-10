package com.example.idks.expenditurecalc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by idks on 3/16/2018.
 */

public class popExp extends Activity {

    static int i = 0;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    TextView expenditure,expenditureDescription;
    private String expenditureString,expenditureDescString;
    SharedPreferences.Editor editor;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exp);

        editor = getSharedPreferences("com.expenditureCalcExp", MODE_PRIVATE).edit();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Expenditures").child(uid);

        FirebaseDatabase.getInstance().getReference().child("Expenditures").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() == null){
                  //  Toast.makeText(getApplicationContext(),"it null",Toast.LENGTH_SHORT).show();
                    editor.putInt("i",0);
                    editor.apply();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        SharedPreferences prefs = getSharedPreferences("com.expenditureCalcExp", MODE_PRIVATE);
        i = prefs.getInt("i",0);

        expenditure = (TextView) findViewById(R.id.expField);
        expenditureDescription= (TextView) findViewById(R.id.expDescField);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.45));


    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    public String randomNoGen(){
        String iS="expenditure";
        i = ++i;
        /*SharedPreferences.Editor editor = getSharedPreferences("com.expenditureCalcExp", MODE_PRIVATE).edit();*/
        editor.putInt("i",i);
        editor.apply();
        return iS+(i);
    }

    public void addExp(View view){

        String iString = randomNoGen();
        expenditureDescString = expenditureDescription.getText().toString();
        expenditureString = expenditure.getText().toString();

        if(TextUtils.isEmpty(expenditureDescString)){
            expenditureDescription.setError("Required");
        }
        else if(TextUtils.isEmpty(expenditureString)){
            expenditure.setError("Required");
        }
        else{
            mDatabase.child(iString).child("description").setValue(expenditureDescString);
            mDatabase.child(iString).child("value").setValue(expenditureString);
            mDatabase.child(iString).child("timestamp").setValue(ServerValue.TIMESTAMP);
            finish();
        }
    }

}
