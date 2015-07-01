package com.suresh.mindelements;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suresh.utility.HelperService;
import com.suresh.utility.ServerRequestTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class QuestionActivity extends ActionBarActivity {

    Button selectFileButton;
    EditText memberIdField;
    String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Question");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void chooseFile(View v) {

        selectFileButton = (Button) findViewById(R.id.selectFileButton);
        File mPath = new File(Environment.getExternalStorageDirectory() + "/s");
        FileDialog fileDialog = new FileDialog(QuestionActivity.this, mPath);
        fileDialog.setFileEndsWith(".xlsx");
        fileDialog.setFileEndsWith(".xls");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
                fileName = file.toString();
                selectFileButton.setText("File Selected");
                selectFileButton.setTextColor(Color.rgb(0,150,0));
                try {
                    File f = new File(file.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        fileDialog
                .addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(File directory) {
                        Log.d(getClass().getName(),
                                "Selected dir " + directory.toString());
                    }
                });
        fileDialog.setSelectDirectoryOption(false);
        fileDialog.showDialog();
    }

    public void uploadFile(View v){

        new ServerRequestTask("Uploading file..... Please wait", this, this)
                .execute("uploadFile");
    }

    public void uploadFileInBackground(){

        memberIdField = (EditText) findViewById(R.id.memberIdField);
        String memberId = memberIdField.getText().toString().trim();
        Log.d(getClass().getName(),"Selected dir "+fileName);
        try {
            File f = new File(fileName);
            String requestURL = "https://portal-mindelements.rhcloud.com/question-rest/rest//questions/getFirstQuestion/"+memberId+"/inputFile";

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPut httpost = new HttpPut(requestURL);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("inputFile", new FileBody(f));
                httpost.setEntity(entity);

                HttpResponse response;
                response = httpclient.execute(httpost);
                int responseCode = response.getStatusLine().getStatusCode();
                String reponseMessage = response.getStatusLine().getReasonPhrase();

                Log.d(getClass().getName(),"Response Code -------------->>"+responseCode);
                Log.d(getClass().getName(),"Response Message-------------->>"+reponseMessage);

                String firstResponse = EntityUtils.toString(response.getEntity());

                JSONObject mainObject = new JSONObject(firstResponse);
                /**
                 * Converts JSONObject to Map
                 */
                HashMap map  =  HelperService.jsonToMap(mainObject);
                if(responseCode==201){
                    map.put("memberId",memberId);
                    Intent intent = new Intent(QuestionActivity.this, SingleQuestionActivity.class);
                    intent.putExtra("dataMap", map);
                    startActivity(intent);
                }

            } catch (Exception ex) {
                System.err.println(ex);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
