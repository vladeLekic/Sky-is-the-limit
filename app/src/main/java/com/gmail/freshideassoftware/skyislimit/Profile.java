package com.gmail.freshideassoftware.skyislimit;

public class Profile {


    private static Profile profile = null;

    private int id;

    private Profile(){ }

    public static Profile getProfile(){
        if(profile==null) profile = new Profile();
        return profile;
    }

    public void setId(int _id){
        id = _id;
    }

    public int getId(){
        return id;
    }
}
