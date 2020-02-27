package com.sagar.notesdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagar.notesdemo.Adapter.RecyclerViewAdapter;
import com.sagar.notesdemo.Model.Data;
import com.sagar.notesdemo.Model.DataWithKey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton fab;

    EditText title;
    EditText desc;
    Button btn_save;
    Button btn_discard;

    AlertDialog dialog;
    String clientId;

    private DatabaseReference databaseReference;

    private List<Data> dataList;

    private List<DataWithKey> dataWithKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");
        clientId = "110";

        fetchData();



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        /*if (dataList != null){
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this,dataList);
            recyclerView.setAdapter(adapter);
        }*/


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        dataWithKeys = new ArrayList<>();

    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        /*builder.setTitle("Add a note here");
        builder.setMessage("Title & Description Required");
        builder.setIcon(R.drawable.ic_dialog_icon);*/
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        title = dialogView.findViewById(R.id.title);
        desc = dialogView.findViewById(R.id.desc);
        btn_save = dialogView.findViewById(R.id.btn_save);
        btn_discard = dialogView.findViewById(R.id.btn_discard);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = title.getText().toString();
                String descText = desc.getText().toString();
                String dateTime = getCurrentTimeStamp();



                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("title",titleText);
                hashMap.put("description",descText);
                hashMap.put("datetime",dateTime);

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Data").child(clientId);

                myRef.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "A note added successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });

        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void fetchData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data").child(clientId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: "+dataSnapshot);

                dataList = new ArrayList<>();

                dataWithKeys.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    //snapshot.
                    Data myData = snapshot.getValue(Data.class);

                    DataWithKey dataWithKey = new DataWithKey();
                    dataWithKey.setKey(snapshot.getKey());
                    dataWithKey.setData(myData);

                    Log.d(TAG, "onDataChange: KKKK: "+snapshot.getKey());

                    if (myData !=null){
                        Log.d(TAG, "onDataChange: Title "+myData.getTitle());
                    }

                    dataList.add(myData);

                    dataWithKeys.add(dataWithKey);
                }

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, dataList, dataWithKeys);
                recyclerView.setAdapter(adapter);

                Log.d(TAG, "onDataChange: "+dataList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //get current time
    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}

