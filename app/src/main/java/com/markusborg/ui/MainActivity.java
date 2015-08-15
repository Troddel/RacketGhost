package com.markusborg.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.markusborg.logic.LogHandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Main activity that displays the initial settings dialog.
 *
 * @author  Markus Borg
 * @version 0.5
 * @since   2015-07-29
 */
public class MainActivity extends AppCompatActivity {

    private Context appContext;
    private EditText txtSets, txtReps, txtInterval, txtBreak, txtHistory;
    private CheckBox chk6Points, chkAudio;

    private LogHandler logger;

    public final static String FILENAME = "history.log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();
        setGUIComponents();
        logger = new LogHandler(appContext);
        displayHistory();

        final Button btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent ghostingIntent = new Intent(appContext, GhostingActivity.class);
                ghostingIntent.putExtra("NBR_SETS", Integer.parseInt(txtSets.getText().toString()));
                ghostingIntent.putExtra("NBR_REPS", Integer.parseInt(txtReps.getText().toString()));
                ghostingIntent.putExtra("TIME_INTERVAL", Integer.parseInt(txtInterval.getText().toString()));
                ghostingIntent.putExtra("TIME_BREAK", Integer.parseInt(txtBreak.getText().toString()));
                ghostingIntent.putExtra("IS_6POINTS", chk6Points.isChecked());
                ghostingIntent.putExtra("IS_AUDIO", chkAudio.isChecked());
                startActivity(ghostingIntent);
            }
        });
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

        //noinspection SimplifiableIfStatement
        final SpannableString s = new SpannableString("www.google.com");

        switch (id) {
            case R.id.action_help :
                final TextView tx1 = new TextView(this);
                tx1.setText("Quick instructions:\n" +
                            "1. Configure your ghosting session.\n" +
                            "2. Place device approx. 2 m in front of T.\n" +
                            "3. Click 'Go' button.\n\n" +
                            "Full instructions available on\n" +
                            s);
                tx1.setAutoLinkMask(RESULT_OK);
                tx1.setMovementMethod(LinkMovementMethod.getInstance());

                Linkify.addLinks(s, Linkify.WEB_URLS);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("About")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                    }
                                })

                        .setView(tx1).show();
                break;
            case R.id.action_about :
                // create about dialog


                final TextView tx2 = new TextView(this);
                tx2.setText("Made by Borg\n" + s);
                tx2.setAutoLinkMask(RESULT_OK);
                tx2.setMovementMethod(LinkMovementMethod.getInstance());

                Linkify.addLinks(s, Linkify.WEB_URLS);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("About")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                    }
                                })

                        .setView(tx2).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayHistory();
    }

    public LogHandler getLogHandler() {
        return logger;
    }

    private void setGUIComponents(){
        txtSets = (EditText) findViewById(R.id.txtSets);
        txtReps = (EditText) findViewById(R.id.txtReps);
        txtInterval = (EditText) findViewById(R.id.txtInterval);
        txtBreak = (EditText) findViewById(R.id.txtBreak);
        chk6Points = (CheckBox) findViewById(R.id.chk6Point);
        chkAudio = (CheckBox) findViewById(R.id.chkAudio);
        txtHistory = (EditText) findViewById(R.id.txtHistory);
    }

    private void displayHistory() {
        txtHistory.setText("Recent history:\n" + logger.getFromLog(3));
    }

}
