package com.suresh.mindelements;

import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;


public class QuestionActivity extends ActionBarActivity {

    Button selectFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Question");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void chooseFile(View v) {

        selectFileButton = (Button) findViewById(R.id.selectFileButton);
        File mPath = new File(Environment.getExternalStorageDirectory() + "/s");
        FileDialog fileDialog = new FileDialog(QuestionActivity.this, mPath);
        fileDialog.setFileEndsWith(".jpg");
        fileDialog.setFileEndsWith(".jpeg");
        fileDialog.setFileEndsWith(".png");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
                selectFileButton.setText(file.toString());
                try {

                    File f = new File(file.toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        fileDialog
                .addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                    public void directorySelected(File directory) {
                        Log.d(getClass().getName(),
                                "Selected dir " + directory.toString());
                    }
                });
        fileDialog.setSelectDirectoryOption(false);
        fileDialog.showDialog();
    }


}
