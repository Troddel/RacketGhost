package com.markusborg.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.markusborg.logic.GhostPlayer;
import com.markusborg.logic.Setting;

public class GhostingActivity extends AppCompatActivity implements GhostingFinishedListener {

    private GhostingFinishedListener mainActivityCallback;
    private Setting theSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghosting);
        Bundle extras = getIntent().getExtras();
        theSetting = new Setting(extras.getInt("NBR_SETS"),
                extras.getInt("NBR_REPS"),
                extras.getInt("TIME_INTERVALS"),
                extras.getInt("TIME_BREAK"),
                extras.getBoolean("IS_6POINTS"),
                extras.getBoolean("IS_AUDIO"));
        GhostingTask gTask = new GhostingTask();
        gTask.delegate = this;
        gTask.execute(theSetting);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghosting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void notifyGhostingFinished() {
        Intent summaryIntent = new Intent(this, ResultsActivity.class);
        summaryIntent.putExtra("NBR_SETS", theSetting.getSets());
        summaryIntent.putExtra("NBR_REPS", theSetting.getReps());
        summaryIntent.putExtra("TIME_INTERVALS", theSetting.getInterval());
        summaryIntent.putExtra("TIME_BREAKS", theSetting.getBreakTime());
        summaryIntent.putExtra("IS_6POINTS", theSetting.isSixPoints());
        summaryIntent.putExtra("IS_AUDIO", theSetting.isAudio());
        startActivity(summaryIntent);
    }

    public class GhostingTask extends AsyncTask<Setting, String, String> {

        public GhostingFinishedListener delegate = null;
        private volatile boolean running = true;
        private TextView lblProgress;

        @Override
        protected String doInBackground(Setting... params) {
            Setting theSetting = params[0];
            GhostPlayer theGhost = new GhostPlayer(theSetting.isSixPoints());

            lblProgress = (TextView) findViewById(R.id.lblProgress);

            // Loop the sets
            for (int i = 1; i <= theSetting.getSets(); i++) {

                displayCountDown();

                // Loop the reps
                for (int j = 1; j <= theSetting.getReps(); j++) {
                    String corner = theGhost.nextCorner();

                    String progress = new String(j + " / " + theSetting.getReps() +
                            " (Set " + i + ")");

                    // Turn on corner
                    publishProgress(progress, corner);
                    try {
                        Thread.sleep((theSetting.getInterval() / 2) * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Turn off corner
                    publishProgress(progress, corner, "OFF");
                    try {
                        Thread.sleep((theSetting.getInterval() / 2) * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } // end reps loop
                publishProgress("Rest...");

                try {
                    Thread.sleep(theSetting.getBreakTime() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } // end sets loop

            finish();

            return "Done";
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (progress.length == 1) {
                lblProgress.setText(progress[0]);
            }
            else if (progress.length == 2) {
                clearCorners();
                lblProgress.setText(progress[0]);
                String cornerToFlash = progress[1];

                if (cornerToFlash.equals("L_FRONT")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.leftFront);
                    corner.setBackgroundColor(Color.BLUE);
                }
                else if (cornerToFlash.equals("R_FRONT")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.rightFront);
                    corner.setBackgroundColor(Color.BLUE);
                }
                else if (cornerToFlash.equals("L_BACK")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.leftBack);
                    corner.setBackgroundColor(Color.BLUE);
                }
                else if (cornerToFlash.equals("R_BACK")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.rightBack);
                    corner.setBackgroundColor(Color.BLUE);
                }
                else if (cornerToFlash.equals("L_VOLLEY")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.leftMid);
                    corner.setBackgroundColor(Color.BLUE);
                }
                else {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.rightMid);
                    corner.setBackgroundColor(Color.BLUE);
                }
            }
            else if (progress.length == 3) {
                String cornerToTurnOff = progress[1];

                if (cornerToTurnOff.equals("L_FRONT")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.leftFront);
                    corner.setBackgroundColor(Color.DKGRAY);
                }
                else if (cornerToTurnOff.equals("R_FRONT")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.rightFront);
                    corner.setBackgroundColor(Color.DKGRAY);
                }
                else if (cornerToTurnOff.equals("L_BACK")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.leftBack);
                    corner.setBackgroundColor(Color.DKGRAY);
                }
                else if (cornerToTurnOff.equals("R_BACK")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.rightBack);
                    corner.setBackgroundColor(Color.DKGRAY);
                }
                else if (cornerToTurnOff.equals("L_VOLLEY")) {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.leftMid);
                    corner.setBackgroundColor(Color.DKGRAY);
                }
                else {
                    LinearLayout corner = (LinearLayout) findViewById(R.id.rightMid);
                    corner.setBackgroundColor(Color.DKGRAY);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            clearCorners();
            delegate.notifyGhostingFinished();
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        private void displayCountDown() {
            publishProgress("3");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress("2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress("1");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress("");
        }

        private void clearCorners() {
            LinearLayout corner = (LinearLayout) findViewById(R.id.leftFront);
            corner.setBackgroundColor(Color.DKGRAY);
            corner = (LinearLayout) findViewById(R.id.rightFront);
            corner.setBackgroundColor(Color.DKGRAY);
            corner = (LinearLayout) findViewById(R.id.leftMid);
            corner.setBackgroundColor(Color.DKGRAY);
            corner = (LinearLayout) findViewById(R.id.rightMid);
            corner.setBackgroundColor(Color.DKGRAY);
            corner = (LinearLayout) findViewById(R.id.leftBack);
            corner.setBackgroundColor(Color.DKGRAY);
            corner = (LinearLayout) findViewById(R.id.rightBack);
            corner.setBackgroundColor(Color.DKGRAY);
        }
    }
}
