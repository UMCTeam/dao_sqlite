package com.chendaole.demo.bean;

import com.chendaole.dao_sqlite_annotations.Entity;
import com.chendaole.dao_sqlite_annotations.Id;

@Entity(table = "my_model")
public class MyBean {
    @Id
    int helloId;
    void hello() {}
}
