package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText email_edit_text, password_editText, confirm_password_editText;
    Button create_account_button;
    ProgressBar progress_bar;
    TextView login_text_view_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        email_edit_text=findViewById(R.id.email_edit_text);
        password_editText=findViewById(R.id.password_editText);
        confirm_password_editText=findViewById(R.id.confirm_password_editText);
        create_account_button=findViewById(R.id.create_account_button);
        progress_bar=findViewById(R.id.progress_bar);
        login_text_view_btn=findViewById(R.id.login_text_view_btn);



        create_account_button.setOnClickListener(view-> createAccount());
        login_text_view_btn.setOnClickListener(view->finish());

    }

    private void createAccount() {

        String email= email_edit_text.getText().toString();
        String password= password_editText.getText().toString();
        String confirmPassword= confirm_password_editText.getText().toString();

        boolean isValidated=validateData(email,password,confirmPassword);

        if(!isValidated)
        {
            return;
        }

        createAccountInFirebase(email,password);
    }

    void createAccountInFirebase(String email,String password)
    {
        changeInProgress(true);

        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //after clicking on the "CREATE ACCOUNT" button, the progress bar should disappear
                        changeInProgress(false);
                        if(task.isSuccessful())
                        {
                            //It means that creating account is done
                            Toast.makeText(CreateAccountActivity.this,"Successfully created an account! Check your email to verify.",Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else
                        {
                            //failed to create account
                            Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    void changeInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progress_bar.setVisibility(View.VISIBLE);
            create_account_button.setVisibility(View.GONE);
        }
        else
        {
            progress_bar.setVisibility(View.GONE);
            create_account_button.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword)
    {
        //validate the data that are input by user.
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            email_edit_text.setError("Invalid e-mail");
            return false;
        }
        if(password.length()<6)
        {
            password_editText.setError("Password must be at least 6 characters");
            return false;
        }
        if(!password.equals(confirmPassword))
        {
            confirm_password_editText.setError("Passwords don't match");
            return false;
        }
        return true;
    }
}