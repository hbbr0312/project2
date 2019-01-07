package com.example.user.test;

public class Listviewitem {
    private String icon;
    private String name;
    private String id;
    private String date;
    private String photo;
    private String text;

    public String getIcon(){return icon;}
    public String getName(){return name;}
    public String getDate(){return date;}
    public String getId(){return id;}
    public String getPhoto(){return photo;}
    public String getText() {return text;}

    public Listviewitem(String icon,String name,String id, String date, String photo,String text){
        this.icon=icon;
        this.name=name;
        this.id=id;
        this.date=date;
        this.photo=photo;
        this.text=text;
    }
}