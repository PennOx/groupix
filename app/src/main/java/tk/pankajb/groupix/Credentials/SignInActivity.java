package tk.pankajb.groupix.Credentials;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.Home.HomeActivity;
import tk.pankajb.groupix.R;

public class SignInActivity extends AppCompatActivity {

    EditText UserEmail;
    EditText UserPass;
    Button SignInSubmit;
    ProgressBar loading;
    Toolbar signInToolBar;

    String UserInputMail;
    String UserInputPass;
    DataStore AppData = new DataStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInToolBar = findViewById(R.id.signinappbar);
        setSupportActionBar(signInToolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserEmail = findViewById(R.id.SignInMail);
        UserPass = findViewById(R.id.SignInPass);
        SignInSubmit = findViewById(R.id.SignInSubmit);
        loading = findViewById(R.id.SignInProgressBar);
        loading.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();


        SignInSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignInSubmit.setEnabled(false);
                loading.setVisibility(View.VISIBLE);

                UserInputMail = UserEmail.getText().toString();
                UserInputPass = UserPass.getText().toString();

                if (UserInputMail.isEmpty()) {
                    Toast.makeText(SignInActivity.this, R.string.Email_Required, Toast.LENGTH_SHORT).show();
                } else if (UserInputPass.isEmpty()) {
                    Toast.makeText(SignInActivity.this, R.string.Password_Required, Toast.LENGTH_SHORT).show();
                } else if (UserInputPass.length() <= 5) {
                    Toast.makeText(SignInActivity.this, R.string.Invalid_Password, Toast.LENGTH_LONG).show();
                } else {
                    AppData.Auth.signInWithEmailAndPassword(UserInputMail, UserInputPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            if (!AppData.getCurrentUser().isEmailVerified()) {
                                reSendVerification();
                            } else {
                                sendToMain();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            Log.e("SignInError", e.getMessage(), e.getCause());

                        }
                    });
                }

                SignInSubmit.setEnabled(true);
                loading.setVisibility(View.GONE);
            }
        });
    }

    void sendToMain() {
        Intent SendingToMain = new Intent(SignInActivity.this, HomeActivity.class);
        SendingToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loading.setVisibility(View.GONE);
        startActivity(SendingToMain);
        finish();
    }

    void reSendVerification() {

        AppData.Auth.getCurrentUser().sendEmailVerification();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(SignInActivity.this);
        AppData.Auth.signOut();
        builder2.setMessage(R.string.Verification_Mail_Sent);
        builder2.setCancelable(false);
        builder2.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert1 = builder2.create();
        alert1.show();
        loading.setVisibility(View.GONE);
    }
}
