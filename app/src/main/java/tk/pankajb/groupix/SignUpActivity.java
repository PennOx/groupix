package tk.pankajb.groupix;

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

import tk.pankajb.groupix.handlers.DataStore;
import tk.pankajb.groupix.handlers.Mapper;

public class SignUpActivity extends AppCompatActivity {

    private EditText newUserName;
    private EditText newUserMail;
    private EditText newUserPass;
    private EditText newUserLastName;
    private Button signUpBtn;

    private DataStore appData = new DataStore();
    private ProgressDialog signUpProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        newUserName = findViewById(R.id.SignUpName);
        newUserMail = findViewById(R.id.SignUpMail);
        newUserPass = findViewById(R.id.SignUpPass);
        newUserLastName = findViewById(R.id.SignUpLastName);
        signUpBtn = findViewById(R.id.SignUpSubmit);

        Toolbar signUpToolbar = findViewById(R.id.SignUpToolBar);
        setSupportActionBar(signUpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        signUpProgressDialog = new ProgressDialog(SignUpActivity.this);
        signUpProgressDialog.setTitle(getString(R.string.SIGNUP_DIALOG_TITLE));
        signUpProgressDialog.setCanceledOnTouchOutside(false);
        signUpProgressDialog.setCancelable(false);

    }

    public void signUp(View view) {

        final String userName = newUserName.getText().toString();
        final String userLastName = newUserLastName.getText().toString();
        final String userMail = newUserMail.getText().toString();
        final String userPass = newUserPass.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(SignUpActivity.this, R.string.Name_Required, Toast.LENGTH_LONG).show();
        } else if (userLastName.isEmpty()) {
            Toast.makeText(SignUpActivity.this, R.string.LastName_Required, Toast.LENGTH_LONG).show();
        } else if (userMail.isEmpty()) {
            Toast.makeText(SignUpActivity.this, R.string.Email_Required, Toast.LENGTH_LONG).show();
        } else if (userPass.isEmpty()) {
            Toast.makeText(SignUpActivity.this, R.string.Password_Required, Toast.LENGTH_LONG).show();
        } else if (userPass.length() < 6) {
            Toast.makeText(SignUpActivity.this, R.string.Password_Length, Toast.LENGTH_LONG).show();
        } else {
            signUpProgressDialog.show();
            view.setEnabled(false);

            appData.Auth.createUserWithEmailAndPassword(userMail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Mapper mapper = new Mapper(SignUpActivity.this);

                        UserProfileChangeRequest NewUserUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
                        appData.Auth.getCurrentUser().updateProfile(NewUserUpdates);

                        appData.getUsersDataRef().child(appData.getCurrentUserId())
                                .setValue(mapper.getNewUserMap(userName, userLastName, userMail, userPass)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                signUpProgressDialog.dismiss();

                                appData.getCurrentUser().sendEmailVerification();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpActivity.this);
                                builder1.setMessage(R.string.Verification_Mail_Sent);
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                appData.Auth.signOut();
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