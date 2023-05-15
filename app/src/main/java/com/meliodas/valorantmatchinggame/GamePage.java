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

public class GamePage extends AppCompatActivity {
    // VARIABLES
    private GridLayout gridLayout;
    private int clicked = 0, score = 0;
    Button firstBtnClicked = null;
    Button secondBtnClicked = null;

    // STORING DRAWABLE IDS INTO AN ARRAY
    int[] drawableIds = new int[12];

    // ADDING ARRAYS INTO LIST
    List<Integer> images = new ArrayList<>();

    //PUTTING BUTTONS INTO MAP
    HashMap<Button, String> buttons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
        gridLayout = findViewById(R.id.gridLayout);

        drawableIds = new int[] {
                R.drawable.jettchibi, R.drawable.neonchibi, R.drawable.phoenixchibi, R.drawable.razechibi, R.drawable.reynachibi, R.drawable.viperchibi,
                R.drawable.jettchibi, R.drawable.neonchibi, R.drawable.phoenixchibi, R.drawable.razechibi, R.drawable.reynachibi, R.drawable.viperchibi
        };

        addCardsToGridLayout(4,3);

        // LOOPING THE BUTTONS AND SET ON CLICK LISTENER ON EACH BUTTON
        gridLayout.post(() -> {
            buttons.keySet().forEach(btn -> {
                btn.setOnClickListener(this::onClick);
                flipAndChangeBackground(btn, getDrawableId(getValueByKey(btn)), false); // SHOW THE FRONT OF THE CARDS
            });
        });

        // Delayed flipping back to default state
        gridLayout.postDelayed(() -> {
            gridLayout.post(() -> {
                buttons.keySet().forEach(btn -> {
                    flipAndChangeBackground(btn, R.drawable.valorantlogo, true); // FLIP THE CARDS BACK
                });
            });
        }, 2000);
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
                }, 500);
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
                button.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.valorantlogo,null));
                button.setText("Unflipped");

                GridLayout.LayoutParams layoutParams =
                        new GridLayout.LayoutParams(new ViewGroup.LayoutParams(totalSpacing, totalSpacing));
                layoutParams.setMargins(0, 0, spacing, spacing);

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
                    btn.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.valorantlogo,null));
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
                return R.drawable.jettchibi;
            case "neonchibi":
                return R.drawable.neonchibi;
            case "phoenixchibi":
                return R.drawable.phoenixchibi;
            case "razechibi":
                return R.drawable.razechibi;
            case "reynachibi":
                return R.drawable.reynachibi;
            case "viperchibi":
                return R.drawable.viperchibi;
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