package com.example.zeeshan.asynctask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static final Integer LOCATION = 0x1;
    static final Integer CALL = 0x2;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    static final Integer CAMERA = 0x5;
    static final Integer ACCOUNTS = 0x6;
    static final Integer GPS_SETTINGS = 0x7;
    String myData = "";

    final File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestFile.txt");

    Button buttonAdd, buttonDelete;
    EditText editTextForSave;
    TextView textViewForShowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextForSave = (EditText) findViewById(R.id.ed_enter_data);
        textViewForShowData = (TextView) findViewById(R.id.tv_show_data);

        buttonAdd = (Button) findViewById(R.id.button_add);
        buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonAdd.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void ask(View v){
        switch (v.getId()){

            case R.id.button_add:
                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                new MyAsynTask().execute();

                break;
            case R.id.button_delete:
                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
                deleteFile();
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {

                case 3:
                    writeFile();
                    break;
                //Read External Storage
                case 4:
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent, 11);
                    break;
            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        ask(v);
    }

    public void writeFile(){
        // write on SD card file data in the text box
        try {
            // buttonAdd.setEnabled(false);

            FileOutputStream fOut = new FileOutputStream(myFile);
            final OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            String temp=editTextForSave.getText().toString();

            myOutWriter.append(temp);
            myOutWriter.append("\n");

            myOutWriter.close();
            fOut.close();
            Log.e("MA : ","Done writing SD");

        } catch (Exception e) {
            Log.e("catch : ",e.getMessage());
        }
//        editTextForSave.setText("");
        // buttonAdd.setEnabled(true);
    }

    public void showData(){
        try {
            FileInputStream fis = new FileInputStream(myFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =  new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + "\n" + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        textViewForShowData.setText(myData);
    }

    public void deleteFile(){
        myFile.delete();
        myData = "";
        textViewForShowData.setText("File no Longer Exist.");
        Log.e("MA : ","File Deleted");
    }

    // The definition of our task class
    private class MyAsynTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            writeFile();
            showData();
            return "All Done!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            buttonAdd.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            textViewForShowData.setText(myData);
            editTextForSave.setText("");
            buttonAdd.setEnabled(true);
        }
    }
}