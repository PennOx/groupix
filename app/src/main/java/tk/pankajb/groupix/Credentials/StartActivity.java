package tk.pankajb.groupix.Credentials;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import tk.pankajb.groupix.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void signIn(View view) {
        Intent SendToSignInActivity = new Intent(StartActivity.this, SignInActivity.class);
        startActivity(SendToSignInActivity);
    }

    public void signUp(View view) {
        Intent SendToSignUpActivity = new Intent(StartActivity.this, SignUpActivity.class);
        startActivity(SendToSignUpActivity);
    }
}
