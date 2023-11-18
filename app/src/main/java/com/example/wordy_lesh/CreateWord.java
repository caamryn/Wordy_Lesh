package com.example.wordy_lesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class CreateWord extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private EditText newWord;
    private TextView label;
    private Button save;
    private Button toMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_word);

        //newWord = findViewById(R.id.newWord_ET);
        save = findViewById(R.id.save_button);
        toMain = findViewById(R.id.return_button);
        label = findViewById(R.id.question_TV);

        save.setOnClickListener(saveListener);
        toMain.setOnClickListener(toMainListener);
        save.setBackgroundColor(getResources().getColor(R.color.white));
        toMain.setBackgroundColor(getResources().getColor(R.color.white));


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Word");
    }

    View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newWord = findViewById(R.id.newWord_ET);
            String s = newWord.getText().toString().toLowerCase();

            if(check(s)){   //checks to make sure word is 5 letters and only letters
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(isDupe(snapshot, s)){  //checks if word already exists
                            String st = databaseReference.push().getKey(); //gets key for new word
                            databaseReference.child(st).setValue(newWord.getText().toString()); // adds new word
                            String message = "Your word was added to the word bank!";
                            Toast.makeText(CreateWord.this, message, Toast.LENGTH_LONG).show();
                        }
                        else{
                            String message = "That word already exists";
                            Toast.makeText(CreateWord.this, message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }
    };

    private Boolean isDupe(DataSnapshot snapshot, String s){
        snapshot.getChildren();
        if(snapshot.getChildrenCount() != 0){
            Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
            String temp;
            for(int i = 0; i < snapshot.getChildrenCount(); i++){
                temp = it.next().getValue(String.class);
                if(temp.equals(s)){
                    label.setTextColor(getColor(R.color.purple));
                    label.setBackgroundColor(getColor(R.color.white));
                    return false;
                }
            }
        }
        return true;
    }


    View.OnClickListener toMainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(CreateWord.this, MainActivity.class);
            startActivity(i);

        }
    };

    public boolean check(String s){
        //s.toLowerCase();
        if(s.length() != 5){
            label.setTextColor(getColor(R.color.purple));
            label.setBackgroundColor(getColor(R.color.white));
            String message = "The length of the word must be 5 letters";
            Toast.makeText(CreateWord.this, message, Toast.LENGTH_LONG).show();
            return false;
        }
        for(int i = 0; i < s.length(); i++){
            if(!Character.isLetter(s.charAt(i))){
                label.setTextColor(getColor(R.color.purple));
                label.setBackgroundColor(getColor(R.color.white));
                String message = "The word can only contain letters";
                Toast.makeText(CreateWord.this, message, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        //check is word is duplicate
        return true;
    }
}