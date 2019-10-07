package com.example.hyvysocialapp;

public class screenitem {
    String Title,Descreption;
    int Screenimg;

    public screenitem(String title, String descreption, int screenimg) {
        Title = title;
        Descreption = descreption;
        Screenimg = screenimg;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescreption() {
        return Descreption;
    }

    public void setDescreption(String descreption) {
        Descreption = descreption;
    }

    public int getScreenimg() {
        return Screenimg;
    }

    public void setScreenimg(int screenimg) {
        Screenimg = screenimg;
    }
}
