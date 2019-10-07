package com.example.hyvysocialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hyvysocialapp.Adapter.IntroAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Views
    Button mRegisterBtn,mLoginBtn;
    Animation animation;
    ImageView imageView;
    TextView textlogo;
    IntroAdapter introviewpageAdapters ;
    private ViewPager screenPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //
        animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_anim);
        //Chuyền tham chiếu vào view
        mRegisterBtn =findViewById(R.id.register_btn);
        mRegisterBtn.setAnimation(animation);
        mLoginBtn= findViewById(R.id.login_btn);
        mLoginBtn.setAnimation(animation);
        //

        //Tạo sự kiện khi nhấn button
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở activity register
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
        //tạo sự kiện khi nhấn button login
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mở login activity
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }
}
