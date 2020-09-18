package tk.pankajb.groupix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

import tk.pankajb.groupix.handlers.DataStore;

public class SignInActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPass;
    private Button signInBtn;

    private ProgressDialog signInProgressDialog;

    private DataStore appData = new DataStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar signInToolBar = findViewById(R.id.SignInToolBar);
        setSupportActionBar(signInToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userEmail = findViewById(R.id.SignInMail);
        userPass = findViewById(R.id.SignInPass);
        signInBtn = findViewById(R.id.SignInSubmit);
    }

    @Override
    protected void onStart() {
        super.onStart();

        signInProgressDialog = new ProgressDialog(SignInActivity.this);
        signInProgressDialog.setTitle("Signing you in");
        signInProgressDialog.setCanceledOnTouchOutside(false);
        signInProgressDialog.setCancelable(false);

    }

    public void signIn(View view) {

        String userInputMail = userEmail.getText().toString();
        String userInputPass = userPass.getText().toString();

        if (userInputMail.isEmpty()) {
            Toast.makeText(SignInActivity.this, R.string.Email_Required, Toast.LENGTH_SHORT).show();
        } else if (userInputPass.isEmpty()) {
            Toast.makeText(SignInActivity.this, R.string.Password_Required, Toast.LENGTH_SHORT).show();
        } else if (userInputPass.length() <= 5) {
            Toast.makeText(SignInActivity.this, R.string.Invalid_Password, Toast.LENGTH_LONG).show();
        } else {
            signInProgressDialog.show();
            view.setEnabled(false);

            appData.Auth.signInWithEmailAndPassword(userInputMail, userInputPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    signInProgressDialog.dismiss();
                    signInBtn.setEnabled(true);

                    if (!appData.getCurrentUser().isEmailVerified()) {
                        reSendVerification();
                    } else {
                        sendToMain();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    signInProgressDialog.dismiss();
                    signInBtn.setEnabled(true);
                    Log.e("SignInError", e.getMessage(), e.getCause());

                }
            });
        }
    }

    private void sendToMain() {
        Intent SendingToMain = new Intent(SignInActivity.this, HomeActivity.class);
        SendingToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SendingToMain);
        finish();
    }

    private void reSendVerification() {

        appData.getCurrentUser().sendEmailVerification();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(SignInActivity.this);
        appData.Auth.signOut();
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
    }
}
