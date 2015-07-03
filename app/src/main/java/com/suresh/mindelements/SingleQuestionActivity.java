package com.suresh.mindelements;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.suresh.utility.HelperService;
import com.suresh.utility.ServerRequestTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingleQuestionActivity extends ActionBarActivity {

    TextView questionNumberLabel;
    TextView questionSetTotalValueLabel;
    TextView totalQuestionLabel;
    TextView numberOfSetsDoneLabel;
    TextView answerLabel;
    Button nextQuestionButton;
    Map<String,String> selection;
    Map<String,String> revSelection;

    TextView questionLabel;

    public static String QUESTION_TYPE = "";
    public static String QUESTION_STATUS = "";
    public static String SESSION_ID = "";
    public static String MEMBER_ID = "";
    public static String ANSWER = "";
    public static String QUESTION_NUMBER = "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_question);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Question Details");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
        Map questionBucketDetails = (Map) hashMap.get("questionBucketDetails");

        /**
         * Initialisation of variables
         */
        questionLabel = (TextView) findViewById(R.id.questionLabel);
        questionNumberLabel = (TextView) findViewById(R.id.questionNumberLabel);
        questionSetTotalValueLabel = (TextView) findViewById(R.id.questionSetTotalValueLabel);
        totalQuestionLabel = (TextView) findViewById(R.id.totalQuestionLabel);
        numberOfSetsDoneLabel = (TextView) findViewById(R.id.numberOfSetsDoneLabel);
        answerLabel = (TextView) findViewById(R.id.answerLabel);
        nextQuestionButton = (Button) findViewById(R.id.nextQuestionButton);

        this.selection = (Map) hashMap.get("selection");
        this.QUESTION_TYPE = hashMap.get("questionType").toString();
        this.QUESTION_STATUS = hashMap.get("status").toString();
        this.SESSION_ID = hashMap.get("sessionId").toString();
        this.MEMBER_ID = hashMap.get("memberId").toString();
        this.QUESTION_NUMBER = hashMap.get("questionNumber").toString();

        revSelection = new HashMap<String,String>();
        for(Map.Entry<String,String> entry : selection.entrySet())
            revSelection.put(entry.getValue(), entry.getKey());

        /**
         * Set text to the labels initiated earlier
         */
        questionLabel.setText(hashMap.get("question").toString());

        questionNumberLabel.setText("Question number : "+questionBucketDetails.get("questionNumber"));
        questionSetTotalValueLabel.setText("Question set total value : "+questionBucketDetails.get("questionSetTotalValue"));
        totalQuestionLabel.setText("Total question : "+questionBucketDetails.get("totalQuestion"));
        numberOfSetsDoneLabel.setText("Number of sets done : " + questionBucketDetails.get("numberOfSetsDone"));

        if(QUESTION_TYPE.equals("")){
            displayOutOfQuestionView();
        }else{
            if(QUESTION_TYPE.equalsIgnoreCase("single"))
                addRadioButtons();
            else
                addCheckBoxes();
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
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                break;
            case R.id.questionToolMenu:
                Intent intent = new Intent(SingleQuestionActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(SingleQuestionActivity.this, QuizActivity.class);
                startActivity(intent2);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(SingleQuestionActivity.this, AboutActivity.class);
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
        return true;

    }
    public void displayOutOfQuestionView(){

        View a = findViewById(R.id.radiogroup);
        View b = findViewById(R.id.gridLayout);
        View c = findViewById(R.id.questionLabel);
        View d = findViewById(R.id.textView6);
        a.setVisibility(View.GONE);b.setVisibility(View.GONE);c.setVisibility(View.GONE);d.setVisibility(View.GONE);

        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout2);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(this);
        tv.setText("Total question set reached. Review wrong answer.");
        tv.setId(1 + 1);
        tv.setPadding(15, 20, 0, 0);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(20);
        tv.setTextColor(Color.rgb(255,255,255));
        ll.addView(tv);

        Button button = new Button(this);
        button.setText("Review Answers");
        button.setPadding(15, 20, 0, 0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ll.addView(button);

    }

    /**
     * For displaying radio button for question with single answer
     */
    public void addRadioButtons() {

        Log.d(getClass().getName(), "Options Map: -------------->>" + selection.keySet());
        LinearLayout ll = new LinearLayout(this);
        RadioGroup rg = (RadioGroup) findViewById(R.id.radiogroup);
        ll.setOrientation(LinearLayout.VERTICAL);
        int i=0;
        for(Object key:selection.keySet()){
            i++;
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId((1 * 2) + i);
            rdbtn.setText(selection.get(key.toString()).toString());
            rg.addView(rdbtn);
        }
        ((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);

    }

    /**
     * For displaying checkbox button for question with multiple answer
     */
    public void addCheckBoxes() {
        View b = findViewById(R.id.radiogroup);
        b.setVisibility(View.GONE);
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout2);
        ll.setOrientation(LinearLayout.VERTICAL);

        int i=0;
        for(Object key:selection.keySet()) {
            i++;
            CheckBox cb = new CheckBox(this);
            cb.setText(selection.get(key.toString()).toString());
            cb.setId(i + 6);
            ll.addView(cb);
        }
    }

    /**
     *
     * @param v
     * Calls method for retrieving next question in background
     */
    public void getNextQuestion(View v){

        new ServerRequestTask("Please wait...", this, this)
                .execute("getNextQuestion");

    }

    /**
     * Method which is executed in background
     */
    public void getQuestionInBackground() {
        Log.d(getClass().getName(), "Fetching question-------------->>");
        String requestURL = "https://portal-mindelements.rhcloud.com:443/question-rest/rest//questions/getNextQuestion/"+MEMBER_ID+"/"+SESSION_ID;
        try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(requestURL);
            HttpResponse response;
            response = httpclient.execute(httpGet);

            int responseCode = response.getStatusLine().getStatusCode();
            String reponseMessage = response.getStatusLine().getReasonPhrase();

            Log.d(getClass().getName(),"Response Code for Next question -------------->>"+responseCode);
            Log.d(getClass().getName(),"Response Message Response Code for Next question-------------->>"+reponseMessage);

            String firstResponse2 = EntityUtils.toString(response.getEntity());
            JSONObject mainObject = new JSONObject(firstResponse2);
            /**
             * Converts JSONObject to Map
             */
            HashMap map  =  HelperService.jsonToMap(mainObject);

            if(responseCode==201) {
                QUESTION_STATUS = map.get("status").toString();
                map.put("memberId", MEMBER_ID);
                Intent intent = new Intent(SingleQuestionActivity.this, SingleQuestionActivity.class);
                intent.putExtra("dataMap", map);
                startActivity(intent);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     *
     * @param v
     * Calls method for retrieving next question in background
     */
    public void checkAnswer(View v){

        new ServerRequestTask("Please wait...", this, this)
                .execute("checkAnswer");

    }

    public void processAfterCheckAnswer(){
        if(!answerLabel.getText().toString().equals("")){
            nextQuestionButton.setClickable(true);
        }else{
            answerLabel.setText("Please provide answer...");
        }
    }

    /**
     * Method for checking answer in background
     */
    public void checkAnswerInBackground(){
        ANSWER = "";

        doAnswerCheck();
        /**
         * If there is no answer given do not send request
         */
        Log.d(getClass().getName(),"ANSWER -------------->> "+ANSWER);
        if(ANSWER=="")return;
        String requestURL = "https://portal-mindelements.rhcloud.com:443/question-rest/rest//questions/checkAnswer/"+ANSWER+"/"+MEMBER_ID+"/"+SESSION_ID+"/"+QUESTION_NUMBER;
        try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(requestURL);
            HttpResponse response;
            response = httpclient.execute(httpGet);

            int responseCode = response.getStatusLine().getStatusCode();
            String reponseMessage = response.getStatusLine().getReasonPhrase();

            Log.d(getClass().getName(),"Response Code for Answer check -------------->> "+responseCode);
            Log.d(getClass().getName(),"Response Message Response Code for Answer check-------------->> "+reponseMessage);

            String firstResponse2 = EntityUtils.toString(response.getEntity());
            JSONObject mainObject = new JSONObject(firstResponse2);
            /**
             * Converts JSONObject to Map
             */
            HashMap map  =  HelperService.jsonToMap(mainObject);

            if(responseCode == 201) {
                Log.d(getClass().getName(), "JSON for Answer check-------------->>" + firstResponse2);
                answerLabel.setText(map.get("answer").toString());
            }

        } catch (Exception ex) {
            System.err.println(ex);
        }

    }

    /**
     * Checks single answer from radio button and multiple answer from checkboxes
     */
    private void doAnswerCheck(){
        if(QUESTION_TYPE.equalsIgnoreCase("single")){
            RadioGroup rg=(RadioGroup)findViewById(R.id.radiogroup);
            RadioButton rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
            /**
             * If none of the option is selected
             */
            if(rb == null){
                Log.d(getClass().getName(),"None of the radio button is checked");
                return;
            }
            ANSWER = revSelection.get(rb.getText().toString());
        }
        if(QUESTION_TYPE.equalsIgnoreCase("multi")){
            ANSWER = "";
            for(int i=1 ;i<=4 ;i++){
                CheckBox checkBox = (CheckBox) findViewById(i*1+6);
                boolean flag = checkBox.isChecked();
                String value = checkBox.getText().toString();
                Log.d(getClass().getName(),"Value: "+value+" is checked-------------->> "+flag);
                if(flag){
                    if(!ANSWER.equals("")) ANSWER+=",";
                    ANSWER+=revSelection.get(value);
                }
            }
        }
    }

}