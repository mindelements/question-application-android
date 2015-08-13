package net.mindelements.thinker;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FlashCardActivity extends ActionBarActivity {

    public static Map<String,String> SELECTION;
    List quizResultList;
    TextToSpeech speaker;

    String QUESTION_TYPE = "";
    String ANSWER = "";
    String QUESTION = "";
    int QUESTION_COUNTER = 0;

    int RIGHT_ANSWER_COUNTER = 0;
    int WRONG_ANSWER_COUNTER = 0;
    int UNANSWERED_COUNTER = 0;

    Button showButton;
    Button rightButton;
    Button wrongButton;
    Button scoreButton;
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
         * Initialise TextToSpeech instance
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
        changingButtons.put(wrongButton, false);
        changingButtons.put(scoreButton, false);

        changeButtonState(changingButtons);
        List<String> textToSpeakList = new ArrayList<String>();
        textToSpeakList.add("speak");
        textToSpeakList.add("hello, how are you peter?");

        new ServerRequestTask().execute(textToSpeakList.toArray(new String[textToSpeakList.size()]));
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
        questionLabel.setTextSize(18);
        questionLabel.setTextColor(Color.rgb(255, 255, 255));
        questionLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        ll.addView(questionLabel);

        Button button = new Button(this);
        button.setText("Play");
        button.setWidth(50);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ServerRequestTask().execute(QUESTION);
            }
        });
        ll.addView(button);

//
//        int i=0;
//        for(Object key:SELECTION.keySet()){
//            i++;
//            RadioButton rdbtn = new RadioButton(this);
//            rdbtn.setId((1 * 2) + i);
//            rdbtn.setText(SELECTION.get(key.toString()).toString());
//            rdbtn.setTextColor(Color.WHITE);
//            rg.addView(rdbtn);
//        }
//        ll.addView(rg);
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
        questionLabel.setTextSize(18);
        questionLabel.setTextColor(Color.rgb(255, 255, 255));
        questionLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        ll.addView(questionLabel);

        Button button = new Button(this);
        button.setText("Play");
        button.setWidth(50);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ServerRequestTask().execute(QUESTION);
            }
        });
        ll.addView(button);
//        int i=0;
//        for(Object key:SELECTION.keySet()) {
//            i++;
//            CheckBox cb = new CheckBox(this);
//            cb.setText(SELECTION.get(key.toString()).toString());
//            cb.setTextColor(Color.WHITE);
//            cb.setId(i + 6);
//            ll.addView(cb);
//        }
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
        final StringBuilder actualAnswer = new StringBuilder();
        String[] answers = ANSWER.split(",");
        if(answers.length>1){
            actualAnswer.append("The answers are \n");
            for(String code : answers){
                actualAnswer.append(SELECTION.get(code)+" \n");
            }
        }else{
            actualAnswer.append("The answer is "+SELECTION.get(answers[0]));
        }
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView answerLabel = new TextView(this);
        answerLabel.setText(actualAnswer.toString());
        answerLabel.setPadding(10, 0, 0, 0);
        answerLabel.setTextSize(18);
        answerLabel.setTextColor(Color.rgb(255, 255, 255));
        answerLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        ll.addView(answerLabel);
        Button button = new Button(this);
        button.setText("Play");
        button.setWidth(50);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ServerRequestTask().execute(actualAnswer.toString());
            }
        });
        ll.addView(button);
        sv.addView(ll);

        mainContentPane.addView(sv);

        /**
         * Enable right , wrong and score button and disable show button
         */
        changingButtons.clear();
        changingButtons.put(rightButton, true);
        changingButtons.put(wrongButton, true);
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
        speaker.shutdown();
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
    public class ServerRequestTask extends AsyncTask<String, Void, String> {
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
            /**
             * ArraySize is added to last index of params to make number of
             * String value to be spoken dynamic
             */
//            int arraySize = Integer.valueOf(params[params.length-1]);

//            switch (invokingMethod) {
//                case "speak":
//                    for(int i=1 ; i<=1 ;i++){
//                        System.out.println("i = speaking "+params[i]);
//                        speaker.speak(params[i], TextToSpeech.QUEUE_ADD, null);
//                        if(i==3){
//                            speaker.playSilence(1500, TextToSpeech.QUEUE_ADD, null);
//                        }else{
//                            speaker.playSilence(500, TextToSpeech.QUEUE_ADD, null);
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
            speaker.speak(params[0], TextToSpeech.QUEUE_ADD, null);
            return returnValue;
        }

        protected void onPostExecute(String params) {
            super.onPostExecute(params);
        }

    }
}
