package tk.pankajb.groupix;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText NewUserName;
    EditText NewUserMail;
    EditText NewUserPass;
    EditText NewUserLastName;
    Button NewUserSubmit;

    Toolbar SignUpToolbar;

    DataStore AppData = new DataStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        NewUserName = findViewById(R.id.SignUpName);
        NewUserMail = findViewById(R.id.SignUpMail);
        NewUserPass = findViewById(R.id.SignUpPass);
        NewUserLastName = findViewById(R.id.SignUpLastName);
        NewUserSubmit = findViewById(R.id.SignUpSubmit);

        SignUpToolbar = findViewById(R.id.signupappbar);
        setSupportActionBar(SignUpToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        NewUserSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String UserName = NewUserName.getText().toString();
                final String UserLastName = NewUserLastName.getText().toString();
                final String UserMail = NewUserMail.getText().toString();
                final String UserPass = NewUserPass.getText().toString();

                if (UserName.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, R.string.Name_Required, Toast.LENGTH_LONG).show();
                } else if (UserLastName.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, R.string.LastName_Required, Toast.LENGTH_LONG).show();
                } else if (UserMail.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, R.string.Email_Required, Toast.LENGTH_LONG).show();
                } else if (UserPass.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, R.string.Password_Required, Toast.LENGTH_LONG).show();
                } else if (UserPass.length() <= 5) {
                    Toast.makeText(SignUpActivity.this, R.string.Password_Length, Toast.LENGTH_LONG).show();
                } else {
                    AppData.Auth.createUserWithEmailAndPassword(UserMail, UserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                HashMap<String, String> CreateUnverifiedMap = new HashMap<>();
                                CreateUnverifiedMap.put("name", UserName);
                                CreateUnverifiedMap.put("lastname", UserLastName);
                                CreateUnverifiedMap.put("email", UserMail);
                                CreateUnverifiedMap.put("Pass", UserPass);

                                HashMap<String, String> CreateAllMap = new HashMap<>();
                                CreateAllMap.put("email", UserMail);
                                CreateAllMap.put("status", "0");

                                UserProfileChangeRequest NewUserUpdates = new UserProfileChangeRequest.Builder().setDisplayName(UserName).build();

                                AppData.Auth.getCurrentUser().updateProfile(NewUserUpdates);

                                AppData.getUnVerifiedUserDataRef().child(AppData.getCurrentUserId()).setValue(CreateUnverifiedMap);

                                AppData.getAllUserDataRef().child(AppData.getCurrentUserId()).setValue(CreateAllMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        AppData.getCurrentUser().sendEmailVerification();
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpActivity.this);
                                        builder1.setMessage(R.string.Verification_Mail_Sent);
                                        builder1.setCancelable(false);
                                        builder1.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        AppData.Auth.signOut();
                                                        dialog.cancel();
                                                        finish();
                                                    }
                                                });
                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }
}