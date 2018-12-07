package es.upm.dit.cnvr.crudzk;

public interface BankClientI {
	int getAccount();
	void setAccount(int account);
	String getName();
	void setName(String name);
	int getBalance();
	void setBalance(int balance);
	String toString();
}
