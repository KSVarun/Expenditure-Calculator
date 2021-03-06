package com.example.idks.expenditurecalc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.Manifest;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser currentUser;
    private static final int PERMISSIONS_REQUEST = 100;

    private SectionPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public int backCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backCount = 0;

        auth = FirebaseAuth.getInstance();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new incomeFragment(),"Income");

        adapter.addFragment(new expenditureFragment(),"Expenditure");
       // adapter.addFragment(new statFragment(),"Stat");

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);

        //to point to a particular fragment when coming back from a activity
        /*if(getIntent().getIntExtra("fragmentNumber",0)==2){

            //     adapter.addFragment(new expenditureFragment(),"Expenditure");

            mViewPager.setCurrentItem(1);
        }*/


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);




    }

    @Override
    public void onStart(){
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this,loginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                auth.signOut();
                startActivity(new Intent(MainActivity.this,loginActivity.class));
                finish();
                return true;
            case R.id.action_settings_account:
                startActivity(new Intent(MainActivity.this,accountSettings.class));
                return true;
            default:break;

        }

        return super.onOptionsItemSelected(item);
    }

   @Override
    public void onBackPressed(){
       if(backCount>0){
           finish();
       }
       else {
           Toast.makeText(getApplicationContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();
           backCount++;

           new CountDownTimer(2000, 1000) {
               @Override
               public void onTick(long l) {

               }

               @Override
               public void onFinish() {
                   backCount = 0;
               }
           }.start();
       }
   }
}
