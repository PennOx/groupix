package tk.pankajb.groupix.Credentials;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.R;

public class SignUpActivity extends AppCompatActivity {

    Toolbar SignUpToolbar;
    EditText NewUserName;
    EditText NewUserMail;
    EditText NewUserPass;
    EditText NewUserLastName;
    Button signUpBtn;

    DataStore AppData = new DataStore();
    ProgressDialog signUpProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        NewUserName = findViewById(R.id.SignUpName);
        NewUserMail = findViewById(R.id.SignUpMail);
        NewUserPass = findViewById(R.id.SignUpPass);
        NewUserLastName = findViewById(R.id.SignUpLastName);
        signUpBtn = findViewById(R.id.SignUpSubmit);

        SignUpToolbar = findViewById(R.id.SignUpToolBar);
        setSupportActionBar(SignUpToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        signUpProgressDialog = new ProgressDialog(SignUpActivity.this);
        signUpProgressDialog.setTitle("Creating account");
        signUpProgressDialog.setCanceledOnTouchOutside(false);
        signUpProgressDialog.setCancelable(false);

    }

    public void signUp(View view) {


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
            signUpProgressDialog.show();
            view.setEnabled(false);

            AppData.Auth.createUserWithEmailAndPassword(UserMail, UserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("Name", UserName);
                        userMap.put("LastName", UserLastName);
                        userMap.put("Email", UserMail);
                        userMap.put("Pass", UserPass);
                        userMap.put("ProfileThumbImage", "default");
                        userMap.put("ProfileImage", "default");

                        UserProfileChangeRequest NewUserUpdates = new UserProfileChangeRequest.Builder().setDisplayName(UserName).build();
                        AppData.Auth.getCurrentUser().updateProfile(NewUserUpdates);

                        AppData.getUsersDataRef().child(AppData.getCurrentUserId()).setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                signUpProgressDialog.dismiss();

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
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                signUpProgressDialog.dismiss();
                                signUpBtn.setEnabled(true);
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signUpBtn.setEnabled(true);
                    signUpProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}