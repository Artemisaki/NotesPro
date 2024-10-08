package com.example.notespro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton add_note_button;
    RecyclerView recycler_view;
    ImageView menu_button;

    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_note_button = findViewById(R.id.add_note_button);
        recycler_view = findViewById(R.id.recycler_view);
        menu_button = findViewById(R.id.menu_button);

        add_note_button.setOnClickListener((v)-> startActivity(new Intent(MainActivity.this,NoteDetailsActivity.class)) );
        setupRecyclerView();
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                //finish();
                            }
                        }).setNegativeButton("Cancel", null);

                AlertDialog alert1 = alert.create();
                alert1.show();
            }
        });

    }


    void setupRecyclerView(){
        Query query  = Utility.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options,this);
        recycler_view.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}