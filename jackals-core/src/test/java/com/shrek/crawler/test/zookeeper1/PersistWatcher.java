package com.shrek.crawler.test.zookeeper1;

import java.io.IOException;

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
public class PersistWatcher {

    private static final int TIMEOUT = 6000;
    private static final String znode = "/globalconfnode";
    private static String globalConfData = "";

    private static Watcher getConnectWatcher() {
        return new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("getConnectWatcher------------------------------------" + event);
                if (event.getType().equals(EventType.None)) {
                    System.out.println("连接状态发生变化。");
                }
            }
        };
    }

    private static Watcher getExistsWatcher(final ZooKeeper zkp) {
        return new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("------------------------------------" + event);

                try {
                    if (event.getType().equals(EventType.NodeDataChanged)
                            || event.getType().equals(EventType.NodeCreated)) {
                        // 节点被创建或修改时更新缓存中的值
                        Stat stat = zkp.exists(znode, this);// 再次注册监听
//                        String data = new String(
//                                zkp.getData(znode, false, stat));
//                        globalConfData = data;
                    } else if (event.getType().equals(EventType.NodeDeleted)) {
                        // 节点被删除时报警
                        System.out
                                .println("global configuration node have been deleted!");
                        try {
                            // 再次注册监听
                            zkp.exists(znode, this);
                        } catch (KeeperException e) {
                            if (e instanceof ConnectionLossException) {
                                System.out.println("连接已断开。");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static void main(String[] args) {
        try {
            new LogbackConfigurer();
            String connection = "localhost:" + 3181;
            ZooKeeper zkp = new ZooKeeper(connection,
                    TIMEOUT, 
                    getConnectWatcher());
            zkp.exists(znode, getExistsWatcher(zkp));
            zkp.create(znode, "config_value".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);

            Thread.sleep(500);// 修改节点后必须sleep，等待watcher回调完成
            System.out.println(globalConfData);

            for (int i = 0; i < 4; i++) {
                zkp.setData(znode, ("config_value" + i).getBytes(), -1);
                Thread.sleep(500);// 修改节点后必须sleep，等待watcher回调完成
                System.out.println(globalConfData);
            }

            zkp.close();// EPHEMERAL节点会被删除，但Session并不会马上失效(只不过ConnectionLoss了)，所以还是会触发watcher

            try {
                // 此时Session已失效
                zkp.exists(znode, false);
            } catch (KeeperException e) {
                if (e instanceof SessionExpiredException)
                    System.out.println("Session已失效。");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}