package net.mindelements.thinker;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Parcelable;
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
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import net.mindelements.thinker.utility.HelperService;
import net.mindelements.thinker.utility.ServerRequestTask;
import net.mindelements.thinker.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionActivity extends ActionBarActivity {

    Button selectFileButton;
    EditText memberIdField;
    String fileName = "";
    String parent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Question");
        actionBar.setDisplayHomeAsUpEnabled(true);

        EditText ed1 = (EditText) findViewById(R.id.memberIdField);
        ed1.setText(String.valueOf(HelperService.randInt(1000, 9999)));

        String root = getIntent().getStringExtra("Activity");
        if(getIntent().getExtras() != null){
            if(root.equalsIgnoreCase("quiz"))
                parent = "QUIZ";
            else if(root.equalsIgnoreCase("quizlisten"))
                parent = "QUIZLISTEN";
            else if(root.equalsIgnoreCase("flashcard"))
                parent = "FLASHCARD";

            TextView tv = (TextView) findViewById(R.id.textView4);
            tv.setText("Message : Practice question tool");


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){

            case R.id.mainMenu:
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                break;
            case R.id.questionToolMenu:
                Intent intent = new Intent(QuestionActivity.this, QuestionActivity.class);
                startActivity(intent);
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(QuestionActivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.quizLIstenToolMenu:
                Intent intentListen = new Intent(QuestionActivity.this, QuestionActivity.class);
                intentListen.putExtra("Activity", "quizlisten");
                startActivity(intentListen);
                break;
            case R.id.flashCardToolMenu:
                Intent flashCard = new Intent(QuestionActivity.this, QuestionActivity.class);
                flashCard.putExtra("Activity", "flashcard");
                startActivity(flashCard);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(QuestionActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.quitMenu:
                Intent quit = new Intent(getApplicationContext(), MainActivity.class);
                quit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                quit.putExtra("EXIT", true);
                startActivity(quit);
                return true;
            case R.id.helpMenu:
                Intent intent4 = new Intent(QuestionActivity.this, HelpActivity.class);
                startActivity(intent4);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        String message = "";
        memberIdField = (EditText) findViewById(R.id.memberIdField);
        String memberId = memberIdField.getText().toString();
        if(memberId.equals("") || fileName.equals("")){
            if(memberId.equals("") && fileName.equals("")){
                message = "Please enter required info...";
            }else if(memberId.equals("")){
                message = "Please enter Member ID...";
            }else if(fileName.equals("")){
                message = "Please select file...";
            }
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
            return;
        }
        new ServerRequestTask("Uploading file..... Please wait", this, this)
                .execute("uploadFile");
    }

    public void uploadFileInBackground(){

        JSONArray jsonArray = null;
        JSONObject mainObject = null;

        memberIdField = (EditText) findViewById(R.id.memberIdField);
        String memberId = memberIdField.getText().toString().trim();
        Log.d(getClass().getName(), "Selected dir " + fileName);
        try {
            File f = new File(fileName);
            String requestURL = "";
            if(parent.equalsIgnoreCase("QUIZ") || parent.equalsIgnoreCase("QUIZLISTEN") || parent.equalsIgnoreCase("FLASHCARD")){
                requestURL = "https://portal-mindelements.rhcloud.com/question-rest/rest//quiz/getQuizQuestions/"+memberId+"/inputFile";
            }else{
                requestURL = "https://portal-mindelements.rhcloud.com/question-rest/rest//questions/getFirstQuestion/"+memberId+"/inputFile";
            }

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



                if(parent.equalsIgnoreCase("QUIZ") || parent.equalsIgnoreCase("QUIZLISTEN")|| parent.equalsIgnoreCase("FLASHCARD")){
                    jsonArray = new JSONArray(firstResponse);
                    mainObject = new JSONObject();
                    mainObject.put("datas",jsonArray);
                }else{
                    mainObject = new JSONObject(firstResponse);
                }
                /**
                 * Converts JSONObject to Map
                 */
                HashMap<String, Object> map  =  HelperService.jsonToMap(mainObject);

                if(parent.equalsIgnoreCase("QUIZLISTEN") || parent.equalsIgnoreCase("FLASHCARD")){
                    List allQuestionDetails = (List) map.get("datas");
                    Map singleQuestionDetails = (Map) allQuestionDetails.get(0);
                    String MEMBER_NUMBER = singleQuestionDetails.get("memberNumber").toString();
                    String SESSION_ID = singleQuestionDetails.get("sessionId").toString();
                    submitQuizAnswersInBackground(MEMBER_NUMBER, SESSION_ID);
                    return;
                }

                if(responseCode==201){

                    if(parent.equalsIgnoreCase("QUIZ")){
                        map.put("memberId",memberId);
                        Intent intent = new Intent(QuestionActivity.this, QuizActivity.class);
                        intent.putExtra("dataMap", HelperService.maptoString(map));
                        startActivity(intent);
                    }else{
                        map.put("memberId", memberId);
                        Intent intent = new Intent(QuestionActivity.this, SingleQuestionActivity.class);
                        intent.putExtra("dataMap", HelperService.maptoString(map));
                        startActivity(intent);
                    }
                }

            } catch (Exception ex) {
                System.err.println(ex);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void submitQuizAnswersInBackground(String memberNumber , String sessionId){

        JSONArray jsonArray = null;
        JSONObject mainObject = null;

        String requestURL  = "https://portal-mindelements.rhcloud.com:443/question-rest/rest//quiz/getQuizResult/"+memberNumber+"/"+sessionId;
        Log.d(getClass().getName(), "Submitting all answer to server of quiz------------->>URL : " + requestURL);
        try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(requestURL);
            HttpResponse response;
            response = httpclient.execute(httpGet);

            int responseCode = response.getStatusLine().getStatusCode();
            String reponseMessage = response.getStatusLine().getReasonPhrase();

            Log.d(getClass().getName(),"Response Code for Submitting Quiz  answer -------------->>"+responseCode);
            Log.d(getClass().getName(),"Response Message Submitting Quiz answer-------------->>"+reponseMessage);

            String firstResponse2 = EntityUtils.toString(response.getEntity());
            jsonArray = new JSONArray(firstResponse2);
            mainObject = new JSONObject();
            mainObject.put("datas", jsonArray);
            /**
             * Converts JSONObject to Map
             */
            HashMap<String, Object> map  =  HelperService.jsonToMap(mainObject);

            if(responseCode==201) {
                Intent intent = null;
                if(parent.equalsIgnoreCase("FLASHCARD"))
                    intent = new Intent(QuestionActivity.this, FlashCardActivity.class);
                else
                    intent = new Intent(QuestionActivity.this, QuizListenActivity.class);

                intent.putExtra("dataMap", HelperService.maptoString(map));
                startActivity(intent);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

    }

}
