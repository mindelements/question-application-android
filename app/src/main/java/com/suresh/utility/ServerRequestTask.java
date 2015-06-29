package com.suresh.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.suresh.mindelements.QuestionActivity;
import com.suresh.mindelements.SingleQuestionActivity;

/**
 * this class performs all the work, shows dialog before the work and dismiss it
 * after
 */

public class ServerRequestTask extends AsyncTask<String, Void, String> {
    String message;
    ProgressDialog pDialog;
    Context context;
    QuestionActivity activity;
    SingleQuestionActivity singleQuestionActivity;
    /**
     *
     * Other constructor can be added here along with the activity
     */
    public ServerRequestTask(String dialogueMessage, Context con,
                             SingleQuestionActivity act) {
        context = con;
        singleQuestionActivity = act;
        message = dialogueMessage;
    }

    public ServerRequestTask(String dialogueMessage, Context con,
                             QuestionActivity act) {
        context = con;
        activity = act;
        message = dialogueMessage;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String invokingMethod = params[0];
        String returnValue = "";

        switch (invokingMethod) {
            case "uploadFile":
                activity.uploadFileInBackground();
                break;
            case "getNextQuestion":
                singleQuestionActivity.getQuestionInBackground();
                break;
            default:
                break;
        }
        return returnValue;
    }

    protected void onPostExecute(String params) {
        super.onPostExecute(params);
        pDialog.dismiss();
        if (params.equals("processAfterLogin")) {
//
        }

    }

}
