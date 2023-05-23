package com.meliodas.valorantmatchinggame;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class HomePage extends AppCompatActivity {
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = MediaPlayer.create(this, R.raw.homescreensoundtrack);
    }
    public void onStart(){
        super.onStart();
        player.setLooping(true);
        player.start();
    }
    public void onPause(){
        super.onPause();
        player.pause();
    }

    public void onResume(){
        super.onResume();
        player.start();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        player.release();
        player.stop();
    }

    public void OnClickEasy(View v){
        Intent intent = new Intent(this, GamePage.class);
        Button easyBtn = findViewById(R.id.buttonEasy);
        easyBtn.setTextColor(getColor(R.color.valred));
        v.postDelayed(() ->{
            easyBtn.setTextColor(getColor(R.color.white));
        },100);
        player.pause();
        startActivity(intent);
    }

    public void OnClickMedium(View v){
        Intent intent = new Intent(this, GamePage2.class);
        Button mediumBtn = findViewById(R.id.buttonMedium);
        mediumBtn.setTextColor(getColor(R.color.valred));
        v.postDelayed(() ->{
            mediumBtn.setTextColor(getColor(R.color.white));
        },100);
        player.pause();
        startActivity(intent);
    }

    public void OnClickHard(View v){
        Intent intent = new Intent(this, GamePage3.class);
        Button hardBtn = findViewById(R.id.buttonHard);
        hardBtn.setTextColor(getColor(R.color.valred));
        v.postDelayed(() ->{
            hardBtn.setTextColor(getColor(R.color.white));
        },100);
        player.pause();
        startActivity(intent);
    }

    public void OnClickExit(View v){
        Button exitBtn = findViewById(R.id.buttonExit);
        exitBtn.setTextColor(getColor(R.color.valred));
        v.postDelayed(() ->{
            exitBtn.setTextColor(getColor(R.color.white));
        },250);
        finishAffinity();
    }
}