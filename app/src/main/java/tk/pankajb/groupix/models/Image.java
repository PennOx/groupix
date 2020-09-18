package tk.pankajb.groupix.models;

public class Image {

    private String image;

    public Image() {
        // required for firebase
    }

    public Image(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
