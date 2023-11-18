package com.example.wordy_lesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.GridLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private GridLayout grid;
    private Button submit;
    private Button clear;
    private Button restart;
    private Button addWord;
    String word;
    int row;   //this keeps track of what row the user is guessing on (tracks num of guesses)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Word");

        grid = findViewById(R.id.grid_Layout);
        row = 0;
        submit = findViewById(R.id.submit_button);
        clear = findViewById(R.id.clear_button);
        restart = findViewById(R.id.restart_button);
        addWord = findViewById(R.id.add_button);

        submit.setOnClickListener(submitListener);
        clear.setOnClickListener(clearListener);
        restart.setOnClickListener(restartListener);
        addWord.setOnClickListener(addListener);
        submit.setBackgroundColor(getResources().getColor(R.color.background));
        clear.setBackgroundColor(getResources().getColor(R.color.background));
        restart.setBackgroundColor(getResources().getColor(R.color.background));
        addWord.setBackgroundColor(getResources().getColor(R.color.background));

        getWord();
    }

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //row starts as 0
            //each time submit is pressed the value is increased by 5
            //so the next time you press submit the methods will know to start with the next row
            if(row<30){
                checkRightLetter();
                checkRightLocation();
            }
        row += 5;
        }
    };

    View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clear();
        }
    };

    View.OnClickListener restartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clear();
            getWord();
        }
    };

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, CreateWord.class);
            startActivity(intent);
        }
    };

    public void clear(){
        for(int i = 0; i < grid.getChildCount(); i++){
            EditText temp = (EditText) grid.getChildAt(i);
            temp.setBackgroundColor(getColor(R.color.white));
            temp.setText("_");
        }
    }
    // method checks to see if the users input has any of the correct letters
    // letter in the correct location will also be colored yellow but will be updated to green in the checkRightLocation() later
    // updates colors to gery and yellow accordingly
    public void checkRightLetter(){
        for(int i = row; i < row+5; i++){
            EditText temp = (EditText) grid.getChildAt(i);
            String st = String.valueOf(temp.getText());
            temp.setBackgroundColor(getColor(R.color.grey));
            for(int j = 0; j < 5; j++){
                Character ch = word.charAt(j);
                if(ch.equals(st.charAt(0))){
                    temp.setBackgroundColor(getColor(R.color.yellow));
                }
            }
        }
    }

    //this method checks to see if the users input has any of the correct letters in the correct location
    //updates colors to green accordingly
    public void checkRightLocation(){
        int numCorrect = 0; // checks how many green squares there are
        for(int i = 0; i < 5; i++){
            EditText temp = (EditText) grid.getChildAt(i+row);
            String st = String.valueOf(temp.getText());
            Character ch = word.charAt(i);
            if(ch.equals(st.charAt(0))){
                temp.setBackgroundColor(getColor(R.color.green));
                numCorrect += 1;
            }
        }
        if(numCorrect == 5){
            String message = "Congratulations! You win!";
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void getWord(){

        Random generator = new Random();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            //generates a random number between 0 and the number of words in the wordbank
            //An iterator runs a random number of times
            //the final word it accesses is used as the word for the game.
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren();
                if((int) snapshot.getChildrenCount() != 0){  // if there are no words in database the word will be grape
                    int randomNum = generator.nextInt((int) snapshot.getChildrenCount());
                    Iterator <DataSnapshot> it = snapshot.getChildren().iterator();
                    String temp = null;
                    for(int i = 0; i <= randomNum; i++){
                        temp = it.next().getValue(String.class);
                    }
                    word = temp;
                }
                else {
                    word = "grape";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}