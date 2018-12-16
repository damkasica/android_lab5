package com.example.kasica.chat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public static String ip;
    public static String nick;
    ListView chatListView;
    EditText messEditText;
    Button sendButton;
    TextView nickTextView;


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            listItems.add("["+msg.getData().getString("NICK") + "]" +
                    msg.getData().getString("MSG"));
            adapter.notifyDataSetChanged();
            chatListView.setSelection(listItems.size()-1);
        }
    };

    public void postOnClick(View view){
        Message msg = myHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("NICK", "JA");
        b.putString("MSG", messEditText.getText().toString());
        msg.setData(b);
        myHandler.sendMessage(msg);
    }

    MqttClient sampleClient=null;
    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://"+ip+":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }
                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", nick);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);}

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);

        nickTextView = findViewById(R.id.nickTextView);
        sendButton = findViewById(R.id.sendButton);
        messEditText = findViewById(R.id.messEditText);
        chatListView = findViewById(R.id.chatListView);

        nickTextView.setText(getIntent().getStringExtra(MainActivity.NICK));
        //Toast.makeText(ChatActivity.this, ip, Toast.LENGTH_LONG).show();

        //w metodzie onCreate obslugujemy dodwanie wiadomosci do listy
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        chatListView.setAdapter(adapter);

        //uruchamiamy MQTT w tle
        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


}

