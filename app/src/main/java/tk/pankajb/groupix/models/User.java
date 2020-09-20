package tk.pankajb.groupix.models;

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
        this.name = name.trim();
        this.lastName = lastName.trim();
        this.profileImage = profileImage.trim();
        this.profileThumbImage = profileThumbImage.trim();
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

    public void seteMail(String eMail) {
        this.eMail = eMail.trim();
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage.trim();
    }

    public void setProfileThumbImage(String profileThumbImage) {
        this.profileThumbImage = profileThumbImage.trim();
    }
}
