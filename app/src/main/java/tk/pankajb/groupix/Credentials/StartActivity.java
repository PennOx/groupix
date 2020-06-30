package tk.pankajb.groupix.Credentials;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import tk.pankajb.groupix.R;

public class StartActivity extends AppCompatActivity {

    Button SignInButton;
    Button SignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SignInButton = findViewById(R.id.SignInButton);
        SignUpButton = findViewById(R.id.SignUpButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SendToSignInActivity = new Intent(StartActivity.this, SignInActivity.class);
                startActivity(SendToSignInActivity);
            }
        });

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SendToSignUpActivity = new Intent(StartActivity.this, SignUpActivity.class);
                startActivity(SendToSignUpActivity);
            }
        });
    }

}
