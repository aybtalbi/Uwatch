package iao.project.uwatch.bean;


public class MyList {

    private int favorite;
    private int like;
    private int hate;

    public MyList()
    {

    }
    public MyList(int favorite , int like , int hate)
    {
        this.favorite = favorite;
        this.like = like;
        this.hate = hate;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getHate() {
        return hate;
    }

    public void setHate(int hate) {
        this.hate = hate;
    }
}
