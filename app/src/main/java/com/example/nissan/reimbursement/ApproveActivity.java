package com.example.nissan.reimbursement;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApproveActivity extends AppCompatActivity {
    private static  String URL_DATA;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<HistoryList> historyLists;
    Button rec;
    String zid,newzid,name;
    TextView displayName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        newzid=pref.getString("ZID", null);
        name=pref.getString("name","prateek");
        displayName=(TextView)findViewById(R.id.nameId);
        displayName.setText(name);

        Bundle extras =getIntent().getExtras();
        if(extras==null)
            Log.d("xxx","NULL");

        //zid=extras.getString("zid");
        URL_DATA = "http://192.168.43.170:8081/internalapp/reimbursements/pull/tobeapproved/"+newzid;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyLists = new ArrayList<>();
        loadUrlData();


    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ApproveActivity.this,DashboardActivity.class);
        startActivity(intent);
    }

    private void loadUrlData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
//                    
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++){

                        JSONObject jo = array.getJSONObject(i);
//

                        HistoryList history = new HistoryList(jo.getJSONObject("arModel").getString("type"),jo.getJSONObject("arModel").getString("status"),jo.getJSONObject("upModel").getString("name"),jo.getJSONObject("arModel").getJSONObject("reuqestdata").getString("amount"),jo.getJSONObject("arModel").getString("requid"),"Initiator");
                        historyLists.add(history);

                    }

                    adapter = new HistoryAdapter(historyLists, getApplicationContext());

                    recyclerView.setAdapter(adapter);


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ApproveActivity.this, "Error" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
