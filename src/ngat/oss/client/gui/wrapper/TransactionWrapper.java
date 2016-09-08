/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

public class TransactionWrapper {

    public static final int INCREMENT = 1;
    public static final int DECREMENT = 2;
    
    public static final int ALLOCATED_BALANCE_TYPE = 0;
    public static final int CONSUMED_BALANCE_TYPE  = 1;

    long accountId;
    int transactionType;    //one of TransactionWrapper.INCREMENT | DECREMENT
    int accountColumn;    // one of TransactionWrapper.ALLOCATED | CONSUMED
    double amount;
    String comment, clientRef;

    public TransactionWrapper(long accountId, int transactionType, int accountColumn, double amount, String comment, String clientRef) {
        this.accountId = accountId;
        this.transactionType = transactionType; //inc / dec
        this.accountColumn = accountColumn; //alloc / cons
        this.amount = amount;
        this.comment = comment;
        this.clientRef = clientRef;
    }

    public long getAccountId() {
        return accountId;
    }

    public int getAccountColumn() {
        return accountColumn;
    }

    public double getAmount() {
        return amount;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public String getTransactionTypeAsString() {
        switch (transactionType) {
            case TransactionWrapper.INCREMENT: 
                return "INCREMENT";
            case TransactionWrapper.DECREMENT: 
                return "DECREMENT";
        }
        return "UNKNOWN";
    }
    
    public String getAccountColumnAsString() {
        switch (accountColumn) {
            case TransactionWrapper.ALLOCATED_BALANCE_TYPE: 
                return "ALLOCATED";
            case TransactionWrapper.CONSUMED_BALANCE_TYPE: 
                return "CONSUMED";
        }
        return "UNKNOWN";
    }

    public String getClientRef() {
        return clientRef;
    }

    public String getComment() {
        return comment;
    }
    
    public String toString() {
        String s = this.getClass().getName() + "[";
        s += "accountId=" + accountId + ", ";
        s += "transactionType=" + getTransactionTypeAsString() + ", ";
        s += "accountColumn=" + getAccountColumnAsString() + ", ";
        s += "amount=" + amount + "]";
        return s;
    }
}
