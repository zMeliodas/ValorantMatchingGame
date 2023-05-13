package com.meliodas.valorantmatchinggame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import java.util.*;
import static com.meliodas.valorantmatchinggame.R.drawable.*;

public class GamePage extends AppCompatActivity {
    // VARIABLES
    private GridLayout gridLayout;
    private int clicked = 0, firstClicked = 0, score = 0;
    Button firstBtnClicked = null;
    Button secondBtnClicked = null;

    // STORING DRAWABLE IDS INTO AN ARRAY
    int[] drawableIds = new int[12];

    // ADDING ARRAYS INTO LIST
    List<Integer> images = new ArrayList<>();
    HashMap<Button, String> buttons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
        gridLayout = findViewById(R.id.gridLayout);

        drawableIds = new int[] {
                jettchibi, neonchibi, phoenixchibi, razechibi, reynachibi, viperchibi,
                jettchibi, neonchibi, phoenixchibi, razechibi, reynachibi, viperchibi
        };

        for (int drawableId : drawableIds) {
            images.add(drawableId);
        }

        // SHUFFLING THE IMAGES ORDER
        Collections.shuffle(images);

        addCardsToGridLayout(4,3);

        /*LOOPING THE BUTTONS TO SET ITS DEFAULT BACKGROUND INTO
        VALORANT LOGO AND SET ON CLICK LISTENER ON EACH BUTTON*/
        gridLayout.post(() -> {
            buttons.keySet().forEach(btn -> {
                btn.setOnClickListener(this::onClick);
            });
        });
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
            flipAndChangeBackground(firstBtnClicked, valorantlogo, true);
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
                }, 500);
                score++;
            } else {
                // IF NOT MATCH FLIP THE CARDS BACK
                view.postDelayed(() -> {
                    flipAndChangeBackground(firstBtnClicked, valorantlogo,true);
                    flipAndChangeBackground(secondBtnClicked, valorantlogo, true);
                    clicked = 0;
                    firstBtnClicked = null;
                    secondBtnClicked = null;
                    enableButtons();
                }, 800);
            }
        }

        // IF SCORE IS EQUAL TO 6 SHOW ALL CARDS
        if (score == 6) {
            view.postDelayed(() -> showAllCompletedCards(), 1000);
            Toast.makeText(view.getContext(),"GAME COMPLETED!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addCardsToGridLayout(int row, int col){
        gridLayout.setColumnCount(col);
        gridLayout.setRowCount(row);
        gridLayout.post(() -> {
            int spacing = 10;
            int height = gridLayout.getHeight();
            int width = gridLayout.getWidth();
            int totalSpacing = (Math.min(width / col, height / row) - 2 * spacing);
            for(int i = 0; i < row * col; i++){
                Button button = new Button(this);
                button.setForeground(ResourcesCompat.getDrawable(getResources(), valorantlogo,null));
                button.setText("Unflipped");

                GridLayout.LayoutParams layoutParams =
                        new GridLayout.LayoutParams(new ViewGroup.LayoutParams(totalSpacing, totalSpacing));
                layoutParams.setMargins(0, 0, spacing, spacing);

                button.setLayoutParams(layoutParams);
                buttons.put(button, getResources().getResourceEntryName(drawableIds[i]));
                gridLayout.addView(button);
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
                    btn.setForeground(ResourcesCompat.getDrawable(getResources(), valorantlogo,null));
                }else {
                    btn.setForeground(ResourcesCompat.getDrawable(getResources(), newBackground,null));
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
            case "jettchibi":
                return jettchibi;
            case "neonchibi":
                return neonchibi;
            case "phoenixchibi":
                return phoenixchibi;
            case "razechibi":
                return razechibi;
            case "reynachibi":
                return reynachibi;
            case "viperchibi":
                return viperchibi;
            default:
                return valorantlogo;
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

    public void enableButtons() {
        buttons.keySet().forEach(btn -> btn.setEnabled(true));
    }

    public void showAllCompletedCards(){
        buttons.keySet().forEach(btn -> btn.setVisibility(View.VISIBLE));
    }

    public void OnClickHome(View v) {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void OnClickRestart(View v) {
        recreate();
    }
}