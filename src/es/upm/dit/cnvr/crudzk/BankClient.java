package es.upm.dit.cnvr.crudzk;

public class BankClient implements BankClientI {
	
	private String name;

	private int account;

	private int balance;

	public BankClient(){

	}

	public BankClient(String name, int account, int balance) {
		this.name = name;
		this.account = account;
		this.balance = balance;
	}

	@Override
	public String getName(){
		return this.name;
	}

	@Override
	public void setName(String name){
		 this.name = name;
	}
	
	@Override
	public int getAccount(){
		return this.account;
	}
	
	@Override	
	public void setAccount(int account){
		 this.account = account;
	}
	
	@Override
	public int getBalance(){
		return this.balance;
	}
	
	@Override	
	public void setBalance(int balance){
		 this.balance = balance;
	}
	
	@Override
	public String toString() {
		return "Nombre: " + this.name + "\n Cuenta: " + this.account + "\n Saldo: " + this.balance;
	}
}