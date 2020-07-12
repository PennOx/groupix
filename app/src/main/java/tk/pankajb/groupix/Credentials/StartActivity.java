package tk.pankajb.groupix.Credentials;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import tk.pankajb.groupix.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onStart() {
        super.onStart();

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
