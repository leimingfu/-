package com.Healthy.Login;


import java.lang.ref.SoftReference;

public class Result {
    private Integer id;
    private String user;
    private String pwd;
    private String pwd_remember;
    private String date;
    private String step;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd_remember() {
        return pwd_remember;
    }

    public void setPwd_remember(String pwd_remember) {
        this.pwd_remember = pwd_remember;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", pwd='" + pwd + '\'' +
                ", pwd_remember='" + pwd_remember + '\'' +
                ", date='" + date + '\'' +
                ", step='" + step + '\'' +
                '}';
    }
}
