package com.example.appsales;

import static java.lang.Double.parseDouble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String eCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText email = findViewById(R.id.email);
        EditText name = findViewById(R.id.name);
        EditText phone = findViewById(R.id.phone);
        TextView comision = findViewById(R.id.commision);
        Button btntoupdate = findViewById(R.id.btntoupdate);
        Button btnsearch = findViewById(R.id.btnsearch);
        Button btnedit = findViewById(R.id.btnEdit);
        Button btndelete = findViewById(R.id.btndelete);
        Button btnsales = findViewById(R.id.btnsales);
        Button btnclear = findViewById(R.id.btnclear);

        btnsales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Sales.class));
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirmacion del borrado
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("Está seguro de eliminar el vendedor con email " + email.getText().toString() + "?");
                alertDialogBuilder.setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //se eliminara el vendedor con el emailCustomer respectivo
                                db.collection("customer").document(eCustomer)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d("cliente", "DocumentSnapshot successfully deleted!");
                                                Toast.makeText(MainActivity.this, "Vendedor borrado correctamente...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, "Error..." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });



        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> uCustomer = new HashMap<>();
                uCustomer.put("email", email.getText().toString());
                uCustomer.put("name",name.getText().toString());
                uCustomer.put("ident", phone.getText().toString());

                db.collection("customer").document(eCustomer)
                        .set(uCustomer)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d("cliente", "DocumentSnapshot successfully written!");
                                Toast.makeText(MainActivity.this, "Cliente actualizado correctmente...", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });


        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("customer")
                        .whereEqualTo("email", email.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            eCustomer = document.getId();
                                            name.setText(document.getString("name"));
                                            phone.setText(document.getString("phone"));
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No existe el vendedor", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

        btntoupdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                saveCustomer(email.getText().toString(), name.getText().toString(), phone.getText().toString());

            }

            private void saveCustomer(String semail, String sname, String sphone) {

                //buscar la identificacion  del cliente nuevo

                db.collection("customer")
                        .whereEqualTo("email", semail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {//no encontró el documento, se puede guardar
                                        //Guardar los datos del cliente

                                        Map<String, Object> customer = new HashMap<>();
                                        customer.put("email", semail);
                                        customer.put("name", sname);
                                        customer.put("phone", sphone);
                                        //esta guardando los datos en customer

                                        db.collection("customer")
                                                .add(customer)//guarda los datos en la db
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        // Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                        Toast.makeText(getApplicationContext(), "Cliente agregado correctamente", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Error! Cliente no se guardó", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "email del cliente existe", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        });

            }

        });
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
                name.setText("");
                phone.setText("");
                comision.setText("");
                email.requestFocus();
            }
        });

        double porComision = 0.2;


    }
}

