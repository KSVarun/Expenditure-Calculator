package com.example.idks.expenditurecalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by idks on 3/14/2018.
 */

public class loginActivity extends Activity{

    private CircleImageView mCircleImage;
    FirebaseAuth auth;
    EditText phone,otp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    String verificationCode;
    ProgressBar pb;

    HashMap<String, String> userMap = new HashMap<>();

    FirebaseUser currentUser;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        phone = (EditText) findViewById(R.id.phone_number);
        otp = (EditText) findViewById(R.id.OTP);
        pb = (ProgressBar) findViewById(R.id.progressBar3);



        otp.setVisibility(View.GONE);
        final View v = findViewById(R.id.check_code);
        v.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                pb.setVisibility(View.VISIBLE);
                signIN(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(getApplicationContext(),"Code sent",Toast.LENGTH_SHORT).show();
                otp.setVisibility(View.VISIBLE);
                v.setVisibility(View.VISIBLE);
            }
        };
    }



    public void send_OTP(View v){

        String number = phone.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS,this,mCallBack
        );


    }


    public void signIN(PhoneAuthCredential cred){

        auth.signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = mCurrentUser.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    userMap.put("name", "");
                    userMap.put("image", "default_image");
                    userMap.put("thumbnail", "default_thumbnail");

                    mDatabase.setValue(userMap);
                    Intent intent = new Intent(loginActivity.this, initialProfileSettingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }

    public void verify(View v){
        pb.setVisibility(View.VISIBLE);
        String inputCode = otp.getText().toString();
        verifyPhoneNumber(verificationCode,inputCode);
    }

    public void verifyPhoneNumber(String vc,String ic){
        PhoneAuthCredential cred = PhoneAuthProvider.getCredential(vc,ic);
        signIN(cred);
    }

}
