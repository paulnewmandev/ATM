package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 6/12/2018.
 */

public class UserModel implements Serializable {
    public int id;
    public String name;
    public String username;
    public String email;
    public String password;
    public int role_id;
    public String log;
    public String create_at;
    public String updated_at;

    public UserModel() {
    }
}
