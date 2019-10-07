package com.example.hyvysocialapp.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hyvysocialapp.Model.ModelPost;
import com.example.hyvysocialapp.R;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    public AdapterPosts() {
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,parent,false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String pTimestamp = postList.get(position).getpTime();

        Calendar calendar =Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(Long.parseLong(pTimestamp));
        String ptime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(ptime);
        holder.PtitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.uNameTv.setText(uName);
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.ic_useravatar).into(holder.uPictureIv);

        }catch (Exception e){

        }
        if(pImage.equals("noImage"))
        try{
            Picasso.get().load(R.drawable.background).into(holder.pImageIv);
        }
        catch (Exception e){

        }
        else{
            try{
                Picasso.get().load(pImage).placeholder(R.drawable.background).into(holder.pImageIv);
            }
            catch (Exception e){}

        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"More",Toast.LENGTH_SHORT).show();
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"comment",Toast.LENGTH_SHORT).show();
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"share",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView uPictureIv,pImageIv;
        TextView uNameTv,PtitleTv,pTimeTv,pDescriptionTv,pLikeTv,likeBtn,commentBtn,shareBtn;
        ImageButton moreBtn;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            uPictureIv=itemView.findViewById(R.id.uPictureIv);
            pImageIv=itemView.findViewById(R.id.pImageView);
            uNameTv=itemView.findViewById(R.id.uNameTv);
            pTimeTv=itemView.findViewById(R.id.pTimeTv);
            pDescriptionTv=itemView.findViewById(R.id.pDescrptionTv);
            likeBtn=itemView.findViewById(R.id.plikeTv);
            commentBtn=itemView.findViewById(R.id.pCommentTv);
            shareBtn=itemView.findViewById(R.id.pShareTv);
            moreBtn=itemView.findViewById(R.id.moreBtn);
            pLikeTv=itemView.findViewById(R.id.pLike);
            PtitleTv=itemView.findViewById(R.id.pTitleTv);

        }
    }
}
