package com.sagar.notesdemo.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sagar.notesdemo.MainActivity;
import com.sagar.notesdemo.Model.Data;
import com.sagar.notesdemo.Model.DataWithKey;
import com.sagar.notesdemo.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sagar.notesdemo.MainActivity.getCurrentTimeStamp;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private Context context;
    private List<Data> dataList;
    private View.OnClickListener clickListener;

    private AlertDialog dialog;

    private String itemKey;

    List<DataWithKey> dataWithKeys;

    public RecyclerViewAdapter(Context context, List<Data> dataList, List<DataWithKey> dataWithKeys) {
        this.context = context;
        this.dataList = dataList;
        this.dataWithKeys = dataWithKeys;
    }

    public RecyclerViewAdapter() {

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DataWithKey dataWithKey = dataWithKeys.get(position);

        Data data = dataWithKey.getData();

        holder.title.setText(data.getTitle());
        holder.desc.setText(data.getDescription());

        Log.d("mylog", "Title : " + data.getTitle() + " Desc : " + data.getDescription());

    }

    @Override
    public int getItemCount() {
        return dataWithKeys.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOptionDialog(getAdapterPosition());
                }
            });
        }
    }

    public void showOptionDialog(final int adapterPos ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("CRUD options");

        String [] items = {"Update", "Delete"};
        ListAdapter listAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                items
        );

        builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    dialog.dismiss();
                    showUpdateDialog( adapterPos );
                } else if(which == 1) {
                    showDeleteDialog(adapterPos);
                }
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showUpdateDialog(final int adapterPos ) {

        final String updatedTime = getCurrentTimeStamp();

        final EditText etTitle, etDesc;
        Button yes,no;

        final String itemKey = dataWithKeys.get(adapterPos).getKey();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final Data data = dataList.get(adapterPos);

        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.update_dialog_layout, null, false);

        etTitle = dialogView.findViewById(R.id.title);
        etDesc = dialogView.findViewById(R.id.desc);
        etTitle.setText( data.getTitle() );
        etDesc.setText( data.getDescription() );

        yes = dialogView.findViewById(R.id.yes);
        no = dialogView.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String desc = etDesc.getText().toString();

                data.setTitle(title);
                data.setDescription(desc);

                dataList.set(adapterPos, data);

                updateDataFromFirebase(title,desc,updatedTime,itemKey);

                dialog.dismiss();

                RecyclerViewAdapter.this.notifyDataSetChanged();


            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView( dialogView );


        dialog = builder.create();
        dialog.show();
    }

    public void showDeleteDialog(final int adapterPos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Warning!");
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Data data = dataList.get(adapterPos);

                itemKey  = dataWithKeys.get(adapterPos).getKey();
                dataWithKeys.remove(adapterPos);
                deleteDataFromFirebase(itemKey);

            }
        });

        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateDataFromFirebase(String title,String desc,String time,String itemKey){

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        Data data = new Data(title,desc,time);

        Map<String,Object> dataValues = data.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Data/110/"+itemKey, dataValues);

        myRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteDataFromFirebase(String key){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data").child("110").child(key);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                RecyclerViewAdapter.this.notifyDataSetChanged();
                Toast.makeText(context, "Deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
