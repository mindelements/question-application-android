package net.mindelements.thinker;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.mindelements.thinker.R;

public class HelpActivity extends ActionBarActivity {
    TextView requestView;
    TextView description;
    TextView description2;
    TextView emailView;
    TextView webpageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Help");
        actionBar.setDisplayHomeAsUpEnabled(true);

        requestView = (TextView)findViewById(R.id.requestView);
        description = (TextView)findViewById(R.id.description);
        description2 = (TextView)findViewById(R.id.description2);
        emailView = (TextView)findViewById(R.id.emailView);
        webpageView = (TextView)findViewById(R.id.webpageView);

        requestView.setText(Html.fromHtml("Please download question template on <a href=\"http://mindelements.net/\">mindelements.net/</a> "));
        requestView.setMovementMethod(LinkMovementMethod.getInstance());

        description.setText("Mind Elements App is a collection of tools targeted towards helping users or students to learn new things. If the user have internal or external certifications that needs to be passed in a company, or a course that needs to be learned quickly in order to pass the exams, the tools provided here will help accomplish that.");
        description2.setText("The Mind Elements App currently offers two tools:\n" +Html.fromHtml("&#x25C6;")+" Question Tool\n"  +Html.fromHtml("&#x25C6;")+" Quiz Tool");

        emailView.setText(Html.fromHtml("<a href=\"mailto:support@mindelements.net\">support@mindelements.net</a>"));
        emailView.setMovementMethod(LinkMovementMethod.getInstance());

        webpageView.setText(Html.fromHtml("<a href=\"http://mindelements.net/\">http://mindelements.net/</a> "));
        webpageView.setMovementMethod(LinkMovementMethod.getInstance());

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
                Intent intent = new Intent(HelpActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.quizToolMenu:
                Intent intent2 = new Intent(HelpActivity.this, QuestionActivity.class);
                intent2.putExtra("Activity", "quiz");
                startActivity(intent2);
                break;
            case R.id.quizLIstenToolMenu:
                Intent intentListen = new Intent(HelpActivity.this, QuestionActivity.class);
                intentListen.putExtra("Activity", "quizlisten");
                startActivity(intentListen);
                break;
            case R.id.aboutMenu:
                Intent intent3 = new Intent(HelpActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.helpMenu:
                Intent intent4 = new Intent(HelpActivity.this, HelpActivity.class);
                startActivity(intent4);
                break;
            case R.id.quitMenu:
                finish();
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
