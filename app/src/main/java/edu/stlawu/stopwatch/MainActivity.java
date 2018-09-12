package edu.stlawu.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variable for our views
    private TextView tv_count = null;
    private Button bt_start = null;
    private Button bt_stop =null;
    private Button bt_reset = null;
    private Button bt_resume = null;
    private Timer t = null;
    private Counter ctr = null;  // TimerTask

    public AudioAttributes  aa = null;
    private SoundPool soundPool = null;
    private int bloopSound = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        this.tv_count = findViewById(R.id.tv_count);
        this.bt_start = findViewById(R.id.bt_start);
        this.bt_stop = findViewById(R.id.bt_stop);
        this.bt_reset = findViewById(R.id.bt_reset);
        this.bt_resume = findViewById(R.id.bt_resume);

        // reload the count from a previous
        // run, if first time running, start at 0
        /// preferences to share state
        final int count = getPreferences(MODE_PRIVATE)
                .getInt("COUNT", 0);
        this.tv_count.setText(String.format("%02d:%02d:%01d", count/600 % 60, count/10 % 60, count%10));
        this.ctr = new Counter();
        this.ctr.count = count;
        this.t = new Timer();

        if(this.ctr.count == 0) {

            this.bt_start.setEnabled(true);
            this.bt_start.setBackgroundColor(getResources().getColor(R.color.GREEN));

            this.bt_stop.setEnabled(false);
            this.bt_stop.setBackgroundColor(getResources().getColor(R.color.RED));

            this.bt_resume.setEnabled(false);
            this.bt_resume.setBackgroundColor(getResources().getColor(R.color.RED));

            this.bt_reset.setEnabled(true);
            this.bt_reset.setBackgroundColor(getResources().getColor(R.color.GREEN));

        } else {

            this.bt_resume.setEnabled(true);
            this.bt_resume.setBackgroundColor(getResources().getColor(R.color.GREEN));

            this.bt_start.setEnabled(false);
            this.bt_start.setBackgroundColor(getResources().getColor(R.color.RED));

            this.bt_stop.setEnabled(false);
            this.bt_stop.setBackgroundColor(getResources().getColor(R.color.RED));

            this.bt_reset.setEnabled(true);
            this.bt_reset.setBackgroundColor(getResources().getColor(R.color.GREEN));
        }



        // press start button
        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_start.setEnabled(false);
                bt_start.setBackgroundColor(getResources().getColor(R.color.RED));

                bt_stop.setEnabled(true);
                bt_stop.setBackgroundColor(getResources().getColor(R.color.GREEN));

                ctr = new Counter();
                t = new Timer();

                t.scheduleAtFixedRate(ctr, 0, 100);
            }
        });

        // press stop button
        this.bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getPreferences(MODE_PRIVATE)
                        .edit()
                        .putInt("COUNT", ctr.count)
                        .apply();

                bt_start.setEnabled(false);
                bt_start.setBackgroundColor(getResources().getColor(R.color.RED));

                bt_stop.setEnabled(false);
                bt_stop.setBackgroundColor(getResources().getColor(R.color.RED));

                bt_reset.setEnabled(true);
                bt_reset.setBackgroundColor(getResources().getColor(R.color.GREEN));

                bt_resume.setEnabled(true);
                bt_resume.setBackgroundColor(getResources().getColor(R.color.GREEN));

                t.cancel();

            }
        });

        // press reset button
        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_stop.setEnabled(false);
                bt_stop.setBackgroundColor(getResources().getColor(R.color.RED));

                bt_start.setEnabled(true);
                bt_start.setBackgroundColor(getResources().getColor(R.color.GREEN));

                bt_resume.setEnabled(false);
                bt_resume.setBackgroundColor(getResources().getColor(R.color.RED));

                MainActivity.this.tv_count.setText(String.format("%02d:%02d:%01d", 00, 00, 0));
                t.cancel();


            }
        });

        // press resume button
        this.bt_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_stop.setEnabled(true);
                bt_stop.setBackgroundColor(getResources().getColor(R.color.GREEN));

                bt_resume.setEnabled(false);
                bt_resume.setBackgroundColor(getResources().getColor(R.color.RED));


                ctr = new Counter();
                //ctr.count = Integer.parseInt(MainActivity.this.tv_count.getText().toString());
                ctr.count = getPreferences(MODE_PRIVATE)
                        .getInt("COUNT", 0);

                t = new Timer();

                t.scheduleAtFixedRate(ctr, 0, 100);

            }
        });

        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();
        this.bloopSound = this.soundPool.load(
                this, R.raw.bloop, 1);

        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(bloopSound, 1f,
                        1f, 1, 0, 1f);
                Animator anim = AnimatorInflater
                        .loadAnimator(MainActivity.this,
                                R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();




        // factory method - design pattern
        Toast.makeText(this, "Stopwatch is started",
                Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("COUNT", ctr.count)
                .apply();



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("COUNT", ctr.count)
                .apply();
    }

    class Counter extends TimerTask {
        private int count = 0;
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.tv_count.setText(String.format("%02d:%02d:%01d", count/600 % 60, count/10 % 60, count%10));
                            count++; // increment



                        }
                    }
            );
        }
    }
}