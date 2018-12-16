package com.example.kasica.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static String IP = "ip";
    public static String NICK = "nick";
    EditText ipEditText;
    EditText nickEditText;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = findViewById(R.id.ipEditText);
        nickEditText = findViewById(R.id.nickEditText);
        startButton = findViewById(R.id.startButton);

    }


    public void startOnClick(View v) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(IP, ipEditText.getText().toString());
        intent.putExtra(NICK, nickEditText.getText().toString());
        startActivity(intent);

    }
}
