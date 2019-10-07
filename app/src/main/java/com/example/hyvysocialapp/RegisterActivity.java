package com.example.hyvysocialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //views
    EditText mEmailtext,mPasswordtext;
    Button mRegisterBtn;
    TextView haveaccount;
    //Tạo hộp Dialog khi đăng kí
    ProgressDialog progressDialog;

    //khai báo firebaseauth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Khai Báo action bar và tựa đề
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("Create Account");

        //cài đặt firebaseauth
        mAuth =FirebaseAuth.getInstance();

        //Hiện nút quay về
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Chuyền tham chiếu vào view
        mEmailtext=findViewById(R.id.EmailEt);
        mPasswordtext=findViewById(R.id.PassEt);
        haveaccount =findViewById(R.id.have_accountTv);

        mRegisterBtn=findViewById(R.id.registerBtn);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký.....");

        //Tạo sự kiện khi nhấn button
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lấy input từ email và password
                String email =mEmailtext.getText().toString().trim();
                String pass =mPasswordtext.getText().toString().trim();
                //xác thực
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //Báo lỗi và bắt nhập lại email
                    mEmailtext.setError("Email không tồn tại!");
                    mEmailtext.setFocusable(true);
                }
                else if (pass.length()<6)
                { //Báo lỗi và bắt nhập lại pass
                    mPasswordtext.setError("Mật khẩu tối đa 6 ký tự!");
                    mPasswordtext.setFocusable(true);

                }
                else {
                    registerUser(email,pass);
                }

            }
        });

        //Sự kiện khi nhấn view login
        haveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(String email,String pass)
    {
        //Nếu chưa tồn tại email và email và mật khâu hợp lệ,tiến hành đăng ký
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Đăng ký thành công đổi giao diện thành giao diện điền thông tin
                    progressDialog.dismiss();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    //Lấy user email và uid người dùng từ firebasUser
                    String email =firebaseUser.getEmail();
                    String uid =firebaseUser.getUid();
                    //Sử dụng HashMap để gán vào database
                    HashMap<Object,String> hashMap =new HashMap<>();
                    //gán thông tin cho hashMap
                    hashMap.put("email",email);
                    hashMap.put("uid",uid);
                    hashMap.put("name","");
                    hashMap.put("onlineStatus","online");
                    hashMap.put("typingTo","noOne");
                    hashMap.put("phone","");
                    hashMap.put("image","https://firebasestorage.googleapis.com/v0/b/socialapp-44748.appspot.com/o/Users_Profile_Cover_Imgs%2Ficonfinder_7_avatar_2754582.png?alt=media&token=3460e55a-7a29-4e48-8ac6-decf5858fcef");
                    hashMap.put("cover","");
                    //Khởi tạo FirebaseDatabase
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference=firebaseDatabase.getReference("Users");
                    //gán hashmap và firebassDatabase
                    databaseReference.child(uid).setValue(hashMap);
                    Toast.makeText(RegisterActivity.this,"Đăng ký ... \n"+firebaseUser.getEmail(),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();

                }else
                {
                    //Nếu báo lỗi , hiển thị cho người dùng
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();//Trở về activity trước
        return super.onSupportNavigateUp();
    }
}
