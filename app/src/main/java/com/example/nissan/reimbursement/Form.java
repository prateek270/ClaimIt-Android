package com.example.nissan.reimbursement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Form extends AppCompatActivity {
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    MaterialBetterSpinner materialBetterSpinner ;

    String[] SPINNER_DATA = {"Travel","Food","Others"};
    private Button btn;
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/reimbursement";
    private int GALLERY = 1, CAMERA = 2;
    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA","android.permission.READ_EXTERNAL_STORAGE"};
    String saveAsDraftUrl;
    String spin,encodedImage ;
    EditText amt,rem;
    int flag=0;
    byte[] imageBytes;
    Context context;
    String zid,newzid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        newzid=pref.getString("ZID", null);

        saveAsDraftUrl="http://192.168.43.170:8081/internalapp/reimbursements/push/"+newzid;

        materialBetterSpinner = (MaterialBetterSpinner)findViewById(R.id.material_spinner1);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Form.this, android.R.layout.simple_dropdown_item_1line, SPINNER_DATA);

        materialBetterSpinner.setAdapter(adapter);

        materialBetterSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                spin = adapterView.getItemAtPosition(position).toString();
            }
        });





        amt=(EditText)findViewById(R.id.amount);
        rem=(EditText)findViewById(R.id.remarks);


        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Log.e("xxyz","value "+spin);
                if(TextUtils.isEmpty(amt.getText()))
                    Toast.makeText(Form.this,"Enter Amount",Toast.LENGTH_LONG).show();
                else if(TextUtils.isEmpty(rem.getText()))
                    Toast.makeText(Form.this,"Enter Remark",Toast.LENGTH_LONG).show();
                else if(TextUtils.isEmpty(spin))
                    Toast.makeText(Form.this,"Select Type",Toast.LENGTH_LONG).show();
                else if (flag==0)
                    Toast.makeText(Form.this,"Select Image",Toast.LENGTH_LONG).show();
                else
                new saveDraftTask().execute(saveAsDraftUrl);
            }
        });




        imageview = (ImageView) findViewById(R.id.cimage);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(Form.this,perms,1);
                showPictureDialog();
            }
        });
    }
    class saveDraftTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog progressDialog;


        String amount="Rs. "+amt.getText().toString();
        final String remarks=rem.getText().toString();
        String spinner=spin;
        String status="",message,name,img;
        int id;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("xxyz 1","value "+amount+" "+remarks);
            progressDialog=new ProgressDialog(Form.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            //progressDialog.dismiss();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
               // Log.e("xxyz 2","value 1 "+amount+" "+remarks);
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
               // Log.d("kush","status1"+status);

            } catch (JSONException e) {
              //  Log.d("kush","catch");


                e.printStackTrace();
            }





            if(progressDialog!=null)
            {
                progressDialog.dismiss();
            }
            if(TextUtils.isEmpty(status))
            {
                Toast.makeText(Form.this, "Select Proper Image", Toast.LENGTH_LONG).show();

            }
            else
            {

                Intent ii=new Intent(Form.this,ClaimitActivity.class);
                ii.putExtra("zid",zid);
                startActivity(ii);
                finish();
            }
        }

        private String postData(String urlPath) throws IOException,JSONException{


            StringBuilder result =new StringBuilder();
            BufferedWriter bufferedWriter=null;
            BufferedReader bufferedReader=null;
            StringBuilder builder;
            try{
                JSONObject dataToSend=new JSONObject();


                dataToSend.put("type",spinner);
                dataToSend.put("img",encodedImage);
                dataToSend.put("amount",amount);
                dataToSend.put("remark",remarks);

                //building connection to the server
                URL url=new URL(urlPath);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(20000);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                //urlConnection.connect();
               // Log.d("qqqq ",dataToSend.toString());

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

           // Log.d("kush","result"+result.toString());
            return result.toString();


        }
    }



    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera",
                "Remove Image"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                            case 2:
                                removeImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }
    public void removeImage() {
        flag=0;
        String uri = "@drawable/cameraicon";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);

        imageview.setImageDrawable(res);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePhotoFromCamera() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.nissan.reimbursement",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                   // Log.d("xxx",bitmap+" ");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageBytes = baos.toByteArray();
                   // Log.d("xxx",imageBytes+" ");

                    encodedImage = Base64.encodeToString(imageBytes,  Base64.NO_WRAP | Base64.URL_SAFE);

                    String path = saveImage(bitmap);

                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Form.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            imageview.setImageBitmap(thumbnail);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            imageBytes = baos.toByteArray();
//            encodedImage = Base64.encodeToString(imageBytes,  Base64.NO_WRAP | Base64.URL_SAFE);
//            saveImage(thumbnail);

            try
            {
                if (resultCode == RESULT_OK) {
                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmap = MediaStore.Images.Media
                            .getBitmap(context.getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) {
                        Log.d("cool","bloody fool");
                        imageview.setImageBitmap(bitmap);
                    }
                }
            }
            catch (Exception error) {
                error.printStackTrace();
            }

        }
    }

    public String saveImage(Bitmap myBitmap) {
        flag=1;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
           // Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
}
