package com.example.user.test;

public class Data {

    private String name;
    private String email;
    private String mobile;

    public Data()
    {
        super();
    }

    public Data(String name, String email, String mobile)
    {
        super();
        this.name = name;
        this.email = email;
        this.mobile = mobile;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
