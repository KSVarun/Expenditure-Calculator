package com.example.idks.expenditurecalc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by idks on 3/15/2018.
 */

public class statFragment extends Fragment {
    private static final String TAG = "tab1_fragment3";
    TextView percentageView;
    double per;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_stats,container,false);
        Button calExpPer = (Button) view.findViewById(R.id.calPer);
        percentageView = (TextView) view.findViewById(R.id.percentage);
        calExpPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*per = Salaries.calcPer();
                percentageView.setText(Double.toString(per));*/

            }
        });
        return view;
    }
}
