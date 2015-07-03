package com.suresh.mindelements;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /**
         * To quit from the entire application from any activities if Quit menu is pressed
         */
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){

            case R.id.mainMenu:
                break;
            case R.id.questionToolMenu:
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent2);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.quitMenu:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param v - view of next activity
     */
    public void getQuestionView(View v) {
        Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
        startActivity(intent);
    }
    /**
     *
     * @param v - view of next activity
     */
    public void getQuizView(View v) {
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        startActivity(intent);
    }
}
