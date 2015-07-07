package com.suresh.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.suresh.mindelements.QuestionActivity;
import com.suresh.mindelements.QuizActivity;
import com.suresh.mindelements.SingleQuestionActivity;
import com.suresh.mindelements.WrongAnswerAvtivity;

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
    WrongAnswerAvtivity wrongAnswerAvtivity;
    QuizActivity quizActivity;
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

    public ServerRequestTask(String dialogueMessage, Context con,
                             WrongAnswerAvtivity act) {
        context = con;
        wrongAnswerAvtivity = act;
        message = dialogueMessage;
    }

    public ServerRequestTask(String dialogueMessage, Context con,
                             QuizActivity act) {
        context = con;
        quizActivity = act;
        message = dialogueMessage;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        if(!message.equalsIgnoreCase("uploadAnswer"))
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
            case "getNextQuestionAgain":
                wrongAnswerAvtivity.getQuestionInBackground();
                break;
            case "checkAnswer":
                singleQuestionActivity.checkAnswerInBackground();
                returnValue = "processAfterCheckAnswer";
                break;
            case "reviewAllAnswer":
                singleQuestionActivity.reviewAllAnswerInBackground();
                returnValue = "processAfterReviewAnswer";
                break;
            case "reviewAllAnswer2":
                wrongAnswerAvtivity.reviewAllAnswerInBackground();
                returnValue = "processAfterReviewAnswer2";
                break;
            case "uploadAnswer":
                quizActivity.uploadAnswerInBackground();
//                returnValue = "processAfterReviewAnswer2";
                break;
            default:
                break;
        }
        return returnValue;
    }

    protected void onPostExecute(String params) {
        super.onPostExecute(params);
        pDialog.dismiss();
        if (params.equals("processAfterCheckAnswer")) {
            singleQuestionActivity.processAfterCheckAnswer();
        }else if(params.equals("processAfterReviewAnswer")){
            singleQuestionActivity.processAfterReviewAnswer();
        }else if(params.equals("processAfterReviewAnswer2")){
            wrongAnswerAvtivity.processAfterReviewAnswer();
        }

    }

}
