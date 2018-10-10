package com.example.idks.expenditurecalc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class accountSettings extends AppCompatActivity {

    private DatabaseReference mDatabase;
    TextView userName;
    HashMap<String, String> userMap = new HashMap<>();
    AlertDialog dialog;
    EditText editText;
    Button changeName;
    FirebaseUser currentUser;
    Toolbar myToolbar;
    public static int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private CircleImageView mDisplayImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //reference to toolbar
        myToolbar = findViewById(R.id.accout_profile_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getting references
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase.keepSynced(true);

        mImageStorage = FirebaseStorage.getInstance().getReference();


        changeName = (Button) findViewById(R.id.nameChange);
        userName = (TextView) findViewById(R.id.profileName);
        mDisplayImage = findViewById(R.id.profilePic);

        //update name dialog
        dialog = new AlertDialog.Builder(this).create();

        editText = new EditText(this);
        dialog.setTitle("Update Name");
        dialog.setView(editText);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userName.setText(editText.getText());

                String uName = editText.getText().toString();
                mDatabase.child("name").setValue(uName);

            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(userName.getText());
                dialog.show();
            }
        });



        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumbNail = dataSnapshot.child("thumbnail").getValue().toString();

                userName.setText(name);
                if(!image.equals("default_image")) {
                    //Picasso.get().load(image).placeholder(R.drawable.account).into(mDisplayImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.account).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.account).into(mDisplayImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void updateImage(View view){
         CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1,1)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Updating Image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMessage("Please wait, Updating in progress!");
                mProgressDialog.show();

                String currentUserId = currentUser.getUid();

                Uri resultUri = result.getUri();

                File thumb_Filepath = new File(resultUri.getPath());

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_Filepath);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filePath = mImageStorage.child("profile_pics").child(currentUserId + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_pics").child("thumbs").child(currentUserId+".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful()){

                                        Map update_imageMap = new HashMap();
                                        update_imageMap.put("image",downloadUrl);
                                        update_imageMap.put("thumbnail",thumb_downloadUrl);

                                        mDatabase.updateChildren(update_imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                    else{

                                    }
                                }
                            });

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
