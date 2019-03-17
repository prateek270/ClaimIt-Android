package com.example.nissan.reimbursement;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {

    String zid,zidd,newzid,name,pA;
    TextView displayName,pendingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        pendingRequest = (TextView)findViewById(R.id.pending);


        newzid=pref.getString("ZID", null);
        if(newzid==null)
        {
            Toast.makeText(DashboardActivity.this,"You are Logged out please Login again",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(DashboardActivity.this,MainActivity.class);
            startActivity(intent);
        }
        name=pref.getString("name","prateek");

        displayName=(TextView)findViewById(R.id.nameId);
        displayName.setText(name);

        Bundle extras =getIntent().getExtras();
        if(extras==null)
            Log.d("xxx","NULL");
        pA=pref.getString("no","0");
        if(!pA.equals("0"))
        pendingRequest.setText(pA);
        pendingRequest.setTextColor(Color.RED);
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Log out ?")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();

                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
    public void claimit(View view)
    {
        Log.d("ZZZ",newzid);

        Intent intent = new Intent(DashboardActivity.this, AddTrip.class);
        intent.putExtra("zid",newzid);
        startActivity(intent);
    }
    public void approve(View view)
    {
        Log.d("ZZZ",newzid);
        Intent intent = new Intent(DashboardActivity.this, ApproveActivity.class);
        intent.putExtra("zid",newzid);
        //Log.d("zzz_send",zid);
        startActivity(intent);
    }
}
