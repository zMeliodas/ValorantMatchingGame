package com.meliodas.valorantmatchinggame;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.*;

public class GamePage3 extends AppCompatActivity {
    // VARIABLES
    private GridLayout gridLayout;
    private int clicked = 0, score = 0;
    private TextView textViewCountDown;
    CountDownTimer timer;
    Button firstBtnClicked = null;
    Button secondBtnClicked = null;
    // STORING DRAWABLE IDS INTO AN ARRAY
    int[] drawableIds = new int[12];
    //PUTTING BUTTONS INTO MAP
    HashMap<Button, String> buttons = new HashMap<>();
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page3);
        player = MediaPlayer.create(this, R.raw.hardsoundtrack);
        player.start();
        textViewCountDown = findViewById(R.id.textViewCountDownID2);
        gridLayout = findViewById(R.id.gridLayout2);

        drawableIds = new int[] {
                R.drawable.brim, R.drawable.chamber, R.drawable.cypher, R.drawable.fade, R.drawable.gekko,
                R.drawable.jett, R.drawable.kayo, R.drawable.killjoy, R.drawable.neon, R.drawable.omen,
                R.drawable.phoenix, R.drawable.raze, R.drawable.reyna, R.drawable.sage, R.drawable.skye,
                R.drawable.sova, R.drawable.viper, R.drawable.yoru, R.drawable.brim, R.drawable.chamber,
                R.drawable.cypher, R.drawable.fade, R.drawable.gekko, R.drawable.jett, R.drawable.kayo,
                R.drawable.killjoy, R.drawable.neon, R.drawable.omen, R.drawable.phoenix, R.drawable.raze,
                R.drawable.reyna, R.drawable.sage, R.drawable.skye, R.drawable.sova, R.drawable.viper, R.drawable.yoru,
        };

        addCardsToGridLayout(6,6);

        // LOOPING THE BUTTONS AND SET ON CLICK LISTENER ON EACH BUTTON
        gridLayout.post(() -> {
            buttons.keySet().forEach(btn -> {
                btn.setOnClickListener(this::onClick);
                flipAndChangeBackground(btn, getDrawableId(getValueByKey(btn)), false); // SHOW THE FRONT OF THE CARDS
            });
        });

        // FLIPPING BACK TO DEFAULT STATE AND STARTING THE TIMER
        gridLayout.postDelayed(() -> {
            buttons.keySet().forEach(btn -> {
                flipAndChangeBackground(btn, R.drawable.valorantlogo, true); // FLIP THE CARDS BACK
            });
        }, 2000);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Game Over! Time is up!");

        builder.setPositiveButton("Try again?", (dialog, which) -> onClickRestart(findViewById(R.id.buttonRestart)));
        builder.setNegativeButton("Home", (dialog, which) -> finish());

        AlertDialog dialog = builder.create();

        timer = new CountDownTimer(240000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                textViewCountDown.setText(String.valueOf(secondsLeft));
            }

            @Override
            public void onFinish() {
                textViewCountDown.setText("0");
                dialog.show();
            }

        };

        gridLayout.postDelayed(() -> {
            timer.start();
        },3000);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timer.cancel();
        player.stop();
    }

    public void onClick(View view) {
        // CHECK IF VIEW IS A BUTTON
        if (!(view instanceof Button)) {
            return;
        }
        Button btn = (Button) view;

        // GET THE IMAGE VALUE OF THE BUTTON
        String image = getValueByKey(btn);

        // CLICKED BUTTONS
        if (firstBtnClicked == null) {
            firstBtnClicked = btn;
        } else {
            secondBtnClicked = btn;
        }

        if(secondBtnClicked != null && secondBtnClicked.equals(firstBtnClicked)) {
            flipAndChangeBackground(firstBtnClicked, R.drawable.valorantlogo, true);
            clicked = 0;
            firstBtnClicked = null;
            secondBtnClicked = null;
            enableButtons();
            return;
        }

        // CHECK IF BUTTON IS UNFLIPPED
        if (btn.getText().equals("Unflipped") && clicked < 2){
            flipAndChangeBackground(view, getDrawableId(image), false);
            clicked++;
        }

        // CHECK IF CLICKED BUTTONS ARE EQUAL
        if (clicked == 2 && firstBtnClicked != null && secondBtnClicked != null) {
            for (Button button : buttons.keySet()) {
                if (button.equals(firstBtnClicked) || button.equals(secondBtnClicked)) {
                    continue;
                }
                button.setEnabled(false);
            }

            if(checkIfEqual(firstBtnClicked, secondBtnClicked)) {
                // IF MATCH ADD SCORE AND MAKE THE PAIR INVISIBLE
                firstBtnClicked.setClickable(false);
                secondBtnClicked.setClickable(false);
                view.postDelayed(() -> {
                    btn.setVisibility(View.INVISIBLE);
                    firstBtnClicked.setVisibility(View.INVISIBLE);
                    clicked = 0;
                    firstBtnClicked = null;
                    secondBtnClicked = null;
                    enableButtons();
                }, 800);
                score++;
            } else {
                // IF NOT MATCH FLIP THE CARDS BACK
                view.postDelayed(() -> {
                    flipAndChangeBackground(firstBtnClicked, R.drawable.valorantlogo,true);
                    flipAndChangeBackground(secondBtnClicked, R.drawable.valorantlogo, true);
                    clicked = 0;
                    firstBtnClicked = null;
                    secondBtnClicked = null;
                    enableButtons();
                }, 800);
            }
        }

        // IF SCORE IS EQUAL TO 18 SHOW ALL CARDS
        if (score == 18) {
            timer.cancel();
            view.postDelayed(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Game Complete!");

                builder.setPositiveButton("Retry", (dialog, which) -> onClickRestart(findViewById(R.id.buttonRestart)));
                builder.setNegativeButton("Home", (dialog, which) -> finish());

                AlertDialog dialog = builder.create();
                dialog.show();
                showAllCompletedCards();
            },1000);
        }
    }

    public void addCardsToGridLayout(int row, int col){
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);
        gridLayout.post(() -> {
            int spacing = 8;
            int height = gridLayout.getHeight();
            int width = gridLayout.getWidth();
            int totalSpacing = (Math.min(width / col, height / row) - 2 * spacing);
            for(int i = 0; i < row * col; i++){
                Button button = new Button(this);
                button.setBackgroundResource(R.drawable.valorantlogo);
                button.setText("Unflipped");
                button.setTextSize(0.0F);

                GridLayout.LayoutParams layoutParams =
                        new GridLayout.LayoutParams(new ViewGroup.LayoutParams(totalSpacing, totalSpacing));
                layoutParams.setMargins(spacing, spacing, spacing, spacing);

                button.setLayoutParams(layoutParams);
                buttons.put(button, getResources().getResourceEntryName(drawableIds[i]));
                gridLayout.addView(button);
                shuffleGridLayout();
            }
        });
    }

    public void flipAndChangeBackground(View view, int newBackground, boolean isFlipped) {
        // create two ObjectAnimator instances for flipping the card
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(view, "rotationY", 0f,90f);
        ObjectAnimator flipIn = ObjectAnimator.ofFloat(view, "rotationY", -90,0f);

        // set the drawable resource for the switch after the flip animation ends
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!(view instanceof Button)) {
                    return;
                }

                Button btn = (Button) view;

                if(isFlipped) {
                    btn.setText("Unflipped");
                    btn.setTextSize(0.0F);
                    btn.setBackgroundResource(R.drawable.valorantlogo);
                }else {
                    btn.setBackgroundResource(newBackground);
                }
                flipIn.start();
            }
        });

        // play the animations together
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(flipOut, flipIn);
        animSet.start();
    }

    public int getDrawableId(String name) {
        switch (name) {
            case "brim":
                return R.drawable.brim;
            case "chamber":
                return R.drawable.chamber;
            case "cypher":
                return R.drawable.cypher;
            case "fade":
                return R.drawable.fade;
            case "jett":
                return R.drawable.jett;
            case "kayo":
                return R.drawable.kayo;
            case "neon":
                return R.drawable.neon;
            case "omen":
                return R.drawable.omen;
            case "raze":
                return R.drawable.raze;
            case "reyna":
                return R.drawable.reyna;
            case "sage":
                return R.drawable.sage;
            case "skye":
                return R.drawable.skye;
            case "sova":
                return R.drawable.sova;
            case "viper":
                return R.drawable.viper;
            case "yoru":
                return R.drawable.yoru;
            case "gekko":
                return R.drawable.gekko;
            case "phoenix":
                return R.drawable.phoenix;
            case "killjoy":
                return R.drawable.killjoy;
            default:
                return R.drawable.valorantlogo;
        }
    }

    public String getValueByKey(Button value) {
        for (Map.Entry<Button, String> entry : buttons.entrySet()) {
            if (entry.getKey().equals(value)) {
                return entry.getValue();
            }
        }
        return "";
    }

    public boolean checkIfEqual(Button button1, Button button2) {
        if(button1.equals(button2)){
            return false;
        }

        return getValueByKey(button1).equals(getValueByKey(button2));
    }

    public void shuffleGridLayout(){
        List<Button> buttonList = new ArrayList<>(buttons.keySet());
        Collections.shuffle(buttonList);

        gridLayout.removeAllViews();
        for (Button button : buttonList) {
            gridLayout.addView(button);
        }
    }

    public void enableButtons() {
        buttons.keySet().forEach(btn -> btn.setEnabled(true));
    }

    public void disableButtons() {
        buttons.keySet().forEach(btn -> btn.setEnabled(false));
    }

    public void showAllCompletedCards(){
        buttons.keySet().forEach(btn -> btn.setVisibility(View.VISIBLE));
    }

    public void onClickHome(View v) {
        finish();
    }

    public void onClickRestart(View v) {
        recreate();
    }
}