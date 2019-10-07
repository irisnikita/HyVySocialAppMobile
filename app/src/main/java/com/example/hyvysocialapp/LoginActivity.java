package com.example.hyvysocialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    //Views
    EditText mEmailet,mPasswordet;
    TextView donthave_account,mRecoverPass;
    Button mLoginBtn;
    SignInButton mGooglebutton;
    //
    AlertDialog alertDialog;
    //
    private FirebaseAuth mAuth;
    //
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Khai Báo action bar và tựa đề
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("Đăng nhập");

        //Hiện nút quay về
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        //
        mAuth= FirebaseAuth.getInstance();

        //Tham chiếu vào findviewbyid
        mEmailet =findViewById(R.id.EmailEt);
        mPasswordet =findViewById(R.id.PassEt);
        donthave_account = findViewById(R.id.donthave_accountTv);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRecoverPass =findViewById(R.id.recoverpass);
        mGooglebutton =findViewById(R.id.logingoogle);

        //Sự kiện khi nhấn vào loginbtn
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Truyền dự liệu vào email, password
                String email=mEmailet.getText().toString().trim();
                String pass=mPasswordet.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //email hiện lỗi
                    mEmailet.setError("Email không hợp lệ");
                    mEmailet.setFocusable(true);
                }
                else
                {
                    loginUser(email,pass);
                }
            }
        });

        //sự kiện khi nhấn vào lấy lại mật khẩu
        mRecoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });

        //sự kiện khi nhấn button google log in
        mGooglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //sự kiện cho không có tài khoản
        donthave_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
        //khai báo progesdilag
        pd =new ProgressDialog(this);

    }

    private void showRecoverPasswordDialog() {
        //Tạo dialog recover pass
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layoutview = getLayoutInflater().inflate(R.layout.dialog_recover,null);
        Button sendmaill= layoutview.findViewById(R.id.button_sendpassrecover);
        Button Cancel =layoutview.findViewById(R.id.button_cancle);
        final EditText textmail =layoutview.findViewById(R.id.Email_recover);
        builder.setView(layoutview);
        alertDialog=builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        sendmaill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nhập email
                String email = textmail.getText().toString().trim();
                if(!email.isEmpty()){
                beginRecovery(email);
                }else textmail.setError("Vui lòng nhập email");
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void beginRecovery(String email) {
        pd.setMessage("Đang gửi email...");
        pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this,"Hãy kiểm tra email để xác nhận",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(LoginActivity.this,"Không thể xác nhận email",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String pass) {
        pd.setMessage("Đang đăng nhập...");
        pd.show();
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
           if(task.isSuccessful()){
               //
               pd.dismiss();
               FirebaseUser user=mAuth.getCurrentUser();
               startActivity(new Intent(LoginActivity.this, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
               finish();
           }
           else{
               //
               pd.dismiss();
               Toast.makeText(LoginActivity.this,"Đăng nhập không thành công",Toast.LENGTH_SHORT).show();
           }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();//Trở về activity trước
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            //Nếu người dùng lần đầu tiên đăng nhập thì lấy dữ liệu và hiển thị từ account google
                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                String email =user.getEmail();
                                String uid =user.getUid();
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
                            }


                            //hiển thị user google trên toast
                            Toast.makeText(LoginActivity.this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
                            //Đến màn hình chính
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.layoutlogin), "Đăng nhập không thành công !", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
