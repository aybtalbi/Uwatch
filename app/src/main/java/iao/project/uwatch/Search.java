package iao.project.uwatch;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Search extends FragmentActivity implements View.OnClickListener {
    private Button buttonSearch, category;
    private ImageButton back;


    List<String> autoComplet = new ArrayList<>();
    AutoCompleteTextView autocompletInput;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //for add a auto completion
        autocompletInput = findViewById(R.id.inputSearch);
        getListAutoComplet();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, autoComplet);
        autocompletInput.setAdapter(adapter);



        //for button search

        buttonSearch = findViewById(R.id.btn_search);
        buttonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(Search.this, SearchResult.class);
            intent.putExtra("typeResult", "search");
            intent.putExtra("search", autocompletInput.getText().toString());
            startActivity(intent);
        });

        //for button Back
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        // for category buttons
        findViewById(R.id.Category_action).setOnClickListener(this);
        findViewById(R.id.Category_Animation).setOnClickListener(this);
        findViewById(R.id.Category_Comedy).setOnClickListener(this);
        findViewById(R.id.Category_Crime).setOnClickListener(this);
        findViewById(R.id.Category_drama).setOnClickListener(this);
        findViewById(R.id.Category_Family).setOnClickListener(this);
        findViewById(R.id.Category_Thriller).setOnClickListener(this);
        findViewById(R.id.Category_Horror).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Search.this, SearchResult.class);
        String category = "";
        switch (v.getId()) {
            case R.id.Category_action:
                category = "Action";
                break;
            case R.id.Category_drama:
                category = "Drama";
                break;
            case R.id.Category_Horror:
                category = "Horror";
                break;
            case R.id.Category_Crime:
                category = "Crime";
                break;
            case R.id.Category_Comedy:
                category = "Comedy";
                break;
            case R.id.Category_Animation:
                category = "Animation";
                break;
            case R.id.Category_Thriller:
                category = "Thriller";
                break;
            case R.id.Category_Family:
                category = "Family";
                break;
        }
        intent.putExtra("typeResult", "category");
        intent.putExtra("category", category);
        startActivity(intent);
    }


    public void getListAutoComplet() {
        try {
            String str = "";
            int fileResourceId = R.raw.auto_complet;
            InputStream is = this.getResources().openRawResource(fileResourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    str = StringUtils.substringBetween(str, "\"", "\"");
                    autoComplet.add(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}




