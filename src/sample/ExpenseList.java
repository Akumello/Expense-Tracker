package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;

public class ExpenseList
{
    // The singleton of expense list
    private static ExpenseList expenseList = null;

    // list is the main underlying data structure for expense list
    private static ObservableList<Expense> list = null;
    // filteredList is a subset of list with only the Expenses the user is interested in
    private static FilteredList<Expense> filteredList = null;
    private boolean isFiltered;

    private ExpenseList()
    {
        list = FXCollections.observableArrayList();
        filteredList = null;
        isFiltered = false;
    }

    // There can be only one active expense list in the program
    // This will be implemented as a singleton
    public static ExpenseList getExpenseList()
    {
        if(expenseList == null)
            expenseList = new ExpenseList();

        return expenseList;
    }

    public ObservableList<Expense> getList() {
        return list;
    }

    public ObservableList<Expense> getFilteredList() {
        return filteredList;
    }


    public int getSize() {
        if(isFiltered)
            return filteredList.size();
        else
            return list.size();
    }

    public boolean addExpense(Expense e) {
        return list.add(e);
    }

    public Expense getExpense(int index) {
        return this.getList().get(index);
    }

    public boolean removeExpense(Expense e) {
        return list.remove(e);
    }

    public void clear()
    {
        if(list != null)
            list.clear();
        if(filteredList != null) {
            filteredList = null;
            isFiltered = false;
        }
    }

    public void clearFilter()
    {
        filteredList = null;
        isFiltered = false;
    }

    @Override
    public String toString() {
        String retS = "{ ";
        for (int i = 0; i < this.getSize(); i++) {
            retS += "" + this.getList().get(i).toString();
            if (i != this.getSize() - 1) {
                retS += ", ";
            }
        }
        retS += " }";
        return retS;
    }

    public void filterByName(String name) {
        filteredList = new FilteredList<>(list, p -> true);
        filteredList.setPredicate(expense -> {
            if(expense.getName().contains(name))
                return true;
            else
                return false;
        });
        isFiltered = true;
    }

    public void filterByCategory(String category) {
        filteredList = new FilteredList<>(list, p -> true);
        filteredList.setPredicate(expense -> {
            if(expense.getCategory().equals(category))
                return true;
            else
                return false;
        });
        isFiltered = true;
    }


    public void filterByDate(Date start, Date end) {
        filteredList = new FilteredList<>(list, p -> true);
        filteredList.setPredicate(expense -> {
            if(expense.getDate().after(start) && expense.getDate().before(end))
                return true;
            else
                return false;
        });
        isFiltered = true;

        /*
        filteredList.clear();
        for (int i = 0; i < this.getSize(); i++) {
            if (this.getExpense(i).getDate().after(start) && this.getExpense(i).getDate().before(end)) {
                filteredList.add(this.getList().get(i));
            }
        }*/
    }

    public void filterByCost(double min, double max) {
        filteredList = new FilteredList<>(list, p -> true);
        filteredList.setPredicate(expense -> {
            if(expense.getCost() >= min && expense.getCost() <= max)
                return true;
            else
                return false;
        });
        isFiltered = true;

        /*
        filteredList.clear();
        for (int i = 0; i < this.getSize(); i++) {
            if (this.getExpense(i).getDate().after(start) && this.getExpense(i).getDate().before(end)) {
                filteredList.add(this.getList().get(i));
            }
        }*/
    }

    public void filterByRecurring(boolean recurEnabled) {
        filteredList = new FilteredList<>(list, p -> true);
        filteredList.setPredicate(expense -> {
            // The user may wish to see only items that recur OR items that don't
            if(recurEnabled)
                return expense.isScheduled();
            else
                return !expense.isScheduled();
        });
        isFiltered = true;

        /*
        filteredList.clear();
        for (int i = 0; i < getSize(); i++) {
            if (this.getExpense(i).isScheduled()) {
                filteredList.add(getList().get(i));
            }
        }//*/
    }

    /**
     * Calculates the total cost of the user's expenses.
     * @return The total dollar amount of expenses
     */
    public double calculateTotalExpenses() {
        double total_cost = 0.00; //initializes total cost as $0.00

        for (int i = 0; i < getSize(); i++) { //for each expense in the expense list
            total_cost += getExpense(i).getCost(); //add each amount to the total cost
        }

        return total_cost;
    }

    /**
     * Saves the user's data to an external file.
     *
     * @param fileName String representation of the file name
     */
    public void saveUserData(String fileName) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName)); //writes data to a file

        for (int i = 0; i < getSize(); i++) {
            System.out.println("Expense #" + (i+1) + ": "); //prints what number the expense is in the list
            pw.println(getExpense(i).getName()); //then prints its data
            pw.println(getExpense(i).getCost());
            pw.println(getExpense(i).getCategory());
            pw.println(getExpense(i).getDate());
            pw.println(getExpense(i).getNote());
            System.out.println();
        }

        pw.close();
    }

    /**
     * Loads the user's data from an external file.
     *
     * @param fileName String representation of the file name
     */
    public void loadUserData(String fileName) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("file.txt"));

        try {
            StringBuilder sb = new StringBuilder();

            String line = "";
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());

                // Added try for unhandled exception
                try {
                    line = br.readLine();
                }
                catch(Exception ex){}
            }

            String everything = sb.toString();
        } finally {
            // Added try for unhandled exception
            try {
                br.close();
            } catch(Exception ex){}
        }
    }

    public void changeFromRecurring(Expense e){
        int index = filteredList.indexOf(e);
        e.setScheduled(false);
        filteredList.add(index, e);
    }

    public void addToFiltered(Expense e){
        filteredList.add(e);
    }

    /*
    public void sortByDate() {
        Collections.sort(list, compareDate);
    }
    public void addToBothLists(Expense e) {
        list.add(e);
        filteredList.add(e);
    }
    public void sortByDateR() {
        Collections.sort(list, compareDate.reversed());
    }

    private Comparator<Expense> compareDate = new Comparator<Expense>() {
        public int compare(Expense e1, Expense e2) {
            return e1.getDate().compareTo(e2.getDate());
        }
    };

    public void sortByAmount() {
        Collections.sort(list, compareAmount);
    }

    public void sortByAmountR() {
        Collections.sort(list, compareAmount.reversed());
    }

    private Comparator<Expense> compareAmount = new Comparator<Expense>() {
        public int compare(Expense e1, Expense e2) {
            return (new Double(e1.getAmount()).compareTo(new Double(e2.getAmount())));
        }
    };

    public void sortByName() {
        Collections.sort(list, compareName);
    }

    public void sortByNameR() {
        Collections.sort(list, compareName.reversed());
    }

    private Comparator<Expense> compareName = new Comparator<Expense>() {
        public int compare(Expense e1, Expense e2) {
            return e1.getName().compareTo(e2.getName());
        }
    };

    public void fSortByName() {
        Collections.sort(filteredList, compareName);
    }

    public void fSortByDate() {
        Collections.sort(filteredList, compareAmount);
    }

    public void fSortByAmount() {
        Collections.sort(filteredList, compareAmount);
    }

    public void fSortByNameR() {
        Collections.sort(filteredList, compareName.reversed());
    }

    public void fSortByDateR() {
        Collections.sort(filteredList, compareAmount.reversed());
    }

    public void fSortByAmountR() {
        Collections.sort(filteredList, compareAmount.reversed());
    }

  /*
  public void updateScheduledExpenses(){
        Expense e;
        this.filterByRecurring();
        for(int i = 0; i < filteredList.size(); i++){
            if(filteredList.get(i).needsUpdate()){
                e = new Expense(filteredList.get(i).getName(),filteredList.get(i).getAmount(),filteredList.get(i).getCategory(),filteredList.get(i).getDate(),filteredList.get(i).getNote(),filteredList.get(i).getFrequency());
                this.addToBothLists(e);
            }
        }
    }
    */

/*
    public static void main(String[] args) {
        Expense e1 = new Expense("Apple", 1.99, "Food", new Date(), "Golden Delicious");
        Expense e2 = new Expense("Banana", 2.99, "Food", new Date(), "Fruit Salad");
        Expense e3 = new Expense("Watermelon", 4.99, "Food", new Date(), "Yummy Yummy");

        ExpenseList list = new ExpenseList();
        list.addExpense(e1);
        list.addExpense(e2);
        list.addExpense(e3);
        System.out.println(list.toString());
    }
 */
}
