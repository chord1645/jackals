package net.xulingbo.zookeeper;

import org.apache.zookeeper.server.ZooKeeperServerMain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TestMainServer
 * <p/>
 * Author By: junshan
 * Created Date: 2010-9-3 16:17:21
 */
public class TestMainServer extends ZooKeeperServerMain {
    public static final int CLIENT_PORT = 3181;

    public static class MainThread extends Thread {
        final File confFile;
        final TestMainServer main;

        public MainThread(int clientPort) throws IOException {
            super("Standalone server with clientPort:" + clientPort);
            File tmpDir = new File("D:\\tmp");
            confFile = new File(tmpDir, "zoo.cfg");

            FileWriter fwriter = new FileWriter(confFile);
            fwriter.write("tickTime=1000\n");
            fwriter.write("initLimit=10\n");
            fwriter.write("syncLimit=5\n");

            File dataDir = new File(tmpDir, "data");
            String df = org.apache.commons.lang.StringUtils.replace(dataDir.toString(), "\\", "/");
            fwriter.write("dataDir=" + df + "\n");

            fwriter.write("clientPort=" + clientPort + "\n");
            fwriter.flush();
            fwriter.close();

            main = new TestMainServer();
        }

        public void run() {
            String args[] = new String[1];
            args[0] = confFile.toString();
            try {
                main.initializeAndRun(args);
            } catch (Exception e) {

            }
        }
    }

    public static void start() {

        MainThread main = null;
        try {
            main = new MainThread(CLIENT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        main.start();
    }

    public static void main(String[] args) {
        File[] f = new File("D:\\tmp\\data\\version-2").listFiles();
        for (File file : f) {
            file.delete();
        }
        TestMainServer.start();
    }
}
