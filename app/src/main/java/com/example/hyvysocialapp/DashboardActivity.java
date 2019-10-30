package com.example.hyvysocialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.hyvysocialapp.Fragments.ChatListFragment;
import com.example.hyvysocialapp.Fragments.HomeFragment;
import com.example.hyvysocialapp.Fragments.ProfileFragment;
import com.example.hyvysocialapp.Fragments.UserFragment;
import com.example.hyvysocialapp.Nofication.Token;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    //firebase auth
    FirebaseAuth firebaseAuth;

    String myUid;
    ActionBar actionBar;
    Dialog popAddpost;

    //
    TextView mProfileTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //Khai Báo action bar và tựa đề
        actionBar =getSupportActionBar();
        actionBar.setTitle("Thông tin");

        //Khai Báo
        firebaseAuth =FirebaseAuth.getInstance();

        //Khai báo views
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
        //Fragment home là trang chính khi  tạo activity
        actionBar.setTitle("Home");
        HomeFragment fragment1 =new HomeFragment();
        FragmentTransaction ft1= getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();

        checkUserSatus();

    }
    public void updateToken(String token){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(myUid).setValue(mToken);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    HomeFragment fragment1 =new HomeFragment();
                    FragmentTransaction ft1= getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content,fragment1,"");
                    ft1.commit();
                    return true;
                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment fragment2 =new ProfileFragment();
                    FragmentTransaction ft2= getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,fragment2,"");
                    ft2.commit();
                    return true;
                case R.id.nav_users:
                    actionBar.setTitle("Users");
                    UserFragment fragment3 =new UserFragment();
                    FragmentTransaction ft3= getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content,fragment3,"");
                    ft3.commit();
                    return true;
                case R.id.nav_chat:
                    actionBar.setTitle("Users");
                    ChatListFragment fragment4 =new ChatListFragment();
                    FragmentTransaction ft4= getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.content,fragment4,"");
                    ft4.commit();
                    return true;
            }
            return false;
        }
    };

//    private void checkUserSatus() {
//        //lấy thông tin người dùng hiện tại
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user != null)
//        { //Người dùng đăng nhập sẽ ở màn hình này
//
//        }
//        else{
//            //Người dùng chưa đăng nhập sẽ trờ về màn hình chính
//            startActivity(new Intent(DashboardActivity.this,IntroActivity.class));
//            finish();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        checkUserSatus();
//        //set online
//        super.onStart();
//    }
private void checkUserSatus() {
    //lấy thông tin người dùng hiện tại
    FirebaseUser user = firebaseAuth.getCurrentUser();
    if(user != null)
    { //Người dùng đăng nhập sẽ ở màn hình này
        myUid=user.getUid();

        SharedPreferences sp =getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("Current_USERID",myUid);
        editor.apply();

        //        update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }
    else{
        //Người dùng chưa đăng nhập sẽ trờ về màn hình chính
        startActivity(new Intent(DashboardActivity.this, IntroActivity.class));
        finish();
    }
}

    private void checkOnlineStatus(String status){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("onlineStatus",status);

        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //get timestamp
        String timeStamp = String.valueOf(System.currentTimeMillis());

        checkOnlineStatus(timeStamp);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
//        checkUserSatus();
        super.onResume();
    }

    @Override
    protected void onStart() {
        checkUserSatus();
        //set online
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
