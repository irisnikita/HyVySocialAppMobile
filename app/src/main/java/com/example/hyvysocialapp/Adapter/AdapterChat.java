package com.example.hyvysocialapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hyvysocialapp.Model.ModelChat;
import com.example.hyvysocialapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat> chatList;
    String imageurl;
    FirebaseUser fuser;
    public AdapterChat(Context context, List<ModelChat> chatList, String imageurl) {
        this.context = context;
        this.chatList = chatList;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);


        }
        else{
        View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
        return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        //get Data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();
        //ép kiểu ngay giờ cho timeStamp
        Calendar cal =Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String datetime = DateFormat.format("hh:mm aa",cal).toString();

        //set Data
        holder.messageTv.setText(message);
        holder.timeTv.setText(datetime);
        try{
            Picasso.get().load(imageurl).into(holder.profileIv);
        }
        catch (Exception e){

        }
        if(position==chatList.size()-1){
           if(chatList.get(position).getIsSeen()){
               holder.checkseen.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_anim));
               holder.isSeenTv.setText("Đã xem");
               Picasso.get().load(imageurl).into(holder.checkseen);
//               holder.checkseen.setImageResource(R.drawable.ic_seen);
           }
           else {
               holder.checkseen.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_anim));
               holder.isSeenTv.setText("Đã gửi");
               holder.checkseen.setImageResource(R.drawable.ic_seen);
           }
        }
        else {
            holder.isSeenTv.setVisibility(View.GONE);
            holder.checkseen.setVisibility(View.GONE);
        }
        holder.messageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setTitle("Xóa tin nhắn");
                builder.setMessage("Bạn có chắc muốn xóa tin nhắn");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(holder.timeTv.getVisibility()==View.GONE){
//                    holder.timeTv.setVisibility(View.VISIBLE);
//                }
//                else {
//                    holder.timeTv.setVisibility(View.GONE);
//                }
//            }
//        });
    }

    private void deleteMessage(int postion) {
        final String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String mgsTimestamp = chatList.get(postion).getTimestamp();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        Query query =databaseReference.orderByChild("timestamp").equalTo(mgsTimestamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds :dataSnapshot.getChildren()){
                    if(ds.child("sender").getValue().equals(myuid)){

                        ds.getRef().removeValue();


//                        HashMap<String,Object> hashMap =new HashMap<>();
//                        hashMap.put("message","Tin nhắn đã bị xóa...");
//                        ds.getRef().updateChildren(hashMap);
//                        Toast.makeText(context,"Tin nhắn đã xóa...",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context,"Bạn chỉ xóa được tin nhắn của bạn",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv,checkseen;
        TextView messageTv, timeTv,isSeenTv;
        LinearLayout messageLayout;
        LinearLayout container;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileIv=itemView.findViewById(R.id.profileIv);
            messageTv=itemView.findViewById(R.id.messageTv);

            timeTv=itemView.findViewById(R.id.timeTv);
            isSeenTv=itemView.findViewById(R.id.isSeenTv);
            checkseen =itemView.findViewById(R.id.checkseen);
            messageLayout =itemView.findViewById(R.id.messageLayout);
            container=itemView.findViewById(R.id.layoutmessg);
        }
    }
}
