package com.suresh.mindelements;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.suresh.utility.HelperService;
import com.suresh.utility.ServerRequestTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WrongAnswerAvtivity extends ActionBarActivity {
    TextView questionLabel;
    TextView optionLabel;
    TextView correctAnswerLabel;
    TextView messageLabel;
    Button nextWrongAnswerButton;

    Map<String,String> selection;
    Map<String,String> revSelection;
    public static HashMap REVIEW_MAP = new HashMap();

    public static String MEMBER_ID;
    public static String SESSION_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answer_avtivity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Answer Review");
        actionBar.setDisplayHomeAsUpEnabled(true);

        questionLabel = (TextView) findViewById(R.id.questionLabel);
        optionLabel = (TextView) findViewById(R.id.optionLabel);
        correctAnswerLabel = (TextView) findViewById(R.id.correctAnswerLabel);
        messageLabel = (TextView) findViewById(R.id.messageLabel);
        nextWrongAnswerButton = (Button) findViewById(R.id.nextWrongAnswerButton);


        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
        Log.d(getClass().getName(), "Received Map Size -------------->>" + hashMap.size());
        Log.d(getClass().getName(), "Received Map -------------->>" + hashMap);

        MEMBER_ID = hashMap.get("memberId").toString();
        SESSION_ID = hashMap.get("sessionId").toString();

        if(!hashMap.get("status").toString().equalsIgnoreCase("STATUS_NULL_QUESTIONS_NOT_ANSWERED")){
            selection = (Map) hashMap.get("selection");
            revSelection = new HashMap<String,String>();
            for(Map.Entry<String,String> entry : selection.entrySet())
                revSelection.put(entry.getValue(), entry.getKey());

            questionLabel.setText("Question:\n"+hashMap.get("question").toString()+"\n");
            optionLabel.setText("Selections:\n"+((Map)hashMap.get("selection")).values()+"\n");

            String[] answers = hashMap.get("answer").toString().split(",");
            List ansList = new ArrayList<>();
            for(String ans : answers){
                ansList.add(selection.get(ans));
            }
            correctAnswerLabel.setText("Answer/s:\n"+ansList+"\n");
        }else{
            questionLabel.setText("Question:\nNot Available\n");
            optionLabel.setText("Selections:\nNot Available\n");
            correctAnswerLabel.setText("Answer/s:\nNot Available\n");
            messageLabel.setText("You're done answering questions. Upload new questions to begin again.\n");
            nextWrongAnswerButton.setText("Upload");
            nextWrongAnswerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WrongAnswerAvtivity.this, QuestionActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){

            case R.id.mainMenu:
                break;
            case R.id.questionToolMenu:
                Intent intent = new Intent(WrongAnswerAvtivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(WrongAnswerAvtivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(WrongAnswerAvtivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.quitMenu:
                Intent quit = new Intent(getApplicationContext(), MainActivity.class);
                quit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                quit.putExtra("EXIT", true);
                startActivity(quit);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }
    public void reviewAllAnswer(View v){
        new ServerRequestTask("Reviewing answers...Please wait",this,this).execute("reviewAllAnswer2");
    }

    public void reviewAllAnswerInBackground(){

        Map<String,String> review = new HashMap<>();
        Log.d(getClass().getName(), "Fetching question-------------->>");
        String requestURL = "https://portal-mindelements.rhcloud.com:443/question-rest/rest//questions/getWrongAnswer/"+MEMBER_ID+"/"+SESSION_ID;
        try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(requestURL);
            HttpResponse response;
            response = httpclient.execute(httpGet);

            int responseCode = response.getStatusLine().getStatusCode();
            String reponseMessage = response.getStatusLine().getReasonPhrase();

            Log.d(getClass().getName(),"Response Code for Review All answer -------------->>"+responseCode);
            Log.d(getClass().getName(),"Response Message Review All answer-------------->>"+reponseMessage);

            String firstResponse2 = EntityUtils.toString(response.getEntity());
            JSONObject mainObject = new JSONObject(firstResponse2);
            /**
             * Converts JSONObject to Map
             */
            HashMap map  =  HelperService.jsonToMap(mainObject);

            if(responseCode==201) {
                Log.d(getClass().getName(), "Map -------------->>" + map);
                REVIEW_MAP = map;
                review.put("question", (String) map.get("question"));
                review.put("question", (String) map.get("question"));
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

    }

    public void processAfterReviewAnswer(){

        Intent myIntent = new Intent(getApplicationContext(), WrongAnswerAvtivity.class);
        REVIEW_MAP.put("memberId",MEMBER_ID);
        myIntent.putExtra("dataMap", REVIEW_MAP);
        startActivityForResult(myIntent, 0);
    }
}
