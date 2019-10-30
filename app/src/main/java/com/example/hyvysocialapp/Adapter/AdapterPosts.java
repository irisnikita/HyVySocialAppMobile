package com.example.hyvysocialapp.Adapter;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hyvysocialapp.Model.ModelPost;
import com.example.hyvysocialapp.PostDetailActivity;
import com.example.hyvysocialapp.R;
import com.example.hyvysocialapp.ThereProfileActivity;
import com.example.hyvysocialapp.addPostActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;

    String myUid;

    private DatabaseReference likesRef;
    private DatabaseReference postsRef;

    boolean mProcessLike = false;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
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
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        final String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        final String pImage = postList.get(position).getpImage();
        String pTimestamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes();
        String pComments = postList.get(position).getpComments();
        Calendar calendar =Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(Long.parseLong(pTimestamp));
        String ptime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(ptime);
        holder.PtitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.pLikeTv.setText(pLikes+" Likes");
        holder.pCommentsTv.setText(pComments+" Comments");
//        holder.uNameTv.setText(uName);
        setLikes(holder,pId);
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
                showMoreOptions(holder.moreBtn,uid,myUid,pId,pImage);
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;
                final String postId =postList.get(position).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(mProcessLike){
                                if(dataSnapshot.child(postId).hasChild(myUid)){
                                    postsRef.child(postId).child("pLikes").setValue(""+(pLikes-1));
                                    likesRef.child(postId).child(myUid).removeValue();
                                    mProcessLike = false;
                                }
                                else{
                                    postsRef.child(postId).child("pLikes").setValue(""+(pLikes+1));
                                    likesRef.child(postId).child(myUid).setValue("Liked");
                                    mProcessLike = false;
                                }
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId",pId);
                context.startActivity(intent);
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"share",Toast.LENGTH_SHORT).show();
            }
        });

        holder.uPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid",uid);
                context.startActivity(intent);
            }
        });
    }

    private void setLikes(final MyHolder holder, final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postKey).hasChild(myUid)){
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked,0,0,0);
                    holder.likeBtn.setText("Liked");
                }
                else{
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_post,0,0,0);
                    holder.likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pId, final String pImage) {
        PopupMenu popupMenu =new PopupMenu(context,moreBtn, Gravity.END);

        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Xóa");
            popupMenu.getMenu().add(Menu.NONE,1,0,"Sửa");
        }
        popupMenu.getMenu().add(Menu.NONE,2,0,"Xem bài viết");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id= menuItem.getItemId();
                if(id==0){
                    beginDelete(pId,pImage);
                }else if(id==1){
                    Intent intent =new Intent(context, addPostActivity.class);
                    intent.putExtra("key","editPost");
                    intent.putExtra("editPostId",pId);
                    context.startActivity(intent);
                }
                else if (id==2){
                    Intent intent=new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId",pId);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        if(pImage.equals("noImage")){
            deleteWithoutImage(pId,pImage);
        }
        else {
            deleteWithImage(pId,pImage);
        }
    }

    private void deleteWithImage(final String pId, String pImage) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Đang xóa...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context,"Xóa thành công",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteWithoutImage(String pId, String pImage) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Đang xóa...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context,"Xóa thành công",Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView uPictureIv,pImageIv;
        TextView uNameTv,PtitleTv,pTimeTv,pDescriptionTv,pLikeTv,likeBtn,commentBtn,shareBtn,pCommentsTv;
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
            pCommentsTv=itemView.findViewById(R.id.pCommentsTv);
            PtitleTv=itemView.findViewById(R.id.pTitleTv);

        }
    }
}
