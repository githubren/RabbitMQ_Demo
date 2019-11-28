package com.example.yfsl.rabbitmq_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button button_p;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_p = findViewById(R.id.btn_p);
        button_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RabbitMQUtils2.basicPublish();//发送消息
            }
        });
        RabbitMQUtils2.basicConsume();
    }
}
