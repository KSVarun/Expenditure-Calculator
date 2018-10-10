package com.example.idks.expenditurecalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.zip.Inflater;

/**
 * Created by idks on 3/15/2018.
 */

public class incomeFragment extends Fragment {
    private static final String TAG = "tab1_fragment";
    Button addSal;
    TextView salaryView;
    String strSalary;
    int finalSalary;
    private DatabaseReference mDatabase;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_salary, container, false);



        mRecyclerView = view.findViewById(R.id.income_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Salaries").child(uid);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), popSal.class);
                getActivity().startActivity(myIntent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query sort = mDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Salaries, IncomeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Salaries, IncomeViewHolder>(
                Salaries.class,
                R.layout.income_layout,
                IncomeViewHolder.class,
                //mDatabase,
                sort

        ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder,final Salaries model, int position) {
                viewHolder.setSalaryDesc(model.getIncomeName());
                viewHolder.setSalaryValue(model.getSalary());
                final String mTimestamp = viewHolder.getDataTimeStamp(model.getTimestamp());

                final String key = this.getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Log.i("check2",key);

                        Intent intent = new Intent(getActivity(),IncomeEditActivity.class);
                        intent.putExtra("incomeDesc",model.getIncomeName());
                        intent.putExtra("incomeValue",Integer.parseInt(model.getSalary()));
                        intent.putExtra("key",key);
                        intent.putExtra("timestamp",mTimestamp);
                        intent.putExtra("fullDesc",model.getFullDescription());
                        startActivity(intent);



                        // Log.i("checktime",mTimestamp);
                        //    Log.i("check1",model.getSalary());
                        //Log.i("checkthis",model.getSalary()+"this is the model name");
                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public IncomeViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setSalaryDesc(String desc) {
            TextView mIncomeDesc = mView.findViewById(R.id.salaryDesc);
            mIncomeDesc.setText(desc);
        }

        public void setSalaryValue(String value) {
            TextView mIncomeValue = mView.findViewById(R.id.salaryValue);
            mIncomeValue.setText(value);
        }
        public String getDataTimeStamp(long timestamp){
            java.util.Date time=new java.util.Date(timestamp);
            SimpleDateFormat pre = new SimpleDateFormat("EEE MMM dd HH:mm");
            //Hear Define your returning date formate
            return pre.format(time);
        }
    }
}
