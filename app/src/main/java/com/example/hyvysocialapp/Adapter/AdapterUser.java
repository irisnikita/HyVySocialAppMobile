package com.example.hyvysocialapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.hyvysocialapp.ChatActivity;
import com.example.hyvysocialapp.Model.ModelUsers;
import com.example.hyvysocialapp.R;
import com.example.hyvysocialapp.ThereProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    List<ModelUsers> usersList;
    //Contructor


    public AdapterUser(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //Lấy dữ liệu
        String myuid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String hisUID =usersList.get(position).getUid();
        String userImage = usersList.get(position).getImage();
        String username = usersList.get(position).getName();
        final String userEmail = usersList.get(position).getEmail();
        //Chuyền dữ liệu
        holder.mNameTv.setText(username);
        holder.mEmailTv.setText(userEmail);
        try{
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_useravatar)
                    .into(holder.mAvatarIv);
        }
        catch (Exception e){

        }
        if(usersList.get(position).getOnlineStatus().equals("online")){
            holder.checkstatusimg.setImageResource(R.drawable.ic_statuson);
        }

        if(usersList.get(position).getTypingTo().equals(myuid)){
            holder.Checkloading.setVisibility(View.VISIBLE);
        }
        else{
            holder.Checkloading.setVisibility(View.GONE);
        }
        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile","Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            Intent intent =new Intent(context, ThereProfileActivity.class);
                            intent.putExtra("uid",hisUID);
                            context.startActivity(intent);
                        }
                        if(i==1){
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid",hisUID);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv,checkstatusimg,Checkloading;
        TextView mNameTv,mEmailTv;
        LinearLayout linearLayout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //Khởi tạo views
            mAvatarIv=itemView.findViewById(R.id.avatarIv);
            mNameTv=itemView.findViewById(R.id.nameTv);
            mEmailTv=itemView.findViewById(R.id.EmailTv);
            checkstatusimg=itemView.findViewById(R.id.statusimage);
            linearLayout=itemView.findViewById(R.id.containeruser);
            Checkloading=itemView.findViewById(R.id.imgloading1);

        }
    }
}
