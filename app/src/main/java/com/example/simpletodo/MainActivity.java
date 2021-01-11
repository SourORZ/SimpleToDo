package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    Button ADDbtn;
    EditText Entered;
    RecyclerView List;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ADDbtn = findViewById(R.id.ADDbtn);
        Entered = findViewById(R.id.Entered);
        List = findViewById(R.id.List);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item was removed",Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Mainactivity","Single click at position" + position);
                //Create the new activity
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                //pass the data being edited
            i.putExtra(KEY_ITEM_TEXT,items.get(position));
            i.putExtra(KEY_ITEM_POSITION,position);
                //display rhe activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(items,onLongClickListener,onClickListener);
        List.setAdapter(itemsAdapter);
        List.setLayoutManager(new LinearLayoutManager(this));

        ADDbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String todoItem = Entered.getText().toString();
               //Add item to the model
                items.add(todoItem);
                //Notify adapter that an item is instered
                itemsAdapter.notifyItemInserted(items.size()-1);
                Entered.setText("");
                Toast.makeText(getApplicationContext(),"Item was added",Toast.LENGTH_SHORT).show();
                saveItems();



            }
        });
    }

    //handle the result of the edit activity


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(),"Item updated successly",Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknonwn call to onActivityResult");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(),"data.txt");
        }
    //This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        }catch(IOException e){
            Log.e("MainActivity", "Error reading items",e);
            items = new ArrayList<>();
        }
    }
    //This function will save items by weiting them into the data file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch(IOException e){
            Log.e("MainActivity", "Error reading items",e);

        }
    }
}