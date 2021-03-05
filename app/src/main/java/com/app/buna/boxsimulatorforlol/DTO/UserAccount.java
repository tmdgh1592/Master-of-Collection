package com.app.buna.boxsimulatorforlol.DTO;

public class UserAccount {

    public String email;
    public String password;
    public String nickname;

    public UserAccount(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

}
