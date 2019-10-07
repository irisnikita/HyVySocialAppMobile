package com.example.hyvysocialapp.Nofication;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.*;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenfresh = FirebaseInstanceId.getInstance().getToken();
        if(user!=null){
            updateToken(tokenfresh);
        }
    }

    private void updateToken(String tokenfresh) {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token =new Token(tokenfresh);
        ref.child(user.getUid()).setValue(token);
    }
}
