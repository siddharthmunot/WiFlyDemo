package org.wifly.wiflydemo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import org.wifly.wiflydemo.moviesView;
import android.widget.TextView;
//import android.R;


public class MainActivity extends Activity {
   // public static final String MyPREFERENCES = "MyPrefs" ;
    public static SharedPreferences pref;
    private View SeatDialogView;
    private String TAG = "wifidemo";
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pref = PreferenceManager.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        String number = "";
        boolean exists_number = pref.contains("mobileNumber");
        //TextView mobilenumber = (TextView)findViewById(R.id.mobileNumber);
        if(exists_number) {
            number = pref.getString("mobileNumber", "Can't Find");
        }
        else {
            Intent intent = new Intent(this, Intro.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            finish();
        }
        boolean exists_seat = pref.contains("seatNumber");
        Log.i(TAG, "exists_seat = " + exists_seat);

        if(!exists_seat) {
            confirmSeatNumber();
        }
        ImageView movies = (ImageView)findViewById(R.id.movies);
        ImageView music = (ImageView)findViewById(R.id.music);
        ImageView food = (ImageView)findViewById(R.id.food);
        movies.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentMovies = new Intent(MainActivity.this, org.wifly.wiflydemo.dlna.DlnaView.class);
                intentMovies.putExtra("flag", 0);
                intentMovies.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentMovies.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentMovies);
                //finish();
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentMusic = new Intent(MainActivity.this, org.wifly.wiflydemo.dlna.DlnaView.class);
                intentMusic.putExtra("flag", 1);
                intentMusic.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentMusic.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentMusic);
                //finish();
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentFood = new Intent(MainActivity.this, foodView.class);
                intentFood.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentFood.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentFood);
                //finish();
            }
        });
    }

    private void confirmSeatNumber() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //final String[] seat = new String[1];

        SeatDialogView = inflater
                .inflate(R.layout.layout_dialog_confirm_seat_number, null);
        final EditText seatNumberText = (EditText) SeatDialogView.findViewById(R.id.seatnumber);

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Confirm Number");
        alertDialog.setView(SeatDialogView);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Verify", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Log.i(TAG, "seat#" + seatNu);
                editor.putString("seatNumber", seatNumberText.getText().toString());
                editor.apply();
                //seatNumberText.getText().toString();
                }
        });
        alertDialog.show();
        //return seat[0];
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
