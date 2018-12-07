package es.upm.dit.cnvr.crudzk;

public class Bank {
	private ClientDB clientDB;
	public Bank() {
		this.clientDB = new ClientDB();
	}
	
	public void createClient(BankClient client) {
		this.clientDB.create(client);
	}
	
	public BankClient readClient(int account) {
		return (BankClient) this.clientDB.read(account);
	}
	
	public void updateClient(int account, int balance) {
		BankClient client = this.readClient(account);
		client.setBalance(balance);
		this.clientDB.update(client);
	}
	
	public void deleteClient(int account) {
		this.clientDB.delete(account);
	}
	
	public String toString() {
		String string = null;
		string = "          Bank Java     \n" +
				"------------------------\n";
		string = clientDB.toString();
		return string;
	}
}
