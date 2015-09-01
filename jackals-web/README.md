##muse-crawler项目部署及运行文档

```
依赖：
redis
phantomjs
mongodb
muse-back
```
部署流程：
```js
1. 安装redis和mongodb（略）
2. 安装phantomjs
	绿色安装，复制内网172.18.10.24/usr/local/phantomjs-1.9.0-linux-x86_64至指定目录，完成
3. 启动phantomjs，依次执行
	phantomjs-1.9.0-linux-x86_64/bin/selenium.sh
	phantomjs-1.9.0-linux-x86_64/bin/phantomjs.sh
4.修改项目配置，打包muse-crawler
	mvn clean install -Dmaven.test.skip=true
	mvn -Ddeploy assembly:assembly -Dmaven.test.skip
5.启动muse-crawler
	(java -jar muse-crawler-1.0-SNAPSHOT-deploy.jar crawler false 2>&1 &)
6.七牛资源清理命令：
	java -jar muse-crawler-1.0-SNAPSHOT-deploy.jar qiniuCleaner
```

启动参数说明：
1.正常启动，不清理未执行的任务
    (java -jar muse-crawler-1.0-SNAPSHOT-deploy.jar crawler false 2>&1 &)
2.启动并清理任务
    (java -jar muse-crawler-1.0-SNAPSHOT-deploy.jar crawler true 2>&1 &)
3.清理七牛存存储数据
    java -jar muse-crawler-1.0-SNAPSHOT-deploy.jar qiniuCleaner