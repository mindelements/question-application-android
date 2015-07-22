package com.wMindElements;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wMindElements.R;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("About");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /**
         * Remove line below to enable quizListenTool
         */
        MenuItem item = menu.findItem(R.id.quizLIstenToolMenu);
        item.setVisible(false);
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
                Intent intent = new Intent(AboutActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(AboutActivity.this, QuizActivity.class);
                startActivity(intent2);
                break;
            case R.id.quizLIstenToolMenu:
                Intent intentListen = new Intent(AboutActivity.this, QuestionActivity.class);
                intentListen.putExtra("Activity", "quizlisten");
                startActivity(intentListen);
                break;
            case R.id.aboutMenu:
                break;
            case R.id.quitMenu:
                Intent quit = new Intent(getApplicationContext(), MainActivity.class);
                quit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                quit.putExtra("EXIT", true);
                startActivity(quit);
                return true;
            case R.id.helpMenu:
                Intent intent4 = new Intent(AboutActivity.this, HelpActivity.class);
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
