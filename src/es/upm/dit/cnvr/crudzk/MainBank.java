package es.upm.dit.cnvr.crudzk;

import java.io.IOException;
import java.util.Scanner;

public class MainBank {
	public MainBank() {

	}

	public void initClients(Bank bank) throws IOException{
		bank.createClient(new BankClient("Angel Alarcón", 1, 100));

		bank.createClient(new BankClient("Bernardo Bueno", 2, 200));

		bank.createClient(new BankClient("Carlos Cepeda", 3, 300));

		bank.createClient(new BankClient("Daniel Díaz", 4, 400));

		bank.createClient(new BankClient("Eugenio Escobar", 5, 500));

		bank.createClient(new BankClient("Fernando Ferrero", 6, 600));
	}

	public BankClient readClient(Scanner sc) {
		int account = 0;
		String name = null;
		int balance = 0;

		System.out.print(">>> Enter account number (int) = ");
		if (sc.hasNextInt()) {
			account = sc.nextInt();
		} else {
			System.out.println("The provided text provided is not an integer");
			sc.next();
			return null;
		}

		System.out.print(">>> Enter name (String) = ");
		name = sc.next();

		System.out.print(">>> Enter balance (int) = ");
		if (sc.hasNextInt()) {
			balance = sc.nextInt();
		} else {
			System.out.println("The provised text provided is not an integer");
			sc.next();
			return null;
		}
		return new BankClient(name, account, balance);
	}

	public static void main(String[] args) {

		boolean correct = false;
		int menuKey = 0;
		boolean exit = false;
		Scanner sc = new Scanner(System.in);
		int accNumber = 0;
		int balance = 0;
		BankClient client = null;

		Bank bank = new Bank();
		MainBank mainBank = new MainBank();

		// System. out .println(">>> Enter opn cliente.: 1) Start bank");
		// sc.next();
		if (bank.isLeader()) {
			try {
				mainBank.initClients(bank);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
		}else {
			System.out.println("No soy líder por lo que espero que me la mande el líder");
		}
		while (!exit) {
			try {
				correct = false;
				menuKey = 0;
				while (!correct) {
					System.out.println(
							">>> Enter opn cliente.: 1) Create. 2) Read. 3) Update. 4) Delete. 5) BankDB. 6) Exit");
					if (sc.hasNextInt()) {
						menuKey = sc.nextInt();
						correct = true;
					} else {
						sc.next();
						System.out.println("The provised text provided is not an integer");
					}
				}

				switch (menuKey) {
				case 1: // Create client
					bank.createClient(mainBank.readClient(sc));
					break;
				case 2: // Read client
					System.out.print(">>> Enter account number (int) = ");
					if (sc.hasNextInt()) {
						accNumber = sc.nextInt();
						client = bank.readClient(accNumber);
						System.out.println(client);
					} else {
						System.out.println("The provised text provided is not an integer");
						sc.next();
					}
					break;
				case 3: // Update client
					System.out.print(">>> Enter account number (int) = ");
					if (sc.hasNextInt()) {
						accNumber = sc.nextInt();
					} else {
						System.out.println("The provised text provided is not an integer");
						sc.next();
					}
					System.out.print(">>> Enter balance (int) = ");
					if (sc.hasNextInt()) {
						balance = sc.nextInt();
					} else {
						System.out.println("The provised text provided is not an integer");
						sc.next();
					}
					bank.updateClient(accNumber, balance);
					break;
				case 4: // Delete client
					System.out.print(">>> Enter account number (int) = ");
					if (sc.hasNextInt()) {
						accNumber = sc.nextInt();
						bank.deleteClient(accNumber);
					} else {
						System.out.println("The provised text provided is not an integer");
						sc.next();
					}
					break;
				case 5:
					String aux = bank.toString();
					System.out.println(aux);
					break;
				case 6:
					exit = true;
				default:
					break;
				}
			} catch (Exception e) {
				System.out.println("Exception at Main. Error read data");
			}
		}
		sc.close();
	}
}
