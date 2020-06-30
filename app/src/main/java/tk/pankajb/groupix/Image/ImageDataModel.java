package tk.pankajb.groupix.Image;

public class ImageDataModel {
    String image;
    String thumb;

    public ImageDataModel(String image, String thumb) {
        this.image = image;
        this.thumb = thumb;
    }

    public ImageDataModel() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
