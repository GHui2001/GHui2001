# 放置验证代码流程

下载java案例
将com下的包全部粘贴过来

>         <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-crypto</artifactId>
            <version>5.8.16</version>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-http</artifactId>
            <version>5.8.16</version>
        </dependency>
       <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.83</version>
        </dependency>
放在pom依赖中

> 将NetworkUtils中main 的配置文件粘贴到你的项目入口处


如下代码就是发送一个网络请求的代码
```java
HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> encryptMap = goEncrypt(map, "11111111111");
        String post = HttpUtil.post("http://127.0.0.1:8089/api/single/login", encryptMap);
        System.out.println(post);
```

