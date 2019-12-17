### Dao-Sqlite
> 采用 bean + model 形式， 开发者只需关注bean结构

- 支持SQL操作（均支持条件操作）
    - 删除
    - 查找
    - 添加
    - 更新


#### 初始化
在使用前必须进行数据库的初始化
```java

 SQLTrait.getInstance().initialize(this);
```

#### 注释
- 使用@Entity标记Bean类
- 使用@Id标记主键

```java
@Entity(table = "user_model")
public class UserBean extends SQLModel<UserBean> {
    @Id
    String id = "bf9875";
    String name = "xiaoming";
    int old = 18;
}
```

#### API
> UserBeanDao, 为自动生成代码
```java
//添加
 UserBeanDao.addOne(new UserBean("老王", id, "bf9875"));

//查找
UserBean> models = UserBeanDao.findById("bf9875");
List<UserBean>  models = UserBeanDao.findAll();
List<UserBean>  models = UserBeanDao.findByCondition("old = 18");

//删除
UserBeanDao().deleteAll();
UserModel().deleteById("bf9875");
UserModel().deleteByCondition("old = 1000");

//更新(不会更新主键)
UserBeanDao().updateById(new UserBean("老刘", id, "bf9875"))
UserBeanDao().updateByCondition(new UserBean("小王", id, "bf9875")， "old=18")
```
