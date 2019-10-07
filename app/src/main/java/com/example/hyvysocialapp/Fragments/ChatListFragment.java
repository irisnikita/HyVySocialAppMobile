package com.example.hyvysocialapp.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.hyvysocialapp.IntroActivity;
import com.example.hyvysocialapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //Khai Báo
        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //Hiển thị menu option ở fragment
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    /*inflate option menu*/


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main,menu);

        //ẩn item post
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    /*handle menu item click*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserSatus();
        }

        return super.onOptionsItemSelected(item);
    }
    private void checkUserSatus() {
        //lấy thông tin người dùng hiện tại
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        { //Người dùng đăng nhập sẽ ở màn hình này

        }
        else{
            //Người dùng chưa đăng nhập sẽ trờ về màn hình chính
            startActivity(new Intent(getActivity(), IntroActivity.class));
            getActivity().finish();
        }
    }

}
