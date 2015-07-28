package net.mindelements.thinker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;



import net.mindelements.thinker.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class QuizListenActivity extends ActionBarActivity {

    TableLayout tableLayout;
    int totalCorrectAnswerCount = 0;
    int QUESTION_COUNTER = 0;
    int dataSize;
    TextToSpeech speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_listen);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Quiz Listen");
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * Initialis TextToSpeech instance
         */
        speaker=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.UK);
                }
            }
        });
        /**
         * Get map of data from parent activity
         */
        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
        List quizResultList = (List) hashMap.get("datas");
        tableLayout = (TableLayout) findViewById(R.id.mainTable);
        dataSize = quizResultList.size();

        int k=0;
        for(Object m : quizResultList){
            Map quizResult = (Map) quizResultList.get(k);
            Map<String,String> selection = (Map)quizResult.get("selection");

            final String speechQuestion = "Question number "+(dataSize-QUESTION_COUNTER)+" , "+quizResult.get("question").toString();
            final String[] speechOptions = new String[4];

            String questionType = quizResult.get("questionType").toString();
            String temp = "" ;

            if(questionType.equalsIgnoreCase("single")){
                String correctOption = quizResult.get("answer").toString();
                String tempSpeechAnswer = selection.get(correctOption);
                temp = "The answer is , "+quizResult.get("answer").toString()+" , "+tempSpeechAnswer;
            }else{
                String[] answers = quizResult.get("answer").toString().split(",");
                StringBuilder sb = new StringBuilder();
                for(String ans : answers){
                    sb.append(ans+" , "+selection.get(ans)+" ");
                }
                temp = "The answer are , "+sb.toString();
            }

            final String speechAnswer = temp;
            final String question = (dataSize-QUESTION_COUNTER)+"."+quizResult.get("question").toString();
            StringBuilder optionBuilder = new StringBuilder();
            int countOption = 0;
            for (Map.Entry<String, String> entry : selection.entrySet()) {
                speechOptions[countOption] = entry.getKey()+" , "+entry.getValue();
                countOption++;
                optionBuilder.append(entry.getKey() + " : " + entry.getValue());
                if(countOption<4)
                    optionBuilder.append("\n");
            }

            String options = optionBuilder.toString();
            String actualAnswer = "Answer : "+quizResult.get("answer").toString()+"\n\n";

            TableLayout ll = (TableLayout) findViewById(R.id.mainTable);
            String[] displayItem = {question,options,actualAnswer};
            /**
             * Row for question
             */
            int i = 0;
            for(String item : displayItem){
                TableRow questionRow = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                questionRow.setLayoutParams(lp);
                TextView label = new TextView(this);
                label.setText(item);
                label.setPadding(10, 0, 0, 0);
                label.setTextColor(Color.rgb(255, 255, 255));
                label.setWidth(getWindowManager().getDefaultDisplay().getWidth()-10);

                label.setTextSize(18);
                questionRow.addView(label);
                ll.addView(questionRow, i);
                i++;
            }

            TableRow questionRow = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            questionRow.setLayoutParams(lp);
            Button label = new Button(this);
            label.setText("Read");
            label.setPadding(10, 0, 0, 0);
            label.setTextColor(Color.rgb(255, 255, 255));
            label.setWidth(200);
            label.setTextSize(18);
            label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ServerRequestTask().execute("speak",speechQuestion,speechOptions[0],speechOptions[1],speechOptions[2],speechOptions[3],speechAnswer);
                }
            });
            questionRow.addView(label);
            ll.addView(questionRow, i);
            i++;
            k++;
            QUESTION_COUNTER++;
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
                Intent intent = new Intent(QuizListenActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(QuizListenActivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(QuizListenActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.quitMenu:
                Intent quit = new Intent(getApplicationContext(), MainActivity.class);
                quit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                quit.putExtra("EXIT", true);
                startActivity(quit);
                return true;
            case R.id.helpMenu:
                Intent intent4 = new Intent(QuizListenActivity.this, HelpActivity.class);
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

    public class ServerRequestTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        /**
         *
         * Other constructor can be added here along with the activity
         */
        public ServerRequestTask() {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String invokingMethod = params[0];
            String returnValue = "";

            switch (invokingMethod) {
                case "speak":
                    for(int i=1 ; i<=6 ;i++){
                        speaker.speak(params[i], TextToSpeech.QUEUE_ADD, null);
                        if(i==6){
                            speaker.playSilence(1500, TextToSpeech.QUEUE_ADD, null);
                        }else{
                            speaker.playSilence(500, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                    break;

                default:
                    break;
            }
            return returnValue;
        }

        protected void onPostExecute(String params) {
            super.onPostExecute(params);
        }

    }
}
