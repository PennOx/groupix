package tk.pankajb.groupix.handlers;

import android.content.Context;
import android.widget.Toast;

import tk.pankajb.groupix.R;
import tk.pankajb.groupix.models.User;

public class Validator {

    private Context context;

    public Validator(Context context) {
        this.context = context;
    }

    public boolean isValidNewUser(User user) {

        if (user.getName().isEmpty()) {
            Toast.makeText(context, R.string.Name_Required, Toast.LENGTH_LONG).show();
        } else if (user.getLastName().isEmpty()) {
            Toast.makeText(context, R.string.LastName_Required, Toast.LENGTH_LONG).show();
        } else if (user.geteMail().isEmpty()) {
            Toast.makeText(context, R.string.Email_Required, Toast.LENGTH_LONG).show();
        } else {
            return true;
        }

        return false;
    }

    public boolean isPasswordValid(String userPass) {

        if (userPass.isEmpty()) {
            Toast.makeText(context, R.string.Password_Required, Toast.LENGTH_LONG).show();
        } else if (userPass.length() < 6) {
            Toast.makeText(context, R.string.Password_Length, Toast.LENGTH_LONG).show();
        } else {
            return true;
        }

        return false;
    }
}
