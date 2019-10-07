package com.example.hyvysocialapp.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.hyvysocialapp.Adapter.AdapterUser;
import com.example.hyvysocialapp.DashboardActivity;
import com.example.hyvysocialapp.IntroActivity;
import com.example.hyvysocialapp.Model.ModelUsers;
import com.example.hyvysocialapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUsers> usersList;
    ImageView imageload;
    //firebase auth
    FirebaseAuth firebaseAuth;
    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //Khai Báo
        firebaseAuth =FirebaseAuth.getInstance();
        //Khởi tạo recylerview
        recyclerView = view.findViewById(R.id.users_reycylerView);
        //Cài đặt recylerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Khởi tạo user list
        usersList = new ArrayList<>();
        //Lấy tất cả danh sách
        getAllUsers();

        return  view;
    }
    private void getAllUsers(){
        //Lấy người dùng hiện tại
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        //Lấy danh sách user từ Users từ firebase database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //Lấy tất cả users từ database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String typingStatus =""+snapshot.child("typingTo").getValue();
                    ModelUsers user= snapshot.getValue(ModelUsers.class);
                    //Lấy tất cả danh sách ngoại trừ tài khoản đang đăng nhập
                    if(!user.getUid().equals(fuser.getUid())){
                        usersList.add(user);

                    }
                }
                //adapter
                adapterUser=new AdapterUser(getActivity(),usersList);
                //Truyền adapter to recycler view
                recyclerView.setAdapter(adapterUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void searchUser(final String querry) {
        //Lấy người dùng hiện tại
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        //Lấy danh sách user từ Users từ firebase database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //Lấy tất cả users từ database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ModelUsers user= snapshot.getValue(ModelUsers.class);
                    //Lấy tất cả danh sách ngoại trừ tài khoản đang đăng nhập
                    if(!user.getUid().equals(fuser.getUid())){
                        if(user.getName().toLowerCase().contains(querry.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(querry.toLowerCase())){
                            usersList.add(user);
                        }

                    }
                }
                //adapter
                adapterUser=new AdapterUser(getActivity(),usersList);
                //refesh adapter
                adapterUser.notifyDataSetChanged();
                //Truyền adapter to recycler view
                recyclerView.setAdapter(adapterUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        //SearchView
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView =(SearchView) MenuItemCompat.getActionView(item);
        super.onCreateOptionsMenu(menu,menuInflater);
        //SearchListener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //được gọi khi người dùng nhấn submit
                if(!TextUtils.isEmpty(s.trim())){
                        searchUser(s);
                }
                else {
                    //nếu không nhấn lấy tất cả danh sách
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                //được gọi khi người dùng nhấn submit
                if(!TextUtils.isEmpty(s.trim())){
                    searchUser(s);
                }
                else {
                    //nếu không nhấn lấy tất cả danh sách
                    getAllUsers();
                }
                return false;
            }
        });
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
//Model Class for recylerView
