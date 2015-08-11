package net.mindelements.thinker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FlashCardActivity extends ActionBarActivity {

    public static Map<String,String> SELECTION;
    List quizResultList;

    String QUESTION_TYPE = "";
    String QUESTION_STATUS = "";
    String SESSION_ID = "";
    String MEMBER_ID = "";
    String ANSWER = "";
    String QUESTION_NUMBER = "";
    String QUESTION = "";
    int QUESTION_COUNTER = 0;

    int RIGHT_ANSWER_COUNTER = 0;
    int WRONG_ANSWER_COUNTER = 0;
    int UNANSWERED_COUNTER = 0;

    Button showButton;
    Button rightButton;
    Button wrongButton;
    Button scoreButton;
    Button endButton;
    Map<Button,Boolean> changingButtons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Flash Card");
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * Get map of data from parent activity
         */
        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
        quizResultList = (List) hashMap.get("datas");

        Map singleMap = (Map)quizResultList.get(QUESTION_COUNTER);
        this.QUESTION = singleMap.get("question").toString();
        this.SELECTION = (Map) singleMap.get("selection");
        this.ANSWER = singleMap.get("answer").toString();

        if(QUESTION_TYPE.equalsIgnoreCase("single"))
            addRadioButtons();
        else
            addCheckBoxes();

        /**
         * Initialise all button of the view
         */
        showButton = (Button) findViewById(R.id.showButton);
        rightButton = (Button)findViewById(R.id.rightButton);
        wrongButton = (Button)findViewById(R.id.wrongButton);
        scoreButton = (Button)findViewById(R.id.scoreButton);
        /**
         * Disable Right, Wrong and Score button at the beginning
         */
        changingButtons.put(rightButton, false);
        changingButtons.put(wrongButton,false);
        changingButtons.put(scoreButton,false);

        changeButtonState(changingButtons);
    }
    /**
     * For displaying radio button for question with single answer
     */
    public void addRadioButtons() {

        LinearLayout ll2 = (LinearLayout)findViewById(R.id.linearLayout2);

        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        RadioGroup rg = new RadioGroup(this);
        rg.setId(99 * 1);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);

        TextView questionLabel = new TextView(this);
        questionLabel.setText(QUESTION);
        questionLabel.setPadding(10, 0, 0, 0);
        questionLabel.setTextSize(16);
        questionLabel.setTextColor(Color.rgb(255, 255, 255));
        ll.addView(questionLabel);

        int i=0;
        for(Object key:SELECTION.keySet()){
            i++;
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId((1 * 2) + i);
            rdbtn.setText(SELECTION.get(key.toString()).toString());
            rdbtn.setTextColor(Color.WHITE);
            rg.addView(rdbtn);
        }
        ll.addView(rg);
        ll2.addView(sv);

    }

    /**
     * For displaying checkbox button for question with multiple answer
     */
    public void addCheckBoxes() {
        LinearLayout ll2 = (LinearLayout)findViewById(R.id.linearLayout2);

        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        TextView questionLabel = new TextView(this);
        questionLabel.setText(QUESTION);
        questionLabel.setPadding(10, 0, 0, 0);
        questionLabel.setTextSize(16);
        questionLabel.setTextColor(Color.rgb(255,255,255));
        ll.addView(questionLabel);
        int i=0;
        for(Object key:SELECTION.keySet()) {
            i++;
            CheckBox cb = new CheckBox(this);
            cb.setText(SELECTION.get(key.toString()).toString());
            cb.setTextColor(Color.WHITE);
            cb.setId(i + 6);
            ll.addView(cb);
        }
        ll2.addView(sv);
    }

    public void changeButtonState(Map<Button,Boolean> buttonMap){
        for (Map.Entry<Button, Boolean> entry : buttonMap.entrySet())
        {
            Button b = entry.getKey();
            boolean state = entry.getValue();
            b.setClickable(state);
            if(state){
                b.setTextColor(Color.rgb(0, 0, 0));
            }else{
                b.setTextColor(Color.rgb(200, 200, 200));
            }
        }
    }

    public void showAnswer(View v){
        LinearLayout mainContentPane = (LinearLayout)findViewById(R.id.linearLayout2);
        mainContentPane.removeAllViews();

        /**
         * Get actual answer from answer code
         */
        StringBuilder actualAnswer = new StringBuilder();
        String[] answers = ANSWER.split(",");
        if(answers.length>1){
            actualAnswer.append("The answers are \n");
            for(String code : answers){
                actualAnswer.append(SELECTION.get(code)+" \n");
            }
        }else{
            actualAnswer.append("The answers is "+SELECTION.get(answers[0]));
        }
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView answerLabel = new TextView(this);
        answerLabel.setText(actualAnswer.toString());
        answerLabel.setPadding(10, 0, 0, 0);
        answerLabel.setTextSize(16);
        answerLabel.setTextColor(Color.rgb(255, 255, 255));
        ll.addView(answerLabel);
        sv.addView(ll);

        mainContentPane.addView(sv);

        /**
         * Enable right , wrong and score button and disable show button
         */
        changingButtons.clear();
        changingButtons.put(rightButton, true);
        changingButtons.put(wrongButton,true);
        changingButtons.put(scoreButton, true);
        changingButtons.put(showButton, false);

        changeButtonState(changingButtons);

    }

    public void getNextQuestionRight(View v){
        RIGHT_ANSWER_COUNTER++;
        getNextQuestion();
    }

    public void getNextQuestionWrong(View v){
        WRONG_ANSWER_COUNTER++;
        getNextQuestion();
    }


    private void getNextQuestion(){

        QUESTION_COUNTER++;
        if(QUESTION_COUNTER >= quizResultList.size()){
            showDoneDialog(true);
        }else{
            LinearLayout ll2 = (LinearLayout)findViewById(R.id.linearLayout2);
            ll2.removeAllViews();

            Map singleMap = (Map)quizResultList.get(QUESTION_COUNTER);
            this.QUESTION = singleMap.get("question").toString();
            this.SELECTION = (Map) singleMap.get("selection");
            this.QUESTION_TYPE = singleMap.get("questionType").toString();
            this.ANSWER = singleMap.get("answer").toString();

            if(QUESTION_TYPE.equalsIgnoreCase("single"))
                addRadioButtons();
            else
                addCheckBoxes();
        }

        changingButtons.clear();

        changingButtons.put(showButton, true);
        changingButtons.put(rightButton, false);
        changingButtons.put(wrongButton, false);

        changeButtonState(changingButtons);
    }

    public void showDoneDialog(final boolean isFinished){

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(R.color.background_floating_material_dark);
        dialog.setContentView(R.layout.question_details);
        dialog.setTitle(isFinished?"Done! Upload another":"Scores");

        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        StringBuilder sb = new StringBuilder();
        int total = quizResultList.size();

        double rightPercentage = Math.round(RIGHT_ANSWER_COUNTER*100/total);

        if(isFinished)
            sb.append(Html.fromHtml("<h1>SCORES</h1>"));

        sb.append("Correct Answer(s): "+RIGHT_ANSWER_COUNTER+"\n");
        sb.append("Incorrect Answer(s): "+WRONG_ANSWER_COUNTER+"\n");
        sb.append("Unanswered: "+UNANSWERED_COUNTER+"\n");
        sb.append("Total: "+total+"\n");
        sb.append("Percentage: "+rightPercentage+"%\n");

        TextView questionNumberLabel = new TextView(this);
        questionNumberLabel.setText(sb.toString());
        questionNumberLabel.setTextColor(Color.WHITE);
        questionNumberLabel.setTextSize(16);
        questionNumberLabel.setPadding(10, 0, 0, 0);
        container.addView(questionNumberLabel, 0);


        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(isFinished){
                    Intent intent = new Intent(FlashCardActivity.this, QuestionActivity.class);
                    intent.putExtra("Activity", "flashcard");
                    startActivity(intent);
                }

            }
        });
        dialog.show();


    }
    public void getScoreDialog(View v){
        showDoneDialog(false);
    }

    public void endFlashCardTool(View v){
        Intent intent = new Intent(FlashCardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

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
                Intent intent = new Intent(FlashCardActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(FlashCardActivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.flashCardToolMenu:
                Intent flashCard = new Intent(FlashCardActivity.this, QuestionActivity.class);
                flashCard.putExtra("Activity", "flashcard");
                startActivity(flashCard);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(FlashCardActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.quitMenu:
                Intent quit = new Intent(getApplicationContext(), MainActivity.class);
                quit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                quit.putExtra("EXIT", true);
                startActivity(quit);
                return true;
            case R.id.helpMenu:
                Intent intent4 = new Intent(FlashCardActivity.this, HelpActivity.class);
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
}
