package com.example.nissan.reimbursement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import com.github.chrisbanes.photoview.PhotoView;

public class ApproveForm extends AppCompatActivity {

    EditText amount,rem,type;
    Button approve,reject;
    String Url,statusUrl;
    ImageView imag;

    PhotoView photoView;
    Bitmap decodedByte;
    String zid,reqId,newzid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_form);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        newzid=pref.getString("ZID", null);
        Bundle extras =getIntent().getExtras();
        if(extras==null)
            Log.d("xxx","NULL");
        //zid=extras.getString("zid");
        reqId=extras.getString("req_id");

        Url="http://192.168.43.170:8081/internalapp/reimbursements/pull/data/"+newzid+"/"+reqId;
        statusUrl="http://192.168.43.170:8081/internalapp/reimbursements/approve/"+reqId+"/";

        Log.d("xxx",Url);
        amount=(EditText)findViewById(R.id.amount2);
        type=(EditText)findViewById(R.id.type);
        rem=(EditText)findViewById(R.id.remarks2);
        imag=(ImageView)findViewById(R.id.dimage);
        imag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ApproveForm.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                photoView = mView.findViewById(R.id.imageView);
                photoView.setImageBitmap(decodedByte);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        approve=(Button)findViewById(R.id.approve);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusUrl="http://192.168.43.170:8081/internalapp/reimbursements/approve/"+reqId+"/Approved";
                new resultTask().execute(statusUrl);

            }
        });


        reject=(Button)findViewById(R.id.reject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusUrl="http://192.168.43.170:8081/internalapp/reimbursements/approve/"+reqId+"/Rejected";

                new resultTask().execute(statusUrl);
            }
        });





        new fetchtTask().execute(Url);

    }
    class resultTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog progressDialog;
        String status="",message,name;
        int id;
        boolean xyz;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog=new ProgressDialog(ApproveForm.this);
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



                status = c.getString("STATUS");


            } catch (JSONException e) {


                e.printStackTrace();
            }

            if(progressDialog!=null)
            {
                progressDialog.dismiss();
            }
            if(status=="true")
            {
                Intent i=new Intent(ApproveForm.this,ApproveActivity.class);
                i.putExtra("zid",zid);
                startActivity(i);
                finish();



            }
            else
            {
                Toast.makeText(ApproveForm.this, "Ivalid", Toast.LENGTH_LONG).show();

            }
        }

        private String postData(String statusUrl) throws IOException,JSONException{


            StringBuilder result =new StringBuilder();
            BufferedWriter bufferedWriter=null;
            BufferedReader bufferedReader=null;
            StringBuilder builder;
            try{
                JSONObject dataToSend=new JSONObject();
                //building connection to the server
                URL url=new URL(statusUrl);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(20000);
                urlConnection.setConnectTimeout(20000);

                urlConnection.setRequestMethod("POST");


                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                //urlConnection.connect();
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

    class fetchtTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog progressDialog;
        String a,r,t,img;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("xxx3","check");
            progressDialog=new ProgressDialog(ApproveForm.this);
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

            JSONObject c = null,d;
            String imgId;
            try {
                c = new JSONObject(result);
                d=c.getJSONObject("rdmModel");
                a = d.getString("amount");
                r=d.getString("remark");
                t=d.getString("type");
                imgId=d.getString("imgId");
                img=c.getString("imgData");

                amount.setText(a);
                rem.setText(r);
                type.setText(t);
                amount.setEnabled(false);
                rem.setEnabled(false);
                type.setEnabled(false);
//                img=c.getString("imageData");

//
//
//
//

                byte[] decodedString = Base64.decode(img, Base64.NO_WRAP | Base64.URL_SAFE);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imag.setImageBitmap(decodedByte);



            } catch (JSONException e) {


                e.printStackTrace();
            }





            if(progressDialog!=null)
            {
                progressDialog.dismiss();
            }
            if(TextUtils.isEmpty(a))
            {
                Toast.makeText(ApproveForm.this, "Invalid Entry", Toast.LENGTH_LONG).show();

            }
            else
            {
            }
        }

        private String postData(String urlPath) throws IOException,JSONException{


            StringBuilder result =new StringBuilder();
            BufferedWriter bufferedWriter=null;
            BufferedReader bufferedReader=null;
            StringBuilder builder;
            String inputLine,res;
            try{
                //building connection to the server
                URL url=new URL(urlPath);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(20000);
                urlConnection.setConnectTimeout(20000);

                urlConnection.setRequestMethod("POST");

                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(urlConnection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder

                res = stringBuilder.toString();
            }catch (Exception e){
                res = null;

                e.printStackTrace();
            }
            finally {


                if(bufferedReader!=null)
                    bufferedReader.close();
                if(bufferedWriter!=null)
                    bufferedWriter.close();

            }


            return res;


        }
    }
}
