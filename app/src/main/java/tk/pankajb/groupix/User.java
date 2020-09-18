package tk.pankajb.groupix;

public class User {

    private String eMail;
    private String name;
    private String lastName;
    private String profileImage;
    private String profileThumbImage;

    public User() { // Required for Firebase

    }

    public User(String eMail, String name, String lastName, String profileImage, String profileThumbImage) {
        this.eMail = eMail;
        this.name = name;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.profileThumbImage = profileThumbImage;
    }

    public String geteMail() {
        return eMail;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getProfileThumbImage() {
        return profileThumbImage;
    }

    public String getFullName() {
        return String.format("%s %s", name, lastName);
    }
}
