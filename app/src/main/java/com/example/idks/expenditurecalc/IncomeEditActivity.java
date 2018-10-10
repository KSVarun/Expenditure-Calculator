package com.example.idks.expenditurecalc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class IncomeEditActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser currentUser;
    private String mIncomeName,mTimestamp,mFullDesc;
    private int mIncomeValue;
    TextView mTIncomeName,mTIncomeValue,mTTimestamp,mTFullDesc;
    private DatabaseReference mDatabase;
    private String uid,key;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.income_edit_layout);


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        //getting references
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Salaries").child(uid);
        mDatabase.keepSynced(true);


        key = getIntent().getStringExtra("key");

        mIncomeName = getIntent().getStringExtra("incomeDesc");
        mIncomeValue = getIntent().getIntExtra("incomeValue",0);
        mTimestamp = getIntent().getStringExtra("timestamp");
        mFullDesc = getIntent().getStringExtra("fullDesc");

        mTIncomeName = (TextView) findViewById(R.id.incomeDesc);
        mTIncomeValue = (TextView) findViewById(R.id.incomeValue);
        mTTimestamp = (TextView) findViewById(R.id.incomeDate);
        mTFullDesc = (TextView) findViewById(R.id.fullDesc);

        mTIncomeName.setText(mIncomeName);
        mTIncomeValue.setText(mIncomeValue+"");
        mTTimestamp.setText(mTimestamp);
        mTFullDesc.setText(mFullDesc);
        //mTFullDesc.setText(mDatabase.child(key).child("fullDescription").toString());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                final Query deleteQuery = mDatabase.child(key);

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        deleteQuery.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                /*Intent nIntent = new Intent(this,MainActivity.class);
                nIntent.putExtra("fragmentNumber",2);
                startActivity(nIntent);*/
                finish();
                return true;

            case R.id.action_update:

                String updatedExpDesc = mTIncomeName.getText().toString();
                String updatedExpValue = mTIncomeValue.getText().toString();
                String fullDesc = mTFullDesc.getText().toString();

                if(TextUtils.isEmpty(updatedExpDesc)){
                    mTIncomeName.setError("Required");
                }
                else if(TextUtils.isEmpty(updatedExpValue)){
                    mTIncomeValue.setError("Required");
                }
                else {
                    mDatabase.child(key).child("description").setValue(updatedExpDesc);
                    mDatabase.child(key).child("value").setValue(updatedExpValue);
                    mDatabase.child(key).child("fullDescription").setValue(fullDesc);

                /*Intent newIntent = new Intent(this,MainActivity.class);
                newIntent.putExtra("fragmentNumber",2);
                startActivity(newIntent);*/
                    finish();
                }
                return true;

            //to go back to the same fragment from where this activity was was called after pressing the back button in the action bar
            case android.R.id.home:
                super.onBackPressed();
                finish();
                return true;

            default:break;

        }

        return super.onOptionsItemSelected(item);
    }
}
