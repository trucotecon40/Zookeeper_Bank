package es.upm.dit.cnvr.crudzk;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class zkMembers implements Watcher{
    private static final int SESSION_TIMEOUT = 5000;

    private static String rootMembers = "/members";
    private static String aMember = "/member-";
    private String myId;
    private String rol;

    // This is static. A list of zookeeper can be provided for decide where to connect
    static String[] hosts = {"127.0.0.1:2181", "127.0.0.1:2181", "127.0.0.1:2181"};

    private ZooKeeper zk;

    public zkMembers (String selectedHost) {

        // Select a random zookeeper server
        Random rand = new Random();
        int i = rand.nextInt(hosts.length);

        // Create a session and wait until it is created.
        // When is created, the watcher is notified
        try {
            if (zk == null) {
                zk = new ZooKeeper(selectedHost, SESSION_TIMEOUT, cWatcher);
                try {
                    // Wait for creating the session. Use the object lock
                    wait();
                    //zk.exists("/",false);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            System.out.println("Error");
        }

        // Add the process to the members in zookeeper

        if (zk != null) {
            // Create a folder for members and include this process/server
            try {
                // Create a folder, if it is not created
                String response = new String();
                Stat s = zk.exists(rootMembers, watcherMember); //this);
                if (s == null) {
                    // Created the znode, if it is not created.
                    response = zk.create(rootMembers, new byte[0],
                            Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    System.out.println(response);
                }

                // Create a znode for registering as member and get my id
                myId = zk.create(rootMembers + aMember, new byte[0],
                        Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

                myId = myId.replace(rootMembers + "/", "");

                List<String> list = zk.getChildren(rootMembers, watcherMember, s); //this, s);
                System.out.println("Created znode nember id:"+ myId );
                printListMembers(list);
                selectLeader(list);
            } catch (KeeperException e) {
                System.out.println("The session with Zookeeper failes. Closing");
                return;
            } catch (InterruptedException e) {
                System.out.println("InterruptedException raised");
            }

        }
    }

    // Notified when the session is created
    private Watcher cWatcher = new Watcher() {
        public void process (WatchedEvent e) {
            System.out.println("Created session");
            System.out.println(e.toString());
            notify();
        }
    };

    // Notified when the number of children in /member is updated
    private Watcher  watcherMember = new Watcher() {
        public void process(WatchedEvent event) {
            System.out.println("------------------Watcher Member------------------\n");
            try {
                System.out.println("        Update!!");
                List<String> list = zk.getChildren(rootMembers,  watcherMember); //this);
                printListMembers(list);
                selectLeader(list);
            } catch (Exception e) {
                System.out.println("Exception: wacherMember");
            }
        }
    };

    @Override
    public void process(WatchedEvent event) {
        try {
            System.out.println("Unexpected invocated this method. Process of the object");
            List<String> list = zk.getChildren(rootMembers, watcherMember); //this);
            printListMembers(list);
            selectLeader(list);
        } catch (Exception e) {
            System.out.println("Unexpected exception. Process of the object");
        }
    }

    private void printListMembers (List<String> list) {
        System.out.println("Remaining # members:" + list.size());
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            String string = (String) iterator.next();
            System.out.print(string + ", ");
        }
        System.out.println();
    }

    private void selectLeader (List<String> list) {
        String leader = list.get(0);
        int min = Integer.parseInt(list.get(0).split("-")[1]);
        for (int i = 0; i < list.size(); i++) {
            if (Integer.parseInt(list.get(i).split("-")[1]) < min) {
                leader = list.get(i);
            }
        }

        String rol = "follower";
        if (this.myId.equals(leader)) {
            rol = "leader";
            System.out.println("I'm "+ leader + " and I'm the " + rol);
            this.rol = rol;
            return;
        }
        this.rol = rol;
        System.out.print("I'm " + rol + " and ");
        System.out.println("the leader is: " +  leader);
    }

    public static void main(String[] args) {
        zkMembers zkList[] = new zkMembers[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            zkList[i] = new zkMembers(hosts[i]);
        }
        //try {
        //    Thread.sleep(300000);
        //} catch (Exception e) {
        //
        //}
    }
}