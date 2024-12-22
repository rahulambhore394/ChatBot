package com.rahul.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private EditText userMsg;
    private FloatingActionButton fbbSend;
    private final String BOT_KEY ="bot";
    private final String USER_KEY ="user";
    private ArrayList<ChatsModel>chatsModelArrayList;
    private ChatRVAdapter chatRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatsRV = findViewById(R.id.RVChats);
        userMsg = findViewById(R.id.et_message);
        fbbSend = findViewById(R.id.fbn_send);


        chatsModelArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModelArrayList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatRVAdapter);

        fbbSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMsg.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter Your Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(userMsg.getText().toString());
                userMsg.setText("");
            }
        });
    }
    private void getResponse(String message){
        chatsModelArrayList.add(new ChatsModel(message,USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=177015&key=AUviApYuZbxJC1S1&uid=&msg="+message;
        String base_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModel> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                if (response.isSuccessful()){
                    MsgModel model = response.body();
                    chatsModelArrayList.add(new ChatsModel(model.getCnt(),BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {
                chatsModelArrayList.add(new ChatsModel("Please Revert Your Question",BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
            }
        });

    }
}