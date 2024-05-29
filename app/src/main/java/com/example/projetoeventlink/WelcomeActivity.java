package com.example.projetoeventlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    private TextView tvNome;
    private FirebaseAuth authProfile;
    private DatabaseReference referenceProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tvNome = findViewById(R.id.tvNome);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        } else {
            checkifEmailVerified(firebaseUser);
            referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users").child(firebaseUser.getUid());

            referenceProfile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String Nome = snapshot.child("nombres").getValue(String.class);
                        if (Nome != null && !Nome.isEmpty()) {
                            String[] nameParts = Nome.split(" ");
                            tvNome.setText(nameParts[0]);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void checkifEmailVerified(FirebaseUser firebaseUser) {

        if (!firebaseUser.isEmailVerified()) {
            ShowAlertDialog();
        }
        
    }

    private void ShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
        builder.setTitle(getString(R.string.alertb_notverified_email_title));
        builder.setMessage(getString(R.string.alertb_notverified_email));

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Para que no se abra en la aplicación y sí de forma separada.
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}