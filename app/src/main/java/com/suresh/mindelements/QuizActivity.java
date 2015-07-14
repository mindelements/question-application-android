package com.suresh.mindelements;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.suresh.utility.HelperService;
import com.suresh.utility.ServerRequestTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuizActivity extends ActionBarActivity {

    public static String QUESTION_TYPE = "";
    public static String QUESTION_NUMBER = "";
    public static String MEMBER_NUMBER = "";
    public static String SESSION_ID = "";
    public static String ANSWER = "";
    Map<String,String> selection;
    Map<String,String> revSelection;
    TableLayout tableLayout;
    int QUESTION_COUNTER = 0;
    int dataSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Quiz");
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * Get map of data from parent activity
         */
        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
        List allQuestionDetails = (List) hashMap.get("datas");
        tableLayout = (TableLayout) findViewById(R.id.mainTable);
        dataSize = allQuestionDetails.size();



        Log.d(getClass().getName(), "Selected ID:  " + allQuestionDetails);


        int i=0;
        for(Object m : allQuestionDetails){
            Map singleQuestionDetails = (Map) allQuestionDetails.get(i);

            QUESTION_TYPE = singleQuestionDetails.get("questionType").toString();
            QUESTION_NUMBER = singleQuestionDetails.get("questionNumber").toString();
            MEMBER_NUMBER = singleQuestionDetails.get("memberNumber").toString();
            SESSION_ID = singleQuestionDetails.get("sessionId").toString();

            selection = (Map)singleQuestionDetails.get("selection");
            revSelection = new HashMap<String,String>();
            for(Map.Entry<String,String> entry : selection.entrySet())
                revSelection.put(entry.getValue(), entry.getKey());

            if(QUESTION_TYPE.equalsIgnoreCase("single")){
                addRadioButtons(singleQuestionDetails,selection,revSelection,QUESTION_NUMBER);
            }else{
                List answerList = new ArrayList();
                addCheckBoxes(singleQuestionDetails,selection,revSelection,answerList,QUESTION_NUMBER);
            }
            i++;
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
                Intent intent = new Intent(QuizActivity.this, QuestionActivity.class);
                startActivity(intent);
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(QuizActivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(QuizActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.quitMenu:
                Intent quit = new Intent(getApplicationContext(), MainActivity.class);
                quit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                quit.putExtra("EXIT", true);
                startActivity(quit);
                return true;
            case R.id.helpMenu:
                Intent intent4 = new Intent(QuizActivity.this, HelpActivity.class);
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

    /**
     * For displaying radio button for question with single answer
     */
    public void addRadioButtons(Map singleQuestionDetails,final Map selection,final Map revSelection,final String questionNumber) {
        TableLayout ll = (TableLayout) findViewById(R.id.mainTable);
        int i=0;
        /**
         * Row for question
         */
        TableRow questionRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        questionRow.setLayoutParams(lp);
        TextView questionLabel = new TextView(this);
        questionLabel.setText((dataSize - QUESTION_COUNTER) + "." + singleQuestionDetails.get("question").toString());
        questionLabel.setPadding(10, 0, 0, 0);
        questionLabel.setTextColor(Color.rgb(255, 255, 255));
        questionLabel.setTextSize(20);
        questionLabel.setWidth(getWindowManager().getDefaultDisplay().getWidth()-10);
        questionRow.addView(questionLabel);
        ll.addView(questionRow,i);
        i++;

        RadioGroup rg = new RadioGroup(this);
        int k = 99;
        for(Object key : selection.keySet()){
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId((1 * 2) + k);
            rdbtn.setText(selection.get(key.toString()).toString());
            rg.addView(rdbtn);
            k--;
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(getClass().getName(), "Selected ID:  " + checkedId);
                RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
                Log.d(getClass().getName(), "Selected Text:  " + radioButton.getText());
                ANSWER = revSelection.get(radioButton.getText()).toString();
                uploadAnswerToServer(questionNumber);

            }
        });

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp2);
        row.addView(rg);
        ll.addView(row,i);
        QUESTION_COUNTER++;
    }


    /**
     * For displaying checkbox button for question with multiple answer
     */
    public void addCheckBoxes(Map singleQuestionDetails,final Map selection,final Map revSelection,final List answerList,final String questionNumber) {

        final TableLayout ll = (TableLayout) findViewById(R.id.mainTable);
        int i=0;
        String answer = "";
        /**
         * Row for question
         */
        TableRow questionRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        questionRow.setLayoutParams(lp);
        TextView questionLabel = new TextView(this);
        questionLabel.setText((dataSize-QUESTION_COUNTER)+"."+singleQuestionDetails.get("question").toString());
        questionLabel.setPadding(10, 0, 0, 0);
        questionLabel.setTextColor(Color.rgb(255, 255, 255));
        questionLabel.setTextSize(20);
        questionLabel.setHorizontallyScrolling(false);
        questionRow.addView(questionLabel);
        ll.addView(questionRow, i);
        i++;

//        ANSWER = "";
        for(Object key : selection.keySet()){
            CheckBox cb = new CheckBox(this);
            cb.setText(selection.get(key.toString()).toString());
            cb.setId(i + 6);

            cb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String value = (String) ((CheckBox) v).getText();
                    Log.d(getClass().getName(), "REV-------------->> " + revSelection);
                    Log.d(getClass().getName(), "SEL-------------->> " + selection);
                    if (((CheckBox) v).isChecked()) {
                        answerList.add(revSelection.get(value));
                    } else {
                        answerList.remove(revSelection.get(value));
                    }
                    ANSWER = TextUtils.join(",",answerList);
                    uploadAnswerToServer(questionNumber);
                    Log.d(getClass().getName(), "ANSWER-------------->> " + answerList);

                }
            });
            ll.addView(cb,i);
        }
    QUESTION_COUNTER++;
    }


    public void uploadAnswerToServer(String questionNumber){

        new ServerRequestTask("uploadAnswer", this, this)
                .execute("uploadAnswer:"+questionNumber);

    }

    public void uploadAnswerInBackground(String questionNumber){
        String requestURL = "https://qa1-mindelements.rhcloud.com/question-web/app/quiz/save/"+questionNumber+"/"+ANSWER+"/"+MEMBER_NUMBER+"/"+SESSION_ID;
        Log.d(getClass().getName(), "Uploading answer to server------------->>URL : " + requestURL);
        try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(requestURL);
            HttpResponse response;
            response = httpclient.execute(httpGet);

            int responseCode = response.getStatusLine().getStatusCode();
            String reponseMessage = response.getStatusLine().getReasonPhrase();

            Log.d(getClass().getName(),"Response Code for Upload  answer -------------->>"+responseCode);
            Log.d(getClass().getName(),"Response Message Upload answer-------------->>"+reponseMessage);

            String firstResponse2 = EntityUtils.toString(response.getEntity());
            JSONObject mainObject = new JSONObject(firstResponse2);
            /**
             * Converts JSONObject to Map
             */
            HashMap map  =  HelperService.jsonToMap(mainObject);

            if(responseCode==200) {
                Log.d(getClass().getName(), "Map -------------->>" + map);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

    }

    public void submitQuizAnswers(View v){
        Log.d(getClass().getName(), "Submitting all quiz answers-------------->>");
        new ServerRequestTask("Submitting answers...Please wait", this, this)
                .execute("submitQuizAnswers");
    }

    public void submitQuizAnswersInBackground(){

        JSONArray jsonArray = null;
        JSONObject mainObject = null;

        String requestURL  = "https://portal-mindelements.rhcloud.com:443/question-rest/rest//quiz/getQuizResult/"+MEMBER_NUMBER+"/"+SESSION_ID;
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
            HashMap map  =  HelperService.jsonToMap(mainObject);

            if(responseCode==201) {
                Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
                intent.putExtra("dataMap", map);
                startActivity(intent);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

    }


}
