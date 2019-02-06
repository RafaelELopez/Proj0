/*
 * Author: Rafael E. Lopez M.
 * Last Modified: Feb 5, 2019
 *
 * Project 0 – Custom Banking Application
 * We need to track customer accounts, see individual transactions, see any related financial details such as loan
 * applications they have made to the bank and if these were approved or denied. Accounts should come in single and
 * joint account types, and should require one or two related customers accordingly.
 * We should have a profile set up for the bank user, and security should be set up for the entire org, including
 * your custom objects. 
 *
 * In addition to the above you should have an Apex class which contains methods for the following:
 * • Check if an account is overdrawn and change the status of the account accordingly.
 * • Create a new transaction relating to the account.
 * • Update the account based on all related transactions to show current balance.
 */
public class Proj0{

    private static Account o_account {get;set;}







    /*
     * Check if an account is overdrawn and change the status of the account accordingly.
     */
    public static void checkOverdrawn(){
        if(o_account.Balance__c < 0){
            o_account.Overdrawn__c = true;
        }

        try{
            Database.update(o_account,true);
        } catch(Exception e) {
            System.debug('Update did not complete successfully.');
        }
    }
    public static void checkOverDrawn(Account acc) {
        o_account = acc;
        checkOverdrawn();
    }







    /*
     * Create a new transaction relating to the account.
     */
    public static void createTransaction(Decimal amt, String type, Account receiver){
        // Check that all arguments passed are valid
        if (amt <= 0){
            System.debug('A transaction\'s ammount must be a positive quantity.');
            return;
        }
        if (type != 'Withdrawal' && type != 'Deposit' && type != 'Transfer') {
            System.debug('Invalid Transaction Type');
            return;
        }
        if (type != 'Transfer' && receiver != null){
            System.debug('Transactions that are not of type Transfer cannot have a Transfer Receiver account.');
            return;
        }
        if (type == 'Transfer' && receiver == null){
            System.debug('Transfer type transactions must have a Transfer Receiver account.');
            return;
        }

        Transaction__c newTransaction = new Transaction__c();
        // populate the transaction's custom fields
        newTransaction.Amount__c = amt;
        newTransaction.IssuingAccount__c = o_account.Id;
        if (receiver != null) {
            newTransaction.TransferReceiver__c = receiver.Id;
        }
        newTransaction.Status__c = 'Pending';
        newTransaction.TransactionType__c = type;

        try{
            Database.insert(newTransaction,true);
        } catch(Exception e) {
            System.debug('Update did not complete successfully.');
        }
    }
    public static void createTransaction(Account acc, Decimal amt, String type) {
        o_account = acc;
        createTransaction(amt, type, null);
    }
    public static void createTransaction(Account acc, Decimal amt, String type, Account receiver) {
        o_account = acc;
        createTransaction(amt, type, receiver);
    }







    /*
     * Update the account based on all related transactions to show current balance.
     */
    public static void updateBalance(){
        // Get all transactions relating to the account
        List<Transaction__c> transList = o_account.Transactions__r;

        for(Transaction__c trans: transList){
            if(trans.Status__c == 'Pending'){
                switch on trans.TransactionType__c {
                    when 'Withdrawal'{
                        if(trans.Amount__c > o_account.Balance__c){
                            trans.Status__c = 'Declined';
                        } else {
                            o_account.Balance__c -= trans.Amount__c;
                        }
                    }
                    when 'Deposit'{
                        o_account.Balance__c += trans.Amount__c;
                    }
                    when 'Transfer'{
                        if(trans.Amount__c > o_account.Balance__c){
                            trans.Status__c = 'Declined';
                        } else {
                            o_account.Balance__c -= trans.Amount__c;
                            trans.TransferReceiver__r.Balance__c += trans.Amount__c;
                        }
                    }
                }
                trans.Status__c = 'Processed';
            }
        }

        // Don't forget to check if the account is overdrawn after all those transactions
        checkOverdrawn();

        try{
            Database.update(transList,true);
        } catch(Exception e) {
            System.debug('Update did not complete successfully.');
        }
    }
    public static void updateBalance(Account acc) {
        o_account = acc;
        updateBalance();
    }







    /*
     * Test the methods written for the project.
     */
    public static void runTests(){
        Boolean[] resultList = new List<Boolean>();
        Integer passes = 0;

        // checkOverdrawn()
        resultList.add(test_checkOverdrawn_positiveBalance());
        resultList.add(test_checkOverdrawn_negativeBalance());
        resultList.add(test_checkOverdrawn_zeroBalance());

        // createTransaction()
        resultList.add(test_createTransaction_badWithdrawalAmount());
        resultList.add(test_createTransaction_badWithdrawalReceiver());
        resultList.add(test_createTransaction_goodWithdrawal());

        resultList.add(test_createTransaction_badDeopsitAmount());
        resultList.add(test_createTransaction_badDepositReceiver());
        resultList.add(test_createTransaction_goodDeposit());

        resultList.add(test_createTransaction_badTransferAmount());
        resultList.add(test_createTransaction_badTransferReceiver());
        resultList.add(test_createTransaction_goodTransfer());

        // updateBalance()
        // resultList.add(test_updateBalance_goodWithdrawal());
        // resultList.add(test_updateBalance_overdrawingWithdrawal());
        // resultList.add(test_updateBalance_Deposit());
        // resultList.add(test_updateBalance_overdrawFixingDeposit());
        // resultList.add(test_updateBalance_goodTransfer());
        // resultList.add(test_updateBalance_overdrawingTransfer());
        // resultList.add(test_updateBalance_receivingTransfer());
        // resultList.add(test_updateBalance_receivingOverdrawFixingTransfer());

        for(Boolean result: resultList){
            passes += result ? 1 : 0;
        }
        System.debug(String.valueOf(passes) + ' tests out of ' + String.valueOf(resultList.size()) + 'passed.');
    }







    /*
     * Test that Account.Overdrawn__c is not set when balance is > 0
     */
    private static Boolean test_checkOverdrawn_positiveBalance(){
        // Arrange
        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account notOverDrawnAcc = new Account();
        notOverDrawnAcc.Name = 'notOverDrawnAcc';
        notOverDrawnAcc.PrimaryAccountOwner__c = cntct.Id;
        notOverDrawnAcc.Balance__c = 30.0;
        insert notOverDrawnAcc;

        // Act
        checkOverdrawn(notOverDrawnAcc);

        // Assert
        Account testAcc = [SELECT OverDrawn__c FROM Account WHERE Id = :notOverDrawnAcc.Id];
        Boolean result = testAcc.Overdrawn__c == false;
        System.Debug(String.valueOf(result) + ' -\tOverdrawn__c is not set when balance is > 0');

        // Finalize
        delete cntct;
        delete notOverDrawnAcc;

        return result;
    }

    /*
     * Test that Account.Overdrawn__c is set when balance is < 0
     */
    private static Boolean test_checkOverdrawn_negativeBalance(){

        // Arrange
        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account overDrawnAcc = new Account();
        overDrawnAcc.Name = 'overDrawnAcc';
        overDrawnAcc.PrimaryAccountOwner__c = cntct.Id;
        overDrawnAcc.Balance__c = -30.0;
        insert overDrawnAcc;

        // Act
        checkOverdrawn(overDrawnAcc);

        // Assert
        Account testAcc = [SELECT OverDrawn__c FROM Account WHERE Id = :overDrawnAcc.Id];
        Boolean result = testAcc.Overdrawn__c == true;
        System.Debug(String.valueOf(result) + ' -\tOverdrawn__c is set when balance is < 0');

        // Finalize
        delete cntct;
        delete overDrawnAcc;

        return result;
    }

    /*
     * Test that Account.Overdrawn__c is not set when balance is = 0
     */
    private static Boolean test_checkOverdrawn_zeroBalance(){
        // Arrange
        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account notOverDrawnAcc = new Account();
        notOverDrawnAcc.Name = 'notOverDrawnAcc';
        notOverDrawnAcc.PrimaryAccountOwner__c = cntct.Id;
        notOverDrawnAcc.Balance__c = 0.0;
        insert notOverDrawnAcc;

        // Act
        checkOverdrawn(notOverDrawnAcc);

        // Assert
        Account testAcc = [SELECT OverDrawn__c FROM Account WHERE Id = :notOverDrawnAcc.Id];
        Boolean result = testAcc.Overdrawn__c == false;
        System.Debug(String.valueOf(result) + ' -\tOverdrawn__c is not set when balance is = 0');

        // Finalize
        delete cntct;
        delete notOverDrawnAcc;

        return result;
    }








    /*
     * Given a non-positive amount, withdrawal transaction isn't created
     */
    private static Boolean test_createTransaction_badWithdrawalAmount(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, -50.0, 'Withdrawal');

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 0;
        System.Debug(String.valueOf(result) + ' -\tGiven a non-positive amount, withdrawal transaction isn\'t created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }
    /*
     * Given a receiver account, withdrawal transaction isn't created
     */
    private static Boolean test_createTransaction_badWithdrawalReceiver(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account receiver = new Account();
        receiver.Name = 'McReceiver';
        receiver.PrimaryAccountOwner__c = cntct.Id;
        receiver.Balance__c = 100.0;
        insert receiver;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, 50.0, 'Withdrawal', receiver);

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 0;
        System.Debug(String.valueOf(result) + ' -\tGiven a receiver account, withdrawal transaction isn\'t created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }
    /*
     * Given valid parameters, withdrawal transaction is created
     */
    private static Boolean test_createTransaction_goodWithdrawal(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, 50.0, 'Withdrawal');

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 1;
        System.Debug(String.valueOf(result) + ' -\tGiven valid parameters, withdrawal transaction is created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }

    /*
     * Given bad amount, deposit transaction isn't created
     */
    private static Boolean test_createTransaction_badDeopsitAmount(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, -50.0, 'Deposit');

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 0;
        System.Debug(String.valueOf(result) + ' -\tGiven bad amount, deposit transaction isn\'t created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }
    /*
     * Given a receiver account, deposit transaction isn't created
     */
    private static Boolean test_createTransaction_badDepositReceiver(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account receiver = new Account();
        receiver.Name = 'McReceiver';
        receiver.PrimaryAccountOwner__c = cntct.Id;
        receiver.Balance__c = 100.0;
        insert receiver;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, 50.0, 'Deposit', receiver);

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 0;
        System.Debug(String.valueOf(result) + ' -\tGiven a receiver account, deposit transaction isn\'t created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }
    /*
     * Given valid parameters, deposit transaction is created
     */
    private static Boolean test_createTransaction_goodDeposit(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, 50.0, 'Deposit');

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 1;
        System.Debug(String.valueOf(result) + ' -\tGiven valid parameters, deposit transaction is created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }

    /*
     * Given invalid amount, transfer transaction is not created
     */
    private static Boolean test_createTransaction_badTransferAmount(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account receiver = new Account();
        receiver.Name = 'McReceiver';
        receiver.PrimaryAccountOwner__c = cntct.Id;
        receiver.Balance__c = 100.0;
        insert receiver;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, -50.0, 'Transfer', receiver);

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 0;
        System.Debug(String.valueOf(result) + ' -\tGiven invalid amount, transfer transaction is not created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }
    /*
     * Not given a receiver, transfer transaction is not created
     */
    private static Boolean test_createTransaction_badTransferReceiver(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, 50.0, 'Transfer');

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 0;
        System.Debug(String.valueOf(result) + ' -\tNot given a receiver, transfer transaction is not created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }
    /*
     * Given valid paramenters, transfer transaction is created
     */
    private static Boolean test_createTransaction_goodTransfer(){
        // Arrange
        Boolean result = false;

        Contact cntct = new Contact();
        cntct.LastName = 'McSampleson';
        insert cntct;

        Account receiver = new Account();
        receiver.Name = 'McReceiver';
        receiver.PrimaryAccountOwner__c = cntct.Id;
        receiver.Balance__c = 100.0;
        insert receiver;

        Account sampleAcc = new Account();
        sampleAcc.Name = 'sampleAcc';
        sampleAcc.PrimaryAccountOwner__c = cntct.Id;
        sampleAcc.Balance__c = 100.0;
        insert sampleAcc;

        // Act
        createTransaction(sampleAcc, 50.0, 'Transfer', receiver);

        // Assert
        Account assertAcc = [SELECT Id, (SELECT Transaction__c.Id FROM Account.Transactions__r)
            FROM Account
            WHERE Id = :sampleAcc.Id];
        result = assertAcc.Transactions__r.size() == 1;
        System.Debug(String.valueOf(result) + ' -\tGiven valid paramenters, transfer transaction is created');

        // Finalize
        delete cntct;
        delete sampleAcc;

        return result;
    }








    
        /*
         * 
         */
        private static Boolean test_updateBalance_goodWithdrawal(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }
        /*
         *
         */
        private static Boolean test_updateBalance_overdrawingWithdrawal(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }
        /*
         *
         */
        private static Boolean test_updateBalance_Deposit(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }
        /*
         *
         */
        private static Boolean test_updateBalance_overdrawFixingDeposit(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }
        /*
         *
         */
        private static Boolean test_updateBalance_goodTransfer(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }
        /*
         *
         */
        private static Boolean test_updateBalance_overdrawingTransfer(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }
        /*
         *
         */
        private static Boolean test_updateBalance_receivingTransfer(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }
        /*
         *
         */
        private static Boolean test_updateBalance_receivingOverdrawFixingTransfer(){
            // Arrange
            Boolean result = false;

            // Act

            // Assert

            // Finalize

            return result;
        }  
}
































