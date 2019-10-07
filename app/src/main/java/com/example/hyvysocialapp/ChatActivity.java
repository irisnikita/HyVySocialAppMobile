package com.example.hyvysocialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyvysocialapp.Adapter.AdapterChat;
import com.example.hyvysocialapp.Model.ModelChat;
import com.example.hyvysocialapp.Model.ModelUsers;
import com.example.hyvysocialapp.Nofication.APIService;
import com.example.hyvysocialapp.Nofication.Client;
import com.example.hyvysocialapp.Nofication.Data;
import com.example.hyvysocialapp.Nofication.Respone;
import com.example.hyvysocialapp.Nofication.Sender;
import com.example.hyvysocialapp.Nofication.Token;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    GifImageView imageload;
    TextView nameTv,statusTv;
    EditText messageEt;
    ImageButton sendBtn;

    ValueEventListener seenListner;
    DatabaseReference userReforseen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    //firebase auth
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String hisUid;
    String myUid;
    String hisimage;

    APIService apiService;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Khởi tạo view
            toolbar =findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle("");
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
             getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            recyclerView =findViewById(R.id.chat_recyclerView);
            profileIv = findViewById(R.id.profileIv);
            nameTv = findViewById(R.id.nameTv);
            statusTv = findViewById(R.id.userStatus);
            imageload=findViewById(R.id.imgloading);
            sendBtn =findViewById(R.id.send);
            messageEt=findViewById(R.id.messageEt);
            //Tạo linearlayour cho recyclerview
            LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
//
//            //create api service
//        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);



            Intent intent =getIntent();
            hisUid=intent.getStringExtra("hisUid");
            firebaseAuth =FirebaseAuth.getInstance();

            firebaseDatabase =FirebaseDatabase.getInstance();
            databaseReference =firebaseDatabase.getReference("Users");

            //search user to get user info
        Query userquery = databaseReference.orderByChild("uid").equalTo(hisUid);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String name =""+ snapshot.child("name").getValue();
                    hisimage=""+snapshot.child("image").getValue();
                    String onlineStatus =""+snapshot.child("onlineStatus").getValue();
                    String typingStatus =""+snapshot.child("typingTo").getValue();
                    //
                    if(typingStatus.equals(myUid)){
                        imageload.setVisibility(View.VISIBLE);
                    }
                    else {
                        imageload.setVisibility(View.GONE);
                    }

                    if(onlineStatus.equals("online")){
                        statusTv.setText(onlineStatus);
                    }
                    else {
                        //ép kiểu ngay giờ cho timeStamp
                        Calendar cal =Calendar.getInstance(Locale.CHINA);
                        cal.setTimeInMillis(Long.parseLong(onlineStatus));
                        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                        statusTv.setText(datetime);
                    }
                    //set data
                    nameTv.setText(name);

                    try {
                        Picasso.get().load(hisimage).placeholder(R.drawable.ic_useravatar).into(profileIv);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_useravatar).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                notify =true;
                String message = messageEt.getText().toString().trim();

                if(TextUtils.isEmpty(message)){

                }
                else{
                    sendMessage(message);
                }
                messageEt.setText("");

            }
        });

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0){
                    checkTypingStatus("noOne");
                }
                else
                {
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        readMessage();
        seenMessage();

    }

    private void seenMessage() {
        userReforseen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListner = userReforseen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat =ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid)&&chat.getSender().equals(hisUid)){
                        HashMap<String,Object> hasseenmap =new HashMap<>();
                        hasseenmap.put("isSeen",true);
                        ds.getRef().updateChildren(hasseenmap);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ModelChat chat = snapshot.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid)&&chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat= new AdapterChat(ChatActivity.this,chatList,hisimage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recylerview
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String message) {

        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);



//        final DatabaseReference database =FirebaseDatabase.getInstance().getReference("Users").child(myUid);
//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ModelUsers user= dataSnapshot.getValue(ModelUsers.class);
//                if(notify){
//                    sentNotification(hisUid,user.getName(),message);
//                }
//                notify=false;
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

//    private void sentNotification(final String hisUid, final String name, final String message) {
//
//        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = allTokens.orderByKey().equalTo(hisUid);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                    Token token = ds.getValue(Token.class);
//                    Data data = new Data(myUid,name+":"+message,"New Message",hisUid,R.drawable.logomessage);
//
//                    Sender sender = new Sender(data,token.getToken());
//                    apiService.sendNotification(sender)
//                            .enqueue(new Callback<Respone>() {
//                                @Override
//                                public void onResponse(Call<Respone> call, Response<Respone> response) {
////                                    Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onFailure(Call<Respone> call, Throwable t) {
//
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void checkUserSatus() {
        //lấy thông tin người dùng hiện tại
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        { //Người dùng đăng nhập sẽ ở màn hình này
            myUid=user.getUid();
        }
        else{
            //Người dùng chưa đăng nhập sẽ trờ về màn hình chính
            startActivity(new Intent(ChatActivity.this, IntroActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("onlineStatus",status);

        databaseReference.updateChildren(hashMap);
    }
    private void checkTypingStatus(String typing){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("typingTo",typing);

        databaseReference.updateChildren(hashMap);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //get timestamp
        String timeStamp = String.valueOf(System.currentTimeMillis());
        Log.d(TAG, "onPause: ");
        checkOnlineStatus(timeStamp);
        checkTypingStatus("noOne");
        userReforseen.removeEventListener(seenListner);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        checkOnlineStatus("online");
        super.onResume();
    }
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        checkUserSatus();
        //set online
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserSatus();
        }


        return super.onOptionsItemSelected(item);
    }
}
