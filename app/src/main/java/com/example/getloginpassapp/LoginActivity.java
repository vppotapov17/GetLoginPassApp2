package com.example.getloginpassapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends Activity implements View.OnClickListener  {
    private EditText etLogin;
    private EditText etPassword;
    private Button buttonSubmit;
    private String login, password, url_link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin=findViewById(R.id.eTLogin);
        etPassword=findViewById(R.id.eTPassword);
        buttonSubmit=findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){

        login=etLogin.getText().toString();
        password=etPassword.getText().toString();


        if (!login.isEmpty() && !password.isEmpty()){
            DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(login);

            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            if (task.getResult().get("password").toString().equals(password)){
                                String name = task.getResult().getString("Name");
                                Intent intent=new Intent();
                                intent.putExtra(MainActivity.FULLUSERNAME, name);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else {
                                Intent intent=new Intent();
                                setResult(RESULT_CANCELED, intent);
                                finish();
                            }
                        }
                        else {
                            Intent intent=new Intent();
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        }
                    }

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

}
