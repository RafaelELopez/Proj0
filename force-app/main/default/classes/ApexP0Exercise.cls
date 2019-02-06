/*
 * Author: Rafael E. Lopez M.
 * Last Modified: Feb 3, 2019
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
 *
 * Additionally in a separate apex class complete the following exercises.
 * Write the following methods in a class called ApexP0Exercises. 
 * Note: The method signatures are given.
 */
public class ApexP0Exercise{
    private enum Bracket {ROUND, CURLY, SQUARE}


    /*
     * 1. Return the nth Fibonacci number
     * Test Cases:
     * Input: 0     Output: 0
     * Input: 1     Output: 1
     * Input: 2     Output: 1
     * Input: 10    Output: 55
     */
    public static Integer nFibonacci( Integer n ){
        if(n==0){
            return 0;
        } else if (n==1) {
            return 1;
        }

        Integer f0 = 0;
        Integer f1 = 1;
        Integer fn = 0;

        for(Integer i = 1; i < n; i++){
            fn = f0 + f1;
            f0 = f1;
            f1 = fn;
        }

        return fn;
    }










    
    /*
     * 2. Sort an array of integers.
     * Test Cases:
     * Input: [2,4,5,1,3,1]     Output: [1,1,2,3,4,5]
     * Input: [5,4,3,2,1]   Output: [1,2,3,4,5]
     *
     * Don't use the sort() method... that would be lame.
     */
    /*
     * Heap sort without recursion (because Apex seems to not like that)
     * 1) heapify intList
     * 2) insert the root of the heap into the result list's 0th place
     * 3) repeat steps 1 and 2 until done
     */
    public static Integer[] sortArray( Integer[] intList ) {
        Integer[] result = new Integer[]{0};
        for(Integer i = (intList.size()) - 1; i >=0; i--){
            if(i != 0){
                heapify(intList);
            }
            result.add(0,intList.remove(0));
        }
        result.remove(result.size()-1);
        return result;
    }
    /*
     * Make a max heap out of the passed array.
     */
    private static void heapify( Integer[] intList ) {
        Integer iParent;

        for(Integer i = intList.size() - 1; i > 0; i--){
            // Determine a node's parent node.
            iParent = (i - ((math.mod(i,2) - 1))) / 2;
            if(intList[i] > intList[iParent]){
                swap(intList,iParent,i);
            }
        }
    }
    /*
     * Swap the values of array intList at indexs i1 and i2
     */
    private static void swap( Integer[] intList, Integer i1, Integer i2){
        //Integer[] result = intList;

        Integer current = intList[i1];
        intList[i1] = intList [i2];
        intList[i2] = current;
    }
    /*
     * Make a sublist of a list, going from indexes i1 (inclusive) to i2 (non-inclusive).
     */
    private static Integer[] subList( Integer[] intList, Integer i1, Integer i2){
        // System.debug('value of i1: ' + String.Valueof(i1));
        // System.debug('value of i2: ' + String.valueof(i2));

        Integer[] result = new Integer[]{};
        for (Integer i = 0; i < i2-i1; i++){
            // System.Debug('value of i is ' + String.valueof(i));
            // System.Debug('intList at i+i1 : ' + String.Valueof(intList[i+i1]));
            result.add(intList[i+ i1]);
            // System.Debug('Result array after adding: ');
            // System.Debug(result);
        }
        return result;
    }








    /*
     * 3. Return the factorial of n.
     * Test Cases:
     * Input: 0 Output: 1
     * Input: 1 Output: 1
     * Input: 3 Output: 6
     */
    // Not written to handle negative numbers.
    public static Integer nFactorial( Integer n) {
        Integer result = 1;
        if(n == 0){
            return 0;
        }
        for(Integer i = 2; i <= n; i++){
            result *= i;
        }
        return result;
    }











    /*
     * 4. Rotate left
     * Given an array, and an integer, n, rotate the values in array left n times and return array
     * Test Cases:
     * Input: [1,2,3,4,5], 1    Output: [2,3,4,5,1]
     * Input: [1,2,3,4,5], 6    Output: [2,3,4,5,1]
     * Input: [1,2,3,4,5], 3    Output: [4,5,1,2,3]
     */
    public static Integer[] rotateLeftNTimes( Integer[] argArray, Integer n) {
        n = Math.mod(n,argArray.size());
        // System.Debug('Value of n: ' + String.valueof(n));
        // Split the list into two subLists at the nth index and rejoin them the other way around.
        Integer[] leftList = subList(argArray,0,n);
        Integer[] rightList = subList(argArray,n,argArray.size());
        // System.Debug(leftList);
        // System.Debug(rightList);
        rightList.addAll(leftList);
        return rightList;
    }











    /*
     * 5. Balanced Brackets
     * A bracket is any one of the following: (, ), {, }, [, or ]
     * 
     * The following are balanced brackets:
     *    ( )
     *    ( ) ( )
     *    ( ( ) )
     *    ( { [ ] } )
     *
     * The following are NOT balanced brackets:
     *   (
     *   )
     *   ( ( )
     *   ( [ ) ]
     *
     * Return true if balanced
     * Return false if not balanced
     */
    /*
     * Add brackets to a stack, only removing them when the appropriate closing bracket appears.
     * If the stack isn't empty by the end of the string, or an unexpected bracket is encountered, return false.
     */
    public static Boolean bracketsAreBalanced( String s ) {
        // Map<String,Integer> bracketMap = new Map<String,Integer>();
        // bracketMap.put('(','('.charAt(0));
        // bracketMap.put('{','{'.charAt(0));
        // bracketMap.put('[','['.charAt(0));
        // bracketMap.put(')',')'.charAt(0));
        // bracketMap.put('}','}'.charAt(0));
        // bracketMap.put(']',']'.charAt(0));

        Bracket[] stack = new Bracket[]{};
        for (Integer i = 0; i < s.length(); i++){
            switch on (s.charAt(i)){
                // Tried various approaches, but only got these magic numbers to work with the switch statement.
                // Couldn't get the switch statements to work unless the only things in the when clauses were literals.s
                // The following is the numerical value for the '(' character.
                // when bracketMap.key('(') {
                when 40 {
                    stack.add(Bracket.ROUND);
                }
                // The following is the numerical value for the '{' character.
                // when bracketMap.key('{') {
                when 123 {
                    stack.add(Bracket.CURLY);
                }
                // The following is the numerical value for the '[' character.
                // when bracketMap.key('[') {
                when 91 {
                    stack.add(Bracket.SQUARE);
                }
                // The following is the numerical value for the ')' character.
                // when bracketMap.key(')') {
                when 41 {
                    if(stack.size() == 0 || stack.remove(stack.size()-1) != Bracket.ROUND){
                        return false;
                    }
                }
                // The following is the numerical value for the '}' character.
                // when bracketMap.key('}') {
                when 125 {
                    if(stack.size() == 0 || stack.remove(stack.size()-1) != Bracket.CURLY){
                        return false;
                    }
                }
                // The following is the numerical value for the ']' character.
                // when bracketMap.key(']') {
                when 93 {
                    if(stack.size() == 0 || stack.remove(stack.size()-1) != Bracket.SQUARE){
                        return false;
                    }
                }
            }
        }
        // The stack must have been emptied for the string to be valid.
        return stack.size() == 0;
    }










    
    /* You should NOT be submitting answers that you find online. You will be expected to be able to defend any of
     * these solutions without notice.
     */
    /*
     * 6. Create a method that retrieves a list of all accounts and updates those accounts.
     *  Create a custom field on the Account standard object called Size__c that will be a 
     *  picklist containing small/medium/large.
     *  Create a method that retrieves a list of all accounts. 
     *      - If an account has between 1-1000 employees then it is classified as small.
     *      - If an account has between 1001-10000 employees it is classified as medium.
     *      - If an account has more than 10000 employees it is classified as large.
     *  Update the Size__c field on each account to reflect the amount of employees in the Account.
     */
    public static void updateAccountSize() {
        List<Account> accList = [SELECT ID, NumberOfEmployees, Size__c FROM Account];
        System.debug(accList);

        for(Account acc: accList) {
            if((acc.NumberOfEmployees<=1000) && (acc.NumberOfEmployees>=1)) {
                acc.Size__c = 'small'; 
            } else if ((acc.NumberOfEmployees>=1001) && (acc.NumberOfEmployees<=10000)) {
                acc.Size__c = 'medium';
            } else if (acc.NumberOfEmployees>10000) {
                acc.Size__c = 'large';
            }
        }
        // update accList;
        try{
            // System.debug(accList);
            Database.update(accList,true);
        } catch(Exception e) {
            System.debug('Update did not complete successfully.');
        }
    }










    
    /* 
     * 7. Create a method that will find all leads that contain 'ca' in their fields. 
     * If a lead is located in California(CA), change their Lead Status field to 'Closed - Not 
     * Converted' and their description should display the message, "We are no longer doing 
     * business in California."
     */
    public static void updateCALeads( ) {
        List<Lead> leadList = [SELECT ID, State, Status, Description FROM Lead];
        for(Lead sObj: leadList){
            if(sobj.State == 'CA'){
                sObj.Status = 'Closed - Not Converted';
                sObj.Description = 'We are no longer doing business in California.';
            }
        }
        
        // update leadList
        try{
            // System.debug(leadList);
            Database.update(leadList,true);
        } catch(Exception e) {
            System.debug('Update did not complete successfully.');
        }
    }










    
    /*
     * 8. Create a method that will find all Opportunities which have already passed their Close Date 
     * and have a Stage field that is not 'Closed Won'. Since they have passed their Close Date and 
     * they are not marked as 'Closed Won', they should be marked as 'Closed Lost'.
     */
    public static void closePastDueOpportunities( ) {
        List<Opportunity> oppList = [SELECT ID, StageName
                                        FROM Opportunity
                                        WHERE CloseDate < TODAY AND StageName != 'Closed Won'];
        for(Opportunity opp: oppList){
            opp.StageName = 'Closed Lost';
        }

        try{
            // System.debug(oppList);
            Database.update(oppList,true);
        } catch(Exception e) {
            System.debug('Update did not complete successfully.');
        }
    }










    
    /*
     * Finally you should prepare a 5 minute presentation on your solution to the banking app, including an overview,
     * erd,
     * and demo.
     */

    /*
     * Testing method. Not used in any of the required assignment methods.
     */
    public static void scratch(){
        // test for assigned method 1
        Integer fibbo = 0;
        System.Debug(fibbo);
        System.Debug(nFibonacci(fibbo));

        fibbo = 1;
        System.Debug(fibbo);
        System.Debug(nFibonacci(fibbo));

        fibbo = 2;
        System.Debug(fibbo);
        System.Debug(nFibonacci(fibbo));

        fibbo = 3;
        System.Debug(fibbo);
        System.Debug(nFibonacci(fibbo));

        fibbo = 4;
        System.Debug(fibbo);
        System.Debug(nFibonacci(fibbo));

        fibbo = 5;
        System.Debug(fibbo);
        System.Debug(nFibonacci(fibbo));

        fibbo = 10;
        System.Debug(fibbo);
        System.Debug(nFibonacci(fibbo));



        // test for assigned method 2
        Integer[] sortable = new Integer[]{17,25,2,3,19,36,1,100,7};
        System.Debug('\n\n' + String.valueOf(sortable));
        System.Debug(sortArray(sortable));



        // test for assigned method 3
        System.Debug('\n\n' + String.valueOf(nFactorial(0)));
        System.Debug(nFactorial(1));
        System.Debug(nFactorial(3));
        System.Debug(nFactorial(10));
        System.Debug(nFactorial(11));



        // test for assigned method 4
        System.Debug('\n\n');
        System.Debug(rotateLeftNTimes(new Integer[]{1,2,3,4,5}, 1));
        System.Debug(rotateLeftNTimes(new Integer[]{1,2,3,4,5}, 6));
        System.Debug(rotateLeftNTimes(new Integer[]{1,2,3,4,5}, 3));



        // test for assigned method 5
        System.Debug('\n\n');
        System.Debug(bracketsAreBalanced('( )'));
        System.Debug(bracketsAreBalanced('( ) ( )'));
        System.Debug(bracketsAreBalanced('( ( ) )'));
        System.Debug(bracketsAreBalanced('( { [ ] } )'));
        System.Debug(bracketsAreBalanced('('));
        System.Debug(bracketsAreBalanced(')'));
        System.Debug(bracketsAreBalanced('( ( )'));
        System.Debug(bracketsAreBalanced('( [ ) ]'));
        // System.Debug();



        // test for assigned method 6
        System.Debug('\n\n');
        updateAccountSize();
        // System.Debug();



        // test for assigned method 7
        System.Debug('\n\n');
        updateCALeads();
        // System.Debug();



        // test for assigned method 8
        System.Debug('\n\n');
        closePastDueOpportunities();
        // System.Debug();
    }
}

