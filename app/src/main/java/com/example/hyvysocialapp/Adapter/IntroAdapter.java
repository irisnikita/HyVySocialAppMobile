package com.example.hyvysocialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hyvysocialapp.MainActivity;
import com.example.hyvysocialapp.R;
import com.example.hyvysocialapp.screenitem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class IntroAdapter extends PagerAdapter {
    Context mcontext;
    List<screenitem> screenitems;


    public IntroAdapter(Context mcontext, List<screenitem> screenitems) {
        this.mcontext = mcontext;
        this.screenitems = screenitems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(mcontext.LAYOUT_INFLATER_SERVICE);
        View layoutscreen = inflater.inflate(R.layout.layout_screen,null);
        ImageView imageView = layoutscreen.findViewById(R.id.imageViewInro);


        TextView textViewtitle = layoutscreen.findViewById(R.id.textView_title);
        TextView textViewdecrep =layoutscreen.findViewById(R.id.textView_detail);
        textViewtitle.setText(screenitems.get(position).getTitle());
        textViewdecrep.setText(screenitems.get(position).getDescreption());
        imageView.setImageResource(screenitems.get(position).getScreenimg());
        container.addView(layoutscreen);
        return layoutscreen;

    }

    @Override
    public int getCount() {
        return screenitems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
