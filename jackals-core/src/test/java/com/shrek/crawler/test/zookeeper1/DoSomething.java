package com.shrek.crawler.test.zookeeper1;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jackals.utils.LogbackConfigurer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 *
 * @description 自定义持久性的zookeeper watcher
 * @author zhangchaoyang
 * @date 2014-6-22
 */
public class DoSomething {

    private static final int TIMEOUT = 6000;
    private static final String znode = "/globalconfnode";
    private static String globalConfData = "";




    public static void main(String[] args) {
        try {
            new LogbackConfigurer();
            String connection = "localhost:" + 3181;
            ZooKeeper zkp = new ZooKeeper(connection,
                    TIMEOUT,
                    new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {

                        }
                    });
            zkp.exists(znode, true);
            zkp.create(znode, "config_value".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);

//            Thread.sleep(500);// 修改节点后必须sleep，等待watcher回调完成
            System.out.println(globalConfData);

            for (int i = 0; i < 100; i++) {
                zkp.setData(znode, ("config_value" + i).getBytes(), -1);
//                Thread.sleep(500);// 修改节点后必须sleep，等待watcher回调完成
                System.out.println(globalConfData);
            }
//            TimeUnit.SECONDS.sleep(1000000);
//            zkp.close();// EPHEMERAL节点会被删除，但Session并不会马上失效(只不过ConnectionLoss了)，所以还是会触发watcher

//            try {
                // 此时Session已失效
//                zkp.exists(znode, false);
//            } catch (KeeperException e) {
//                if (e instanceof SessionExpiredException)
//                    System.out.println("Session已失效111111。");
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}