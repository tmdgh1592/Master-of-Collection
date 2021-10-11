package com.app.buna.boxsimulatorforlol.game.hangman;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.game.ScoreActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class HangmanActivity extends AppCompatActivity {

    // declare variables
    TextView txtWordToBeGuessed;
    String wordToBeGuessed;
    String wordDisplayedString;
    char[] wordDisplayedCharArray;
    ArrayList<String> myListOfWords;
    EditText edtInput;
    TextView txtLettersTried;
    TextView txtTriesLeft;
    String lettersTried;
    final String MESSAGE_WITH_LETTERS_TRIED = "Letters Tried: ";
    String triesLeft;
    final String WINNING_MESSAGE = "Thresh : YOU WON";
    final String LOSING_MESSAGE = "Thresh : YOU LOST! Hahaha";

    void revealLetterInWord(char letter) {
        int indexOfLetter = wordToBeGuessed.indexOf(letter);

        //loop if index is positive or 0
        while (indexOfLetter >= 0) {
            wordDisplayedCharArray[indexOfLetter] = wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = wordToBeGuessed.indexOf(letter, indexOfLetter + 1);
        }
        //update the string as well
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);
    }

    void displayWordOnScreen() {
        StringBuilder formattedString = new StringBuilder();
        for (char character : wordDisplayedCharArray) {
            formattedString.append(character).append(" ");
        }
        txtWordToBeGuessed.setText(formattedString.toString());
    }

    void initializeGame() {
        //1.WORD
        //shuffle array list and get first element, and then remove it
        Collections.shuffle(myListOfWords);
        wordToBeGuessed = myListOfWords.get(0);
        myListOfWords.remove(0);

        //initialize char array
        wordDisplayedCharArray = wordToBeGuessed.toCharArray();

        //add underscores
        for (int i = 1; i < wordDisplayedCharArray.length - 1; i++) {
            wordDisplayedCharArray[i] = '_';
        }
        //reveal all occurrences of first character
        revealLetterInWord(wordDisplayedCharArray[0]);

        //reveal all occurrences of last character
        revealLetterInWord(wordDisplayedCharArray[wordDisplayedCharArray.length - 1]);

        //initialize a string from this char aart(for search purpose)
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);

        //display word
        displayWordOnScreen();

        //2.INPUT
        //clear input field
        edtInput.setText("");

        //3.LETTER TRIED
        //initialize string for letter tried with a space
        lettersTried = " ";

        //display on screen
        txtLettersTried.setText(MESSAGE_WITH_LETTERS_TRIED);

        //4.TRIES LEFT
        //initialize the string for tries left
        triesLeft = " ♥ ♥ ♥ ♥ ♥";
        txtTriesLeft.setText(triesLeft);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman);

        ((AdView) findViewById(R.id.banner)).loadAd(new AdRequest.Builder().build());

        //initialize Variables
        myListOfWords = new ArrayList<>();
        txtWordToBeGuessed = (TextView) findViewById(R.id.txtWordToBeGuessed);
        edtInput = (EditText) findViewById(R.id.edtInput);
        txtLettersTried = (TextView) findViewById(R.id.txtLettersTried);
        txtTriesLeft = (TextView) findViewById(R.id.txtTriesLeft);

        //traverse database file and populate array list
        InputStream myInputStream = null;
        Scanner in = null;
        String aWord;

        try {
            myInputStream = getAssets().open("hangman_words.txt");
            in = new Scanner(myInputStream);
            while (in.hasNext()) {
                aWord = in.next();
                myListOfWords.add(aWord);

            }

        } catch (IOException e) {
            Toast.makeText(HangmanActivity.this, e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            //close Scanner
            if (in != null) {
                in.close();
            }
            //close InputStream
            try {
                if (myInputStream != null) {
                    myInputStream.close();
                }
            } catch (IOException e) {
                Toast.makeText(HangmanActivity.this, e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        initializeGame();

        //setup the text changed listener for the edit text
        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if there is some letter in input field
                if (s.length() != 0) {
                    checkIfLetterIsInWord(s.charAt(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                edtInput.removeTextChangedListener(this);
                edtInput.setText("");
                edtInput.addTextChangedListener(this);
            }
        });

        final Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                initializeGame();
            }
        });
    }

    void checkIfLetterIsInWord(char letter) {
        //if letter was found inside the word to be guessed
        if (wordToBeGuessed.indexOf(letter) >= 0) {
            //if letter was not displayed yet
            if (wordDisplayedString.indexOf(letter) < 0) {
                //replace the underscore with that letter
                revealLetterInWord(letter);
                //update the changes on screen
                displayWordOnScreen();
                //check if the game is won
                if (!wordDisplayedString.contains("_")) {
                    txtTriesLeft.setText(WINNING_MESSAGE);
                    startActivity(new Intent(this, ScoreActivity.class).putExtra("from", "hangman").putExtra("score", wordToBeGuessed.length() * 10));
                    finish();
                }
            }
        }
        //if letter was not found inside the word to be guessed
        else {
            //decrease the no of tries left, and show on screen
            decreaseAndDisplayTriesLeft();
            //check if the game is lost
            if (triesLeft.isEmpty()) {
                txtTriesLeft.setText(LOSING_MESSAGE);
                txtWordToBeGuessed.setText(wordToBeGuessed);
            }
        }

        //display the letter that was tried
        if (lettersTried.indexOf(letter) < 0) {
            lettersTried += letter + ", ";
            String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
            txtLettersTried.setText(messageToBeDisplayed);
        }
    }

    void decreaseAndDisplayTriesLeft() {
        //if there are still some tries left
        if (!triesLeft.isEmpty()) {
            //take out the last 2 characters from this string
            triesLeft = triesLeft.substring(0, triesLeft.length() - 2);
            txtTriesLeft.setText(triesLeft);
        }
    }


}