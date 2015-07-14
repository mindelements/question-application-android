package com.suresh.mindelements;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


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
        int id = item.getItemId();
        switch (id){

            case R.id.mainMenu:
                break;
            case R.id.questionToolMenu:
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(MainActivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.helpMenu:
                Intent intent4 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent4);
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
        Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
        intent.putExtra("Activity", "quiz");
        startActivity(intent);
    }

    /**
     * Copy sample template file inside asset folder to Download folder
     * @param v
     */
    public void copyTemplate(View v){
        copyAssets();
    }

    private void copyAssets() {

        AssetManager assetManager = getAssets();
        String filename = "Question-Template.xlsx";
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open("Question-Template.xlsx");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        try {
            File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/", filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            Toast.makeText(getApplicationContext(), "Copied to Download folder",
                    Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + filename, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }

    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
