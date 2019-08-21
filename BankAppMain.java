package AssignmentSession8;

class BankAccount {

	volatile double balance;
	double unBalance;
	
	public BankAccount(double balance) {
		this.balance = balance;
		this.unBalance = balance;
	}
	
	synchronized void withdraw(double amt) {
		if (amt > 0 && balance >= amt) {
			balance -= amt;
		}
	}
	
	synchronized void deposit(double amt) {
		if (amt > 0) {
			this.balance += amt;
		}
	}
	
	synchronized double getBalance() {
		return this.balance;
	}
	
	void unsyncDeposit(double amt) {
		if (amt > 0) {
			this.unBalance += amt;
		}
	}
	
	void unsyncWithdraw (double amt) {
		if (amt > 0 && balance >= amt) {
			unBalance -= amt;
		}
	}
	
	double getUnBalance() {
		return this.unBalance;
	}
}

class SyncTransaction extends Thread{
	
	volatile BankAccount acc;
	int transactionType;
	double amt;
	
	public SyncTransaction(BankAccount acc, int transactionType, double amt) {
		this.acc = acc;
		this.transactionType = transactionType;
		this.amt = amt;
	}
	
	@Override
	public void run() {
		synchronized (this) {
			if (transactionType == 0) {
				deposit(amt);
			} else {
				withdraw(amt);
			}
			notify();
		}
		
	}
	
	void withdraw (double amt) {
		acc.withdraw(amt);
	}
	
	void deposit  (double amt) {
		acc.deposit(amt);
	}
	
}

class UnsyncTransaction extends Thread{
	
	BankAccount acc;
	int transactionType;
	double amt;
	
	public UnsyncTransaction(BankAccount acc, int transactionType, double amt) {
		this.acc = acc;
		this.transactionType = transactionType;
		this.amt = amt;
	}
	
	@Override
	public void run() {
		if (transactionType == 0) {
			deposit(amt);
		} else {
			withdraw(amt);
		}
	}
	
	void withdraw (double amt) {
		acc.unsyncWithdraw(amt);
	}
	
	void deposit  (double amt) {
		acc.unsyncDeposit(amt);
	}
}


public class BankAppMain {
	public static void main(String[] args) {
		
		BankAccount ba = new BankAccount(5000);
		Thread syn = null;
		int loop = 10000;
		
		for (int i = 0; i< loop; i++) {
			syn = new SyncTransaction(ba, 0, 100);
			syn.start();
			syn = new SyncTransaction(ba, 1, 50);
			syn.start();
		}
		
		synchronized (syn) {
			try {
				syn.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("This app deposits 100 and withdraws 50 " + loop + " times. \nThe starting balance is 5000");
		System.out.println("Calculated (Estimated) Result : " + (5000 + (100-50)*loop));
		System.out.println("The synced balance:  " + ba.getBalance());
		for(int i = 0; i < loop; i++) {
			syn = new UnsyncTransaction(ba, 0, 100);
			syn.start();
			syn = new UnsyncTransaction(ba, 1, 50);
			syn.start();
		}
		
		
		System.out.println("The unsynced balance:  " + ba.getUnBalance());
		System.out.println();
		System.out.println("The synced balance should be more accurate. ");
	}
}
