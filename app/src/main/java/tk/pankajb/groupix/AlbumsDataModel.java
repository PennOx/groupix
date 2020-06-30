package tk.pankajb.groupix;

public class AlbumsDataModel {

    String name;
    String coverimg;
    String description;

    public AlbumsDataModel(String name, String coverimg, String description) {

        this.name = name;
        this.coverimg = coverimg;
        this.description = description;
    }

    public AlbumsDataModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverimg() {
        return coverimg;
    }

    public void setCoverimg(String coverimg) {
        this.coverimg = coverimg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
