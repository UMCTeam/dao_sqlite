package com.chendaole.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chendaole.demo.bean.UserBean;
import com.chendaole.dao_sqlite.SQLTrait;
import com.chendaole.demo.bean.UserBeanDao;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLTrait.getInstance().initialize(this);
    }


    public void onClickAdd(View view) {
        String id = String.valueOf(System.currentTimeMillis());
        UserBeanDao.addOne(new UserBean("老王", id, new Random().nextInt()));
    }

    public void onClickFind(View view) {
        List<UserBean>  models = UserBeanDao.findAll();
        Toast.makeText(this, "length:" + models.size(), Toast.LENGTH_SHORT)
                .show();
    }

    public void onClickDelete(View view) {
        UserBeanDao.deleteAll();
    }
}
