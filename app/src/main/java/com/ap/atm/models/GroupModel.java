package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 2/12/2018.
 */

public class GroupModel implements Serializable {
    public int id;
    public String name;
    public int status;
    public String update_at;

    public enum fields {id, name, status, update_at}

    public GroupModel() {
    }
}
