package com.example.womennetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    //Firebase auth object //
    private FirebaseAuth firebaseAuth;

    //Progress Dialog //
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting Firebase auth object //
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getCurrentUser method is not null means user is already logged in //
        if(firebaseAuth.getCurrentUser() != null){
            //close this activity//
            finish();
            //Opening ProfileActivity //
            startActivity(new Intent(getApplicationContext(), Post.class));

        }

        editTextEmail = (EditText) findViewById(R.id.Email);
        editTextPassword = (EditText) findViewById(R.id.Password);
        buttonSignIn = (Button) findViewById(R.id.BSignin);
        textViewSignup  = (TextView) findViewById(R.id.SignUp);

        progressDialog = new ProgressDialog(this);

        //attaching onClick listener//
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);

    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //To Check if Email and Passwords are empty //
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the Email and Password are not empty display a progress dialog //

        progressDialog.setMessage("Hold Up!");
       // progressDialog.show();

        // User Login //
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      //  progressDialog.hide();
                        //if user is logged in //
                        if(task.isSuccessful()){
                            //Start the ProfileActivity //
                            Log.d("krishna","here");
                            finish();
                            Log.d("krishna","here2");
                            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                        }
                        if (!task.isSuccessful()) {
                            Log.e("Krishna", "onComplete: Failed=" + task.getException().getMessage());
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }

        if(view == findViewById(R.id.SignUp)) {
            progressDialog.hide();
            finish();
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
