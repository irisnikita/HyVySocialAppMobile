package com.example.hyvysocialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyvysocialapp.Adapter.AdapterPosts;
import com.example.hyvysocialapp.Model.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth ;

    RecyclerView postsRecylerView;

    //views
    ImageView avatarIv,coverIv;
    TextView nameTv,emailTv,phoneTv;

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        avatarIv =findViewById(R.id.avatarIv);
        nameTv =findViewById(R.id.nameTv);
        coverIv=findViewById(R.id.coverIv);
        emailTv =findViewById(R.id.EmailTv);
        phoneTv =findViewById(R.id.PhoneTv);

        postsRecylerView=findViewById(R.id.recyclerview_posts);

        firebaseAuth =FirebaseAuth.getInstance();

        Intent intent =getIntent();
        uid =intent.getStringExtra("uid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Kiểm tra cho đến khi nhận được giá trị
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //Lấy dữ liệu
                    String name = ""+snapshot.child("name").getValue();
                    String email = ""+snapshot.child("email").getValue();
                    String phone = ""+snapshot.child("phone").getValue();
                    String image = ""+snapshot.child("image").getValue();
                    String cover = ""+snapshot.child("cover").getValue();
                    //Truyền dữ liệu
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    try{
                        //Nết ảnh nhận được thì set vào avatarIv
                        Picasso.get().load(image).into(avatarIv);
                    }
                    catch (Exception e){
                        //Nếu không thì để ảnh mặc định
                        Picasso.get().load(R.drawable.ic_useravatar).into(avatarIv);
                    }
                    try{
                        //Nết ảnh nhận được thì set vào coverIv
                        Picasso.get().load(cover).into(coverIv);
                    }
                    catch (Exception e){

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postList =new ArrayList<>();

        checkUserSatus();
        loadHisPosts();
    }

    private void loadHisPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecylerView.setLayoutManager(layoutManager);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        //query to load post
        Query query = ref.orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelPost myPosts = snapshot.getValue(ModelPost.class);

                    postList.add(myPosts);

                    //adapter
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this,postList);
                    //set this adapter to recyclerview
                    postsRecylerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchHisPosts(final String searchQuery){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecylerView.setLayoutManager(layoutManager);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        //query to load post
        Query query = ref.orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelPost myPosts = snapshot.getValue(ModelPost.class);

                    if(myPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())
                            ||myPosts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(myPosts);
                    }
                    //adapter
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this,postList);
                    //set this adapter to recyclerview
                    postsRecylerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add).setVisible(false);

        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button
                if(!TextUtils.isEmpty(s)){
                    searchHisPosts(s);
                }
                else {
                    loadHisPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called whenever user type any letter
                if(!TextUtils.isEmpty(s)){
                    searchHisPosts(s);
                }
                else {
                    loadHisPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
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
//            uid = user.getUid();

        }
        else{
            //Người dùng chưa đăng nhập sẽ trờ về màn hình chính
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}