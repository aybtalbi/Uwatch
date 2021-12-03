package iao.project.uwatch.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Parcelable {

    private String nom;
    private String prenom;
    private String userName;
    private String mail;
    private String password;
    private Image image;
    private String etat;
    private HashMap<String, String> rateList = new HashMap<>();
    private ArrayList<String> likeList = new ArrayList();
    private ArrayList<String> hateList = new ArrayList();
    private ArrayList<String> favoriteList = new ArrayList();
    private MyList list;

    public User()
    {

    }
    public User(String username , String nom , String prenom , String mail , String password , String etat)
    {
        this.userName = username;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.password = password;
        this.etat = etat;
    }

    public User(String username , String nom , String mail)
    {
        this.userName = username;
        this.nom = nom;
        this.mail = mail;
    }

    public User(Parcel in)
    {
        nom = in.readString();
        prenom = in.readString();
        userName = in.readString();
        mail = in.readString();
        password = in.readString();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEtat() { return etat; }

    public void setEtat(String etat) { this.etat = etat; }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public MyList getList() {
        return list;
    }

    public void setList(MyList list) {
        this.list = list;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeString(userName);
        dest.writeString(mail);
        dest.writeString(password);
    }


    public ArrayList<String> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(ArrayList<String> favoriteList) {
        this.favoriteList = favoriteList;
    }

    public ArrayList<String> getHateList() {
        return hateList;
    }

    public void setHateList(ArrayList<String> hateList) {
        this.hateList = hateList;
    }

    public ArrayList<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(ArrayList<String> likeList) {
        this.likeList = likeList;
    }

    public HashMap<String, String> getRateList() {
        return rateList;
    }

    public void setRateList(HashMap<String, String> rateList) {
        this.rateList = rateList;
    }
}
