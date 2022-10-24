package com.example.appsales;

import static java.lang.Double.parseDouble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Sales extends AppCompatActivity {
    FirebaseFirestore dbs = FirebaseFirestore.getInstance();//Instancia de firestore



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        EditText email = findViewById(R.id.email);
        EditText date = findViewById(R.id.date);
        EditText salevalue = findViewById(R.id.salevalue);
        Button btnsave = findViewById(R.id.btnsave);
        Button btnseller = findViewById(R.id.btnseller);
        Button btnclears = findViewById(R.id.btnclear);


        btnseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUpdateCustomer(email.getText().toString(), date.getText().toString(), salevalue.getText().toString());

            }
        });
        btnclears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
                date.setText("");
                salevalue.setText("");
                email.requestFocus();
            }
        });
    }
    private void toUpdateCustomer(String emails, String dates, String salevalues) {
        dbs.collection("customer")
                .whereEqualTo("email", emails)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Email no existe", Toast.LENGTH_SHORT).show();
                            }
                            Map<String, Object> customer = new HashMap<>();
                            customer.put("email", emails);
                            customer.put("date", dates);
                            customer.put("salevalue", salevalues);

                            dbs.collection("customer")
                                    .add(customer)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                                EditText salevalue = findViewById(R.id.salevalue);
                                            if (parseDouble(salevalue.getText().toString()) >= 10000000) {
                                                Toast.makeText(getApplicationContext(), "Valor guardado con exito", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Valor menor a 10000000", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error! Valor no se guardó", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            }
                    }
                });

    }



}




