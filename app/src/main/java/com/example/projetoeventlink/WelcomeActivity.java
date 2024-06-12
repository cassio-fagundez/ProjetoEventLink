package com.example.projetoeventlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
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
    private BottomNavigationView bnView;
    private FloatingActionButton faButton;

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

        // Configuración de los ítems del Bottom Navigation Bar.

        bnView = findViewById(R.id.bottomNavigationView);
        faButton = findViewById(R.id.floatingActionButton);

        // CONTINUAR DESDE AQUÍ.

       /* bnView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.menu_home){
                    return true;
                } else if (item.getItemId()==R.id.menu_refresh){
                    return true;
                } else if (item.getItemId()==R.id.menu_settings){
                    return true;
                } else if (item.getItemId()==R.id.menu_logout) {
                    return true;
                } else { return true; }
            }
        }); VERSION NO DEPRECADA. PROBAR DESPUÉS SI FUNCIONA. */

        bnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.menu_home){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } else if (item.getItemId()==R.id.menu_refresh){
                    startActivity(getIntent());
                    overridePendingTransition(0,0);


                } else if (item.getItemId()==R.id.menu_settings){


                } else if (item.getItemId()==R.id.menu_logout) {
                    authProfile.signOut();
                    Toast.makeText(WelcomeActivity.this, getString(R.string.toast_sucessful_logout), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    //Flags para que el usuario no pueda regresar y loguearse.
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AGREGAR FUNCION PARA AGREGAR NUEVO EVENTO. INTENT PARA ADD EVENT. REVISAR SI YA HAY DOCUMENTO REGISTRADO.
            }
        });

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