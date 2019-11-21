package com.chendaole.demo.bean;

import com.chendaole.dao_sqlite_annotations.Entity;
import com.chendaole.dao_sqlite_annotations.Id;


@Entity(table = "user_model")
public class UserBean {
    String name = "xiaoming";

    @Id
    String id = "xxxxx";

    int bigSerial = 439;

    public UserBean() {}

    public UserBean(String name, String id, int serial) {
        this.name = name;
        this.id = id;
        this.bigSerial = serial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBigSerial() {
        return bigSerial;
    }

    public void setBigSerial(int bigSerial) {
        this.bigSerial = bigSerial;
    }
}
