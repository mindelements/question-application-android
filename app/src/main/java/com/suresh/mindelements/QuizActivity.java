package com.suresh.mindelements;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuizActivity extends ActionBarActivity {

    public static String QUESTION_TYPE = "";
    Map<String,String> selection;
    Map<String,String> revSelection;
    TableLayout tableLayout;

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

        int i=0;
        for(Object m : allQuestionDetails){
            Map singleQuestionDetails = (Map) allQuestionDetails.get(i);

            QUESTION_TYPE = singleQuestionDetails.get("questionType").toString();
            selection = (Map)singleQuestionDetails.get("selection");
            revSelection = new HashMap<String,String>();
            for(Map.Entry<String,String> entry : selection.entrySet())
                revSelection.put(entry.getValue(), entry.getKey());

            if(QUESTION_TYPE.equalsIgnoreCase("single")){
                addRadioButtons(singleQuestionDetails);
            }else{
                addCheckBoxes(singleQuestionDetails);
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
    public void addRadioButtons(Map singleQuestionDetails) {
        TableLayout ll = (TableLayout) findViewById(R.id.mainTable);
        int i=0;
        /**
         * Row for question
         */
        TableRow questionRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        questionRow.setLayoutParams(lp);
        TextView questionLabel = new TextView(this);
        questionLabel.setText(singleQuestionDetails.get("question").toString());
        questionLabel.setPadding(10, 0, 0, 0);
        questionLabel.setTextColor(Color.rgb(255,255,255));
        questionLabel.setTextSize(20);
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

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp2);
        row.addView(rg);
        ll.addView(row,i);
    }


    /**
     * For displaying checkbox button for question with multiple answer
     */
    public void addCheckBoxes(Map singleQuestionDetails) {

        TableLayout ll = (TableLayout) findViewById(R.id.mainTable);
        int i=0;
        /**
         * Row for question
         */
        TableRow questionRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        questionRow.setLayoutParams(lp);
        TextView questionLabel = new TextView(this);
        questionLabel.setText(singleQuestionDetails.get("question").toString());
        questionLabel.setPadding(10, 0, 0, 0);
        questionLabel.setTextColor(Color.rgb(255,255,255));
        questionLabel.setTextSize(20);
        questionRow.addView(questionLabel);
        ll.addView(questionRow,i);
        i++;

        for(Object key : selection.keySet()){
            CheckBox cb = new CheckBox(this);
            cb.setText(selection.get(key.toString()).toString());
            cb.setId(i + 6);
            ll.addView(cb,i);
        }
    }
}
