package iao.project.uwatch;


public class Item {
    private String ID;
    private String imageId;
    private String title;
    private String dateYear;
    private String rate;
    private String Category;

    public Item(String id, String imageId, String title, String dateYear, String rate, String category) {
        ID = id;
        this.imageId = imageId;
        this.title = title;
        this.dateYear = dateYear;
        this.rate = rate;
        Category = category;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateYear() { return dateYear; }

    public void setDateYear(String dateYear) {
        this.dateYear = dateYear;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
