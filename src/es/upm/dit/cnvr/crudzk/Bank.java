package es.upm.dit.cnvr.crudzk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Bank {
	private ClientDB clientDB;
	private static final int SESSION_TIMEOUT = 5000;

	private final int PUERTO = 1234;
	private final String HOST = "localhost";
	private Socket cs;
	private ObjectOutputStream salida;
	private ServerSocket ss;

	private static String createNode = "/create";
	private static String updateNode = "/update";
	private static String deleteNode = "/delete";

	private static String rootMembers = "/members";
	private static String aMember = "/member-";
	private String myId;
	private Boolean isLeader = false;
	// This is static. A list of zookeeper can be provided for decide where to
	// connect
	String host = "127.0.0.1:2181";
	private List<String> members;

	private ZooKeeper zk;

	public Bank() {
		this.clientDB = new ClientDB();
		this.zkLeaderSelector();
	}

	public boolean isLeader() {
		return this.isLeader;
	}

	public void createClient(BankClient client) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(client);
		try {
			Stat s = zk.exists(createNode, null);
			zk.setData(createNode, out.toByteArray(), s.getVersion());
			this.clientDB.create(client);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}		
	}

	public BankClient readClient(int account) {
		return (BankClient) this.clientDB.read(account);
	}

	public void updateClient(int account, int balance) throws IOException{
		BankClient client = this.readClient(account);
		client.setBalance(balance);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(client);
		try {
			Stat s = zk.exists(updateNode, null);
			zk.setData(updateNode, out.toByteArray(), s.getVersion());
			this.clientDB.update(client);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteClient(int account) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(account);
		try {
			Stat s = zk.exists(deleteNode, null);
			zk.setData(deleteNode, b.array(), s.getVersion());
			this.clientDB.delete(account);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
	}

	public String toString() {
		String string = null;
		string = "          Bank Java     \n" + "------------------------\n";
		string = clientDB.toString();
		return string;
	}

	private void zkLeaderSelector() {

		// Create a session and wait until it is created.
		// When is created, the watcher is notified
		try {
			if (zk == null) {
				zk = new ZooKeeper(this.host, SESSION_TIMEOUT, cWatcher);
				try {
					// Wait for creating the session. Use the object lock
					wait();
					// zk.exists("/",false);
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
				Stat s = zk.exists(rootMembers, watcherMember); // this);
				if (s == null) {
					// Create the znode, if it is not created.
					response = zk.create(rootMembers, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					System.out.println(response);
				}

				// Create a znode for registering as member and get my id
				myId = zk.create(rootMembers + aMember, new byte[0], Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL_SEQUENTIAL);

				myId = myId.replace(rootMembers + "/", "");

				this.members = zk.getChildren(rootMembers, watcherMember, s); // this, s);
				System.out.println("Created znode nember id:" + myId);
				printListMembers(members);
				selectLeader(members);
				if (!this.isLeader) {
					try {
						this.ss = new ServerSocket(PUERTO);
						this.cs = ss.accept();
						ObjectInputStream entrada = new ObjectInputStream(cs.getInputStream());
						this.clientDB = (ClientDB) entrada.readObject();
						ss.close();
						cs.close();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}

				Stat s2 = zk.exists(createNode, null);
				if (s2 == null) {
					zk.create(createNode, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
				zk.exists(createNode, createWatcher);
				
				Stat s3 = zk.exists(updateNode, null);
				if (s3 == null) {
					zk.create(updateNode, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
				zk.exists(updateNode, updateWatcher);
				
				Stat s4 = zk.exists(deleteNode, null);
				if (s4 == null) {
					zk.create(deleteNode, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
				zk.exists(deleteNode, deleteWatcher);

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
		public void process(WatchedEvent e) {
			System.out.println("Created session");
			System.out.println(e.toString());
			notify();
		}
	};

	// Notified when the number of children in /member is updated
	private Watcher watcherMember = new Watcher() {
		public void process(WatchedEvent event) {
			System.out.println("------------------Watcher Member------------------\n");
			try {
				System.out.println("        Members Update!!");
				List<String> list = zk.getChildren(rootMembers, watcherMember); // this);
				if (isLeader() && list.size() > members.size()) {
					System.out.println("Tengo que mandar la base de datos!");
					Thread.sleep(1000);
					sendDB();
				}
				members = list;
				printListMembers(members);
				selectLeader(members);
			} catch (Exception e) {
				System.out.println("Exception: wacherMember");
			}
		}
	};

	private Watcher createWatcher = new Watcher() {
		public void process(WatchedEvent event) {
			System.out.println("------------------Create Watcher------------------\n");
			try {
				Stat s = zk.exists(createNode, null);
				byte[] data = zk.getData(createNode, createWatcher, s);
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				BankClient client = (BankClient) is.readObject();
				clientDB.create(client);
			} catch (Exception e) {
				System.out.println("Exception: createWatcher");
			}
		}
	};
	
	private Watcher updateWatcher = new Watcher() {
		public void process(WatchedEvent event) {
			System.out.println("------------------Update Watcher------------------\n");
			try {
				Stat s = zk.exists(updateNode, null);
				byte[] data = zk.getData(updateNode, updateWatcher, s);
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				BankClient client = (BankClient) is.readObject();
				clientDB.update(client);
			} catch (Exception e) {
				System.out.println("Exception: updateWatcher");
			}
		}
	};
	
	private Watcher deleteWatcher = new Watcher() {
		public void process(WatchedEvent event) {
			System.out.println("------------------Delete Watcher------------------\n");
			try {
				Stat s = zk.exists(deleteNode, null);
				byte[] data = zk.getData(deleteNode, deleteWatcher, s);
				ByteBuffer buffer = ByteBuffer.wrap(data);
				int account = buffer.getInt();
				clientDB.delete(account);
			} catch (Exception e) {
				System.out.println("Exception: deleteWatcher");
			}
		}
	};

	public void process(WatchedEvent event) {
		try {
			System.out.println("Unexpected invocated this method. Process of the object");
			this.members = zk.getChildren(rootMembers, watcherMember); // this);
			printListMembers(this.members);
			selectLeader(this.members);
		} catch (Exception e) {
			System.out.println("Unexpected exception. Process of the object");
		}
	}

	private void printListMembers(List<String> list) {
		System.out.println("Remaining # members:" + list.size());
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.print(string + ", ");
		}
		System.out.println();
	}

	private void selectLeader(List<String> list) {
		String leader = list.get(0);
		int min = Integer.parseInt(list.get(0).split("-")[1]);
		for (int i = 0; i < list.size(); i++) {
			if (Integer.parseInt(list.get(i).split("-")[1]) < min) {
				leader = list.get(i);
			}
		}
		this.isLeader = false;
		if (this.myId.equals(leader)) {
			this.isLeader = true;
			System.out.println("I'm " + leader + " and I'm the leader");
			return;
		}
		System.out.print("I'm not the leader, ");
		System.out.println("the leader is: " + leader);
	}

	private void sendDB() throws IOException {
		this.cs = new Socket(HOST, PUERTO);
		salida = new ObjectOutputStream(cs.getOutputStream());
		salida.writeObject(this.clientDB);
		cs.close();
	}
}
