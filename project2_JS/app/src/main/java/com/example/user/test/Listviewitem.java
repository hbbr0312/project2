package com.example.user.test;

import android.graphics.Bitmap;

public class Listviewitem {
    private Bitmap icon;
    private String name;
    private String id;
    private String date;
    private Bitmap photo;
    private String text;

    public Bitmap getIcon(){return icon;}
    public String getName(){return name;}
    public String getDate(){return date;}
    public String getId(){return id;}
    public Bitmap getPhoto(){return photo;}
    public String getText() {return text;}

    public Listviewitem(Bitmap icon, String name, String id, String date, Bitmap photo, String text){
        this.icon=icon;
        this.name=name;
        this.id=id;
        this.date=date;
        this.photo=photo;
        this.text=text;
    }
}