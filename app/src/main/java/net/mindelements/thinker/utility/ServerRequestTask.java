package net.mindelements.thinker.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.mindelements.thinker.QuizActivity;
import net.mindelements.thinker.SingleQuestionActivity;
import net.mindelements.thinker.WrongAnswerAvtivity;
import net.mindelements.thinker.QuestionActivity;

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


        Log.d(getClass().getName(), "--------------------"+params[0]);
        String invokingMethod = params[0].split(":")[0];
        String questionNumber = "";
        if(invokingMethod.equalsIgnoreCase("uploadAnswer")){
            questionNumber = params[0].split(":")[1];
        }
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
                quizActivity.uploadAnswerInBackground(questionNumber);
//                returnValue = "processAfterReviewAnswer2";
                break;
            case "submitQuizAnswers":
                quizActivity.submitQuizAnswersInBackground();
//                returnValue = "processAfterReviewAnswer2";

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
