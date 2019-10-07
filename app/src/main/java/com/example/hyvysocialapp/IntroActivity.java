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

import com.example.hyvysocialapp.Adapter.IntroAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    FirebaseAuth.AuthStateListener authStateListener;
    private ViewPager screenPager;
    IntroAdapter introviewpageAdapters ;
    TabLayout tab;
    Button buttonnext;
    Button getstart;
    Animation animation;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ConstraintLayout constraintLayout =findViewById(R.id.layoutintro);
        AnimationDrawable animationDrawable =(AnimationDrawable)constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        tab =findViewById(R.id.tabLayout);
        buttonnext =findViewById(R.id.button_next);
        getstart=findViewById(R.id.buttonstart);
        animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_anim);
        final List<screenitem> mlist=new ArrayList<>();
        mlist.add(new screenitem("KẾT NỐI TOÀN CẦU","Kết nối bạn bè bốn phương , chia sẻ những khoảng khắc đẹp",R.drawable.fragment1));
        mlist.add(new screenitem("CHÁT CÙNG CRUSH","Tám thả ga, đụng đâu tám đó",R.drawable.fragment2));
        mlist.add(new screenitem("CHIA SẺ MỌI THỨ","Tâm trạng , hình ảnh , cảm xúc . Mọi thứ đều có thể chia sẻ !",R.drawable.fragment3));
        //cai dat view pager
        screenPager =findViewById(R.id.viewPager);
        introviewpageAdapters = new IntroAdapter(this,mlist);
        screenPager.setAdapter(introviewpageAdapters);
        //Cai dat tablayout
        tab.setupWithViewPager(screenPager);
        //Sự kiện khi nhấn bút bắt đầu
        getstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this,MainActivity.class));
                finish();
            }
        });
        //su kien khi nhan nut button next
        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = screenPager.getCurrentItem();
                if(position<mlist.size())
                {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if(position==mlist.size()-1)
                {
                    lastscreen();
                }
            }
        });
        //Bắt sự kiện khi lướt view pager
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==mlist.size()-1)
                {
                    lastscreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }
    private  void lastscreen(){
        buttonnext.setVisibility(View.INVISIBLE);
        getstart.setVisibility(View.VISIBLE);
        tab.setVisibility(View.INVISIBLE);
        //Tạo hiêu ứng cho button start
        getstart.setAnimation(animation);
    }
}
