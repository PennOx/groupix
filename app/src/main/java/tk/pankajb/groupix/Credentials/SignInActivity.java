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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import tk.pankajb.groupix.DataStore;
import tk.pankajb.groupix.Home.HomeActivity;
import tk.pankajb.groupix.R;

public class SignInActivity extends AppCompatActivity {

    EditText UserEmail;
    EditText UserPass;
    Button SignInSubmit;
    ProgressBar loading;

    String Verified;

    String UserInputMail;
    String UserInputPass;
    DataStore AppData = new DataStore();
    private Toolbar SignInToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInToolBar = findViewById(R.id.signinappbar);
        setSupportActionBar(SignInToolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserEmail = findViewById(R.id.SignInMail);
        UserPass = findViewById(R.id.SignInPass);
        SignInSubmit = findViewById(R.id.SignInSubmit);
        loading = findViewById(R.id.SignInProgressBar);
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

                            AppData.getAllUserDataRef().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child(AppData.getCurrentUserId()).child("status").getValue().toString().equals("1")) {
                                        Verified = "1";
                                    } else if (dataSnapshot.child(AppData.getCurrentUserId()).child("status").getValue().toString().equals("0")) {
                                        Verified = "0";
                                    }

                                    if (dataSnapshot.child(AppData.getCurrentUserId()).child("status").getValue().toString().equals("1") && AppData.getCurrentUser().isEmailVerified()) {

                                        SendToMain();

                                    } else if (!AppData.getCurrentUser().isEmailVerified()) {

                                        ResendVerification();

                                    } else if (dataSnapshot.child(AppData.getCurrentUserId()).child("status").getValue().toString().equals("0") && AppData.getCurrentUser().isEmailVerified()) {

                                        CreateVerifiedAccount();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
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

    void SendToMain() {
        Intent SendingToMain = new Intent(SignInActivity.this, HomeActivity.class);
        SendingToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SendingToMain);
        finish();
    }

    void ResendVerification() {

        AppData.Auth.getCurrentUser().sendEmailVerification();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(SignInActivity.this);

        builder2.setMessage(R.string.Verification_Mail_Sent);
        builder2.setCancelable(false);
        builder2.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppData.Auth.signOut();
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert1 = builder2.create();
        alert1.show();
    }

    void CreateVerifiedAccount() {

        AppData.getUnVerifiedUserDataRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, String> CreateVerifiedMap = new HashMap<>();
                CreateVerifiedMap.put("name", AppData.getCurrentUserName());
                CreateVerifiedMap.put("lastname", dataSnapshot.child(AppData.getCurrentUserId()).child("lastname").getValue(String.class));
                CreateVerifiedMap.put("email", AppData.getCurrentUser().getEmail());
                CreateVerifiedMap.put("Pass", UserInputPass);
                CreateVerifiedMap.put("profileImage", "default");
                CreateVerifiedMap.put("profileThumbImage", "default");

                AppData.getUserDataRef().child("verified").child(AppData.getCurrentUserId()).setValue(CreateVerifiedMap);

                AppData.getUserDataRef().child("unverified").child(AppData.getCurrentUserId()).setValue(null);
                AppData.getUserDataRef().child("all").child(AppData.getCurrentUserId()).child("status").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SendToMain();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
