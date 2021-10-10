package com.app.buna.boxsimulatorforlol.game.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.buna.boxsimulatorforlol.R;

public class CardScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_score);

        TextView tv = (TextView) findViewById(R.id.textView);
        Intent i = getIntent();
        int mistake = i.getIntExtra("mistake",0);
        int number_of_card = i.getIntExtra("numberOfCard",1);
        tv.setTextSize(30);     //set font size

        int score = (number_of_card*100) - mistake;
        int bonus = 100;

        tv.setText("\tCard" + ":  " + number_of_card + "\n\t" + "Mistake: "  + mistake + "\n\t" + "Score: " + score
                + "\n\tBONUS:  " + bonus + "\n\t" + "Total: " + (bonus + score));

        Button tryb = findViewById(R.id.trybtn);
        Button menu = findViewById(R.id.menubtn);

        tryb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CardScoreActivity.this, CardGameActivity.class);
                startActivity(i);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CardScoreActivity.this, CardGameActivity.class);
                startActivity(i);
            }
        });

    }
}