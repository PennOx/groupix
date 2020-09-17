package tk.pankajb.groupix;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private DataStore AppData = new DataStore();
    private ActionHandler handler = new ActionHandler(SplashScreen.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (AppData.Auth.getCurrentUser() == null) {
            handler.logOut();
            finish();
        } else if (AppData.getCurrentUser() != null && !AppData.getCurrentUser().isEmailVerified()) {
            handler.logOut();
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