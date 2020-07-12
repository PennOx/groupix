package tk.pankajb.groupix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    DataStore AppData = new DataStore();
    ActionHandler handler = new ActionHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (AppData.Auth.getCurrentUser() == null) {
            handler.logOut(SplashScreen.this);
            finish();
        } else if (AppData.getCurrentUser() != null && !AppData.getCurrentUser().isEmailVerified()) {
            handler.logOut(SplashScreen.this);
            finish();
        } else {
            sendToHome();
        }
    }

    private void sendToHome() {
        Intent sendToHome = new Intent(SplashScreen.this, tk.pankajb.groupix.Home.HomeActivity.class);
        sendToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToHome);
        finish();
    }

}