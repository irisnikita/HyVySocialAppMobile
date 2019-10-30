package com.example.hyvysocialapp.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.hyvysocialapp.Adapter.AdapterPosts;
import com.example.hyvysocialapp.DashboardActivity;
import com.example.hyvysocialapp.IntroActivity;
import com.example.hyvysocialapp.Model.ModelPost;
import com.example.hyvysocialapp.R;
import com.example.hyvysocialapp.addPostActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //Storage
    StorageReference storageReference;
    //path where images of user profile and cover will be stored
    String storagePath ="Users_Profile_Cover_Imgs/";
    //views
    ImageView avatarIv,coverIv;
    TextView nameTv,emailTv,phoneTv;
    FloatingActionButton fab;
    RecyclerView postsRecylerView;
    //Tạo progges dialog
    ProgressDialog pd;
    Dialog popAddpost;

    //Cấp quyền truy cập
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_CAMERA_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    //
    String cameraPermissions[];
    String storagePermissions[];

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;
    //
    Uri image_uri;

    String profileOrCoverPhoto;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        iniPopUp();
        //Khai báo firebase
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();
        //
        cameraPermissions =new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions =new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //Khai báo views
        avatarIv =view.findViewById(R.id.avatarIv);
        nameTv =view.findViewById(R.id.nameTv);
        coverIv=view.findViewById(R.id.coverIv);
        emailTv =view.findViewById(R.id.EmailTv);
        phoneTv =view.findViewById(R.id.PhoneTv);
        fab=view.findViewById(R.id.fab);
        postsRecylerView=view.findViewById(R.id.recyclerview_posts);
        //Khai báo progesDialog
        pd=new ProgressDialog(getActivity());

        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrCoverPhoto ="image";
                showImagePicDialog();
            }
        });
        coverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrCoverPhoto ="cover";
                showImagePicDialog();
            }
        });

        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
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
        //Sự kiện khi nhấn fab button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });
        postList =new ArrayList<>();
        checkUserSatus();
        loadMyPosts();
        return view;
    }

    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

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

                    postList.add(myPosts);

                    //adapter
                    adapterPosts = new AdapterPosts(getActivity(),postList);
                    //set this adapter to recyclerview
                    postsRecylerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchdMyPosts(final String searchQuery) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

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
                    adapterPosts = new AdapterPosts(getActivity(),postList);
                    //set this adapter to recyclerview
                    postsRecylerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean checkStoragePermission(){
        //Kiểm tra quyền truy cập tệp có được cấp hay không
        //trả về true nếu có và trả về false nếu không
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }
    private void requestStoragePermission(){
        //Yêu cầu quyền truy cập
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        //Kiểm tra quyền truy cập tệp có được cấp hay không
        //trả về true nếu có và trả về false nếu không
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }
    private void requestCameraPermission(){
        //Yêu cầu quyền truy cập
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        //Tạo lựa chọn hiển thị trong dialog
        String options[]={"Chọn ảnh đại diện","Chọn ảnh bìa","Sửa tên","Sửa số điện thoại"};
        //Màn hình thông báo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Đặt tiêu đề
        builder.setTitle("Chỉnh sửa thông tin");
        //Đưa item vào dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //Chỉnh sửa ảnh đại diện

                    pd.setMessage("Cập nhật ảnh đại diện");
                    profileOrCoverPhoto ="image";
                    showImagePicDialog();
                }
                else if(i==1){
                    //Chỉnh sửa ảnh bìa
                    pd.setMessage("Cập nhật ảnh bìa");
                    profileOrCoverPhoto="cover";
                    showImagePicDialog();
                }else if(i==2){
                    //Chỉnh sửa tên
                    pd.setMessage("Cập nhật ảnh tên");
                    showNamePhoneUpdateDialog("name");
                }else if(i==3){
                    //Chỉnh sửa số điện thoại
                    pd.setMessage("Cập nhật số điện thoại");
                    showNamePhoneUpdateDialog("phone");
                }

            }
        });
        builder.create().show();

    }

    private void showNamePhoneUpdateDialog(final String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(key.equals("name")){
        builder.setTitle("Cập nhật Tên");}
        else{
            builder.setTitle("Cập nhật Số điện thoại");
        }
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText= new EditText(getActivity());
        editText.setHint("Nhập "+key);
        if(key.equals("phone")){
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        //add buttons in dialog
        builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String value = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String,Object> result = new HashMap<>();
                    result.put(key,value);
                    databaseReference.child(firebaseUser.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(),"Đã cập nhật ...",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    if(key.equals("name")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        Query query =ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child = ds.getKey();
                                    if(dataSnapshot.child(child).hasChild("Comments")){
                                        String child1 = ""+dataSnapshot.child(child).getKey();
                                        Query child2 =FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                                    String child = snapshot.getKey();
                                                    dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getActivity(),"Hãy nhập "+key+"",Toast.LENGTH_SHORT).show();
                }

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

    private void showImagePicDialog() {

        String options[]={"Chụp ảnh từ camera","Chọn ảnh từ thư viện"};
        //Màn hình thông báo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Đặt tiêu đề
        if(profileOrCoverPhoto.equals("image")){
        builder.setTitle("Cập nhật ảnh đại diện");}
        else if(profileOrCoverPhoto.equals("cover")){
            builder.setTitle("Cập nhật ảnh bìa");
        }

        //Đưa item vào dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //Chọn camera
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else
                        {pickFromCamera();}
                }
                else if(i==1){
                    //Chọn từ thư viện
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else
                        {
                        pickFromGallery();
                    }

                }

            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Phương thức này sẽ hiện cho người dùng cấp quyền truy cập hệ thống
        // người dùng sẽ chọn cấp hoặc không
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
            //Chọn ảnh từ camera,kiểm tra xem có được quyền truy cập hay không
                if(grantResults.length>0){
                    boolean cameraAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted =grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        //Được cấp quyền
                        pickFromCamera();
                    }
                    else{
                        //Không được cấp
                        Toast.makeText(getActivity(),"Hãy cấp quyền truy cập camera và thư mục",Toast.LENGTH_SHORT).show();
                    }
                }
            }break;
            case STORAGE_REQUEST_CODE:{

                //Chọn ảnh từ thư viện,kiểm tra xem có được quyền truy cập hay không
                if(grantResults.length>0){
                    boolean writeStorageAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        //Được cấp quyền
                        pickFromGallery();
                    }
                    else{
                        //Không được cấp
                        Toast.makeText(getActivity(),"Hãy cấp quyền truy cập Thư viện",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Phương thức này sẽ gọi sau khi chọn ảnh từ camera hoặc gallery
        if(resultCode==RESULT_OK)
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //ảnh được chọn từ camera,lấy uri của ảnh
                image_uri =data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode ==IMAGE_PICK_CAMERA_CODE){
            //ảnh được chọn từ gallery,lấy uri của ảnh
                uploadProfileCoverPhoto(image_uri);

            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        //show progress
        pd.show();
        //path va name của ảnh được lưu vào Storage firebass
        String filepathAndname =storagePath+""+profileOrCoverPhoto+"_"+firebaseUser.getUid();
        StorageReference storageReference2nd =storageReference.child(filepathAndname);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                final Uri downloadUri = uriTask.getResult();

                //Kiểm tra xem ảnh được upload lên hay không
                if(uriTask.isSuccessful()){

                    HashMap<String,Object> results =new HashMap<>();
                    results.put(profileOrCoverPhoto,downloadUri.toString());

                    databaseReference.child(firebaseUser.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Đang tải ảnh ...",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                Toast.makeText(getActivity(),"Tải ảnh thất bại...",Toast.LENGTH_SHORT).show();
                            }
                        });

                    if(profileOrCoverPhoto.equals("image")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        Query query =ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child = ds.getKey();
                                    if(dataSnapshot.child(child).hasChild("Comments")){
                                        String child1 = ""+dataSnapshot.child(child).getKey();
                                        Query child2 =FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                                    String child = snapshot.getKey();
                                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }else {
                    pd.dismiss();
                    Toast.makeText(getActivity(),"Có một số trục trặc!",Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFromCamera() {
        try{
        //Truy cập vùng chọn ảnh từ thiết bị
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Tem Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Tem Description");
        //Nhập image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
//        //intent to start camera
        Intent cameraIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
        }
       catch (Exception e){
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
       }

    }

    private void pickFromGallery() {
        //Chọn ảnh từ Gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
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
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button
                if(!TextUtils.isEmpty(s)){
                    searchdMyPosts(s);
                }
                else {
                    loadMyPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called whenever user type any letter
                if(!TextUtils.isEmpty(s)){
                    searchdMyPosts(s);
                }
                else {
                    loadMyPosts();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,menuInflater);
    }

    /*handle menu item click*/

    private void iniPopUp() {
        popAddpost=new Dialog(getActivity());
        popAddpost.setContentView(R.layout.activity_add_post);
        popAddpost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddpost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddpost.getWindow().getAttributes().gravity= Gravity.TOP;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserSatus();
        }
        if(id == R.id.action_add){
            startActivity(new Intent(getActivity(),addPostActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }
    private void checkUserSatus() {
        //lấy thông tin người dùng hiện tại
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        { //Người dùng đăng nhập sẽ ở màn hình này
            uid = user.getUid();

        }
        else{
            //Người dùng chưa đăng nhập sẽ trờ về màn hình chính
            startActivity(new Intent(getActivity(), IntroActivity.class));
            getActivity().finish();
        }
    }
}
