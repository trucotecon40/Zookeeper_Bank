package es.upm.dit.cnvr.crudzk;

public class ClientDB implements ClientDBI<BankClientI> {
	
	private java.util.HashMap <Integer, BankClientI> clientDB;
	
	public ClientDB (ClientDB clientDB) {
		this.clientDB = clientDB.getClientDB();
	}
	
	public ClientDB() {
		this.clientDB = new java.util.HashMap <Integer, BankClientI>();
	}
	
	public java.util.HashMap <Integer, BankClientI> getClientDB() {
		return this.clientDB;
	}
	
	@Override
	public ServiceStatus create(BankClientI client) {
		if (this.clientDB.containsKey(client.getAccount())) {
			return ServiceStatus.CLIENT_EXISTED;
		}else {
			this.clientDB.put(client.getAccount(), client);
			return ServiceStatus.OK;
		}
	}

	@Override
	public ServiceStatus update(BankClientI client) {
		if (this.clientDB.containsKey(client.getAccount())) {
			this.clientDB.put(client.getAccount(), client);
			return ServiceStatus.OK;
		} else {
			return ServiceStatus.INFORMATION_INVALID;
		}
	}

	@Override
	public BankClientI read(String name) {
		for(BankClientI clienti: this.clientDB.values()) {
			if (clienti.getName().equals(name)) {
				return clienti;
			}
		}
		return null;
	}

	@Override
	public BankClientI read(int account) {
		if (this.clientDB.containsKey(account)) {
			return this.clientDB.get(account);
		} else {
			return null;
		}
	}

	@Override
	public ServiceStatus delete(int account) {
		if (this.clientDB.containsKey(account)) {
			this.clientDB.remove(account);
			return ServiceStatus.OK;
		}
		return ServiceStatus.INFORMATION_INVALID;
	}
	
	public String toString() {
		String aux = new String();

		for (java.util.HashMap.Entry <Integer, BankClientI>  entry : this.clientDB.entrySet()) {
			aux = aux + entry.getValue().toString() + "\n";
		}
		return aux;
	}

}
