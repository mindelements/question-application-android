package com.suresh.mindelements;

import android.content.Intent;
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
    Map selection;

    TextView questionLabel;

    public static String QUESTION_TYPE = "";
    public static String QUESTION_STATUS = "";
    public static String SESSION_ID = "";
    public static String MEMBER_ID = "";


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

        this.selection = (Map) hashMap.get("selection");
        this.QUESTION_TYPE = hashMap.get("questionType").toString();
        this.QUESTION_STATUS = hashMap.get("status").toString();
        this.SESSION_ID = hashMap.get("sessionId").toString();
        this.MEMBER_ID = hashMap.get("memberId").toString();

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
        getMenuInflater().inflate(R.menu.menu_single_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            cb.setId(i+6);
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
    public void getQuestionInBackground(){
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
}
