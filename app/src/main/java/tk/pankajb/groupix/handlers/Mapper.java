package tk.pankajb.groupix.handlers;

import android.content.Context;

import java.util.HashMap;

import tk.pankajb.groupix.R;

public class Mapper {

    private Context context;

    public Mapper(Context context) {
        this.context = context;
    }

    public HashMap<String, String> getNewUserMap(String name, String lastName, String userMail, String userPass) {

        HashMap<String, String> userMap = new HashMap<>();

        userMap.put("name", name);
        userMap.put("lastName", lastName);
        userMap.put("eMail", userMail);
        userMap.put("pass", userPass);
        userMap.put("profileThumbImage", context.getString(R.string.DEFAULT_USER_PROFILE_THUMB));
        userMap.put("profileImage", context.getString(R.string.DEFAULT_USER_PROFILE_IMAGE));

        return userMap;
    }
}
