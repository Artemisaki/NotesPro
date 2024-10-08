package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText notes_title_text,notes_content_text;
    ImageButton save_button;
    TextView page_title, delete_note_button;
    String title,content,docId;
    boolean isEditMode= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        notes_title_text = findViewById(R.id.notes_title_text);
        notes_content_text = findViewById(R.id.notes_content_text);
        save_button = findViewById(R.id.save_button);
        page_title = findViewById(R.id.page_title);
        delete_note_button  = findViewById(R.id.delete_note_button);

        //receive data
        title = getIntent().getStringExtra("title");
        content= getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        notes_title_text.setText(title);
        notes_content_text.setText(content);
        if(isEditMode){
            page_title.setText("Edit your note");
            delete_note_button.setVisibility(View.VISIBLE);
        }

        save_button.setOnClickListener( (v)-> saveNote());

        delete_note_button.setOnClickListener((v)-> deleteNoteFromFirebase() );

    }

    void saveNote(){
        String noteTitle = notes_title_text.getText().toString();
        String noteContent = notes_content_text.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty() ){
            notes_title_text.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }



        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note added successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
                }
            }
        });

    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");
                }
            }
        });
    }

}