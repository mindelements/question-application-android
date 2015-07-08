package com.suresh.mindelements;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuizResultActivity extends ActionBarActivity {

    TableLayout tableLayout;
    int totalCorrectAnswerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Quiz Result");
        actionBar.setDisplayHomeAsUpEnabled(true);


        /**
         * Get map of data from parent activity
         */
        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
        List quizResultList = (List) hashMap.get("datas");
        tableLayout = (TableLayout) findViewById(R.id.mainTable);

        int k=0;
        for(Object m : quizResultList){
            Map quizResult = (Map) quizResultList.get(k);

            String question = quizResult.get("question").toString();
            Map<String,String> selection = (Map)quizResult.get("selection");
            StringBuilder optionBuilder = new StringBuilder();
            int countOption = 0;
            for (Map.Entry<String, String> entry : selection.entrySet()) {
                countOption++;
                optionBuilder.append(entry.getKey() + " : " + entry.getValue());
                if(countOption<4)
                    optionBuilder.append("\n");
            }

            String options = optionBuilder.toString();
            String actualAnswer = "Answer : "+quizResult.get("answer").toString();
            String memberAnswer = "Your answer : "+quizResult.get("memberAnswer").toString();

            String correct = "Correct : false";
            if(quizResult.keySet().contains("correct")){
                correct = "Correct : "+quizResult.get("correct").toString();
                totalCorrectAnswerCount++;
            }
            String explanation = "Explanation : "+quizResult.get("explanation").toString()+"\n\n";

            TableLayout ll = (TableLayout) findViewById(R.id.mainTable);

            String[] displayItem = {question,options,actualAnswer,memberAnswer,correct,explanation};
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
                if(correct.contains("false")){
                    label.setTextColor(Color.rgb(200, 30, 30));
                }
                label.setTextSize(18);
                questionRow.addView(label);
                ll.addView(questionRow, i);
                i++;
            }
            k++;
        }
        TextView totalCorrectAnswerLabel = (TextView) findViewById(R.id.totalCorrect);
        TextView totalQuestionLabel = (TextView) findViewById(R.id.totalQuestion);
        TextView average = (TextView) findViewById(R.id.averageLabel);

        double dividend = totalCorrectAnswerCount;
        double divisor = quizResultList.size();

        double correctPercentage = (dividend/divisor)*100;

        totalCorrectAnswerLabel.setText("Total Correct Answer : "+totalCorrectAnswerCount);
        totalQuestionLabel.setText("Total Question : "+quizResultList.size());
        average.setText("Average : "+correctPercentage+" %");
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
                Intent intent = new Intent(QuizResultActivity.this, QuestionActivity.class);
                startActivity(intent);
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(QuizResultActivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(QuizResultActivity.this, AboutActivity.class);
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
}
