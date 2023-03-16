package com.example.a6_mar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    List<ChatModel> list;
    List<ChatMessage> messages;
    EditText editText;
    ImageView imageViewSend;

    //text to speech
    TextToSpeech textToSpeech;

    //ChatGPT, make API requests using HTTP requests
    final String apiKey = "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";
    String token = System.getenv(apiKey);
    String text = "";
    final String apiUrl = "https://api.openai.com/v1/chat/completions?prompt=" + text + "&max_tokens=128";
    OpenAiService openAiService;

    OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenAiApiSetUp();
        setInput();
        Objects.requireNonNull(getSupportActionBar()).hide();
        setRecyclerView();
        client = new OkHttpClient();
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
        setUpTextToSpeech();
    }

    private void setUpTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(new Locale("yue", "HK"));
                }
            }
        });
    }

    private void OpenAiApiSetUp() {
        openAiService = new OpenAiService(apiKey);
        messages = new ArrayList<>();
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<ChatModel>();
        //list.add(new ChatModel(false, "What are you?"));
        //list.add(new ChatModel(true, "I am ChatGPT, a language model created by OpenAI. I am designed to help answer questions and provide information on a wide range of topics. How can I assist you today?"));

        chatAdapter = new ChatAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(chatAdapter);
    }

    private void setInput() {
        editText = findViewById(R.id.edit_text);
        imageViewSend = findViewById(R.id.send_button);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(list.size() - 1);
            }
        });

        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                text = editText.getText().toString().trim();
                list.add(new ChatModel(false, text));
                Toast.makeText(getApplicationContext(), "Sending", Toast.LENGTH_SHORT).show();
                getChatGPTResponse(text);
                editText.getText().clear();
            }
        });
    }

    private void getChatGPTResponse(String text) {
        String stringResponse = "";
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), text);
        messages.add(systemMessage);
        /*
        String responseData;
        //Create an HTTP request object and set the appropriate headers and parameters
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try {
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (responseData != null) {
            list.add(new ChatModel(true, responseData));
        } else {
            list.add(new ChatModel(false, "Failed to get response from ChatGPT"));
        }
         */

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(2.0)
                .n(1)
                .maxTokens(2000)
                .logitBias(new HashMap<>())
                .build();

        try {
            List<ChatCompletionChoice> choices = openAiService.createChatCompletion(chatCompletionRequest).getChoices();
            for (ChatCompletionChoice n : choices) {
                stringResponse = n.getMessage().getContent();
                list.add(new ChatModel(true, stringResponse));
            }
        } catch (Exception e) {
            stringResponse = "Damn! Error";
            list.add(new ChatModel(true, stringResponse));
        }

        textToSpeech.speak(stringResponse, TextToSpeech.QUEUE_FLUSH, null, "tt");
        recyclerView.scrollToPosition(list.size() - 1);
    }

    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}