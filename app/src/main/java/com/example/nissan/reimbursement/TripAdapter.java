package com.example.nissan.reimbursement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder>
{


    // Inside this class, we’ll have another class for the ViewHolder thus
    public List<TripList> tripLists;
    private Context context;

    public TripAdapter(List<TripList> tripLists, Context context) {
        this.tripLists= tripLists;
        this.context = context;
    }
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_trip_list,parent,false);
        return new TripAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TripAdapter.ViewHolder holder, final int position)
    {
        final TripList tripPosition = tripLists.get(position);
        holder.type.setText(tripPosition.getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),ClaimitActivity.class);
                view.getContext().startActivity(intent);

            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Name of the Trip");

                final EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_Text = input.getText().toString();
                        tripPosition.setName(new_Text);
                        holder.type.setText(new_Text);







                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }
    @Override
    public int getItemCount() {
        return tripLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView type;
        public Button send;
        public Button edit;
        public RelativeLayout relativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = (TextView)itemView.findViewById(R.id.department);
            send=(Button)itemView.findViewById(R.id.submit);
            edit=(Button)itemView.findViewById(R.id.edit);
            relativeLayout=(RelativeLayout)itemView.findViewById(R.id.tripCard);

        }
    }


}