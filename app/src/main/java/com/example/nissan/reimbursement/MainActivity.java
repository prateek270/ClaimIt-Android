package com.example.nissan.reimbursement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText zid,password;
    Button submit;
    String loginUrl="192.168.43.170:8081/internalapp/";
    public static final String MY_PREFS_NAME = "sharedPreference";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zid=(EditText)findViewById(R.id.email1);
        password=(EditText)findViewById(R.id.pass1);

        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new loginTask().execute(loginUrl);
            }
        });

        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mchannel=new NotificationChannel(Constants.CHANNEL_ID,Constants.CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);

            mchannel.setDescription(Constants.CHANNEL_DESC);
            mchannel.enableLights(true);
            mchannel.setLightColor(Color.RED);
            mchannel.enableVibration(true);

            notificationManager.createNotificationChannel(mchannel);
        }

    }
    class loginTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog progressDialog;


        String zzid=zid.getText().toString();
        final String pass=password.getText().toString();

        String status="",message,name,pendingApproval;
        int id;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            //progressDialog.dismiss();
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                return postData(params[0]);
            } catch (IOException ex) {


                return "Network Error!";
            } catch (JSONException ex) {
                return "Data Invalid !";
            }
        }
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            JSONObject c = null;
            try {
                c = new JSONObject(result);



                status = c.getJSONObject("upModel").getString("zid");
                name=c.getJSONObject("upModel").getString("name");
                pendingApproval=c.getString("no");
                message= c.getJSONObject("upModel").getString("email");
                Log.d("id",status+" "+message+" ");

            } catch (JSONException e) {
                e.printStackTrace();
            }





            if(progressDialog!=null)
            {
                progressDialog.dismiss();
            }
            if(TextUtils.isEmpty(message))
            {
                Toast.makeText(MainActivity.this, "Incorrect Id or Password", Toast.LENGTH_LONG).show();


            }
            else
            {
                //Toast.makeText(MainActivity.this, "good", Toast.LENGTH_LONG).show();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor=pref.edit();
                editor.putString("ZID", zzid);
                editor.putString("name",name);// Storing string
                editor.putString("no",pendingApproval);
                editor.commit(); // commit changes


                Intent i=new Intent(MainActivity.this,DashboardActivity.class);
                i.putExtra("zid",zzid);
                i.putExtra("pending",pendingApproval);
                startActivity(i);

            }
        }

        private String postData(String urlPath) throws IOException,JSONException{


            StringBuilder result =new StringBuilder();
            BufferedWriter bufferedWriter=null;
            BufferedReader bufferedReader=null;
            StringBuilder builder;
            try{
                JSONObject dataToSend=new JSONObject();

                dataToSend.put("zid",zzid);
                dataToSend.put("password",pass);
                dataToSend.put("fcm_token",FirebaseInstanceId.getInstance().getToken());



                //building connection to the server
                URL url=new URL("http://192.168.43.170:8081/internalapp/");
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(20000);
                urlConnection.setConnectTimeout(20000);

                urlConnection.setRequestMethod("POST");


                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                //urlConnection.connect();
                Log.d("qqqq ",dataToSend.toString());


                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(dataToSend.toString());
                wr.flush();


                //read data response from server


                InputStream inputStream=urlConnection.getInputStream();
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line=bufferedReader.readLine())!=null){
                    result.append(line).append("\n");
                }

                builder = new StringBuilder();
                builder.append(urlConnection.getResponseCode());
            }catch (Exception e){

                e.printStackTrace();
            }
            finally {


                if(bufferedReader!=null)
                    bufferedReader.close();
                if(bufferedWriter!=null)
                    bufferedWriter.close();

            }
            return result.toString();


        }
    }
}
