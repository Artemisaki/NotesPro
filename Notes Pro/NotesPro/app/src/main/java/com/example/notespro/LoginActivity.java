package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email_edit_text, password_editText;
    Button login_button;
    ProgressBar progress_bar;
    TextView create_account_text_view_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_edit_text=findViewById(R.id.email_edit_text);
        password_editText=findViewById(R.id.password_editText);
        login_button=findViewById(R.id.login_button);
        progress_bar=findViewById(R.id.progress_bar);
        create_account_text_view_btn=findViewById(R.id.create_account_text_view_btn);

        login_button.setOnClickListener(view-> loginUser());
        create_account_text_view_btn.setOnClickListener(view-> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));

    }

    void loginUser()
    {
        String email= email_edit_text.getText().toString();
        String password= password_editText.getText().toString();

        boolean isValidated=validateData(email,password);

        if(!isValidated)
        {
            return;
        }

        loginAccountInFirebase(email,password);
    }

    void loginAccountInFirebase(String email, String password)
    {
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        changeInProgress(true);
        //Check if the login was successful or not by displaying toast messages
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful())
                {
                    //Successful login
                    if(firebaseAuth.getCurrentUser().isEmailVerified())
                    {
                        //go to MainActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Email not verified. Please verify your email.",Toast.LENGTH_SHORT).show();

                    }

                }
                else
                {
                    //login failed
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    void changeInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progress_bar.setVisibility(View.VISIBLE);
            login_button.setVisibility(View.GONE);
        }
        else
        {
            progress_bar.setVisibility(View.GONE);
            login_button.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password)
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

        return true;
    }
}