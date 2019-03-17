package com.example.nissan.reimbursement;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class AddTrip extends AppCompatActivity {

    Button newTrip;
    private String m_Text = "";
    public List<TripList> tlist;
    SwipeController swipeController = null;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        tlist = new ArrayList<>();






        newTrip = (Button) findViewById(R.id.addTrip);
        newTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(AddTrip.this);
                builder.setTitle("Name of the Trip");

                final EditText input = new EditText(AddTrip.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        TripList tripHistory = new TripList(m_Text,0);
                        tlist.add(tripHistory);

                        adapter = new TripAdapter(tlist, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        swipeController = new SwipeController(new SwipeControllerActions() {
                            @Override
                            public void onRightClicked(int position) {
                                tlist.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                            }
                        });

                        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
                        itemTouchhelper.attachToRecyclerView(recyclerView);

                        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                            @Override
                            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                                swipeController.onDraw(c);
                            }
                        });

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

}
