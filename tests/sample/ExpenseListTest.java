package sample;

import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseListTest {

    private static ExpenseList eList;
    private static Expense e1, e2, e3, e4, e5;

    @BeforeAll
    static void setUp() {
        eList = ExpenseList.getExpenseList();
        e1 = new Expense("Test1", 42.42, "test1", new Date(new Date().getTime() - 1000000), new Date(), "foo bar");
        e2 = new Expense("Test2", 420.42, "test1", new Date(new Date().getTime() - 2000000), new Date(), "foo bar", 750000);
        e3 = new Expense("Test3", 4200.42, "test1", new Date(new Date().getTime() - 3000000), new Date(), "foo bar");
        e4 = new Expense("Test4", 42000.42, "tester2", new Date(new Date().getTime() - 4000000), new Date(), "foo bar");
        e5 = new Expense("Test5", 420000.42, "tester2", new Date(new Date().getTime() - 5000000), new Date(), "foo bar", 500000);
        eList.addExpense(e1);
        eList.addExpense(e2);
        eList.addExpense(e3);
        eList.addExpense(e4);
        eList.addExpense(e5);
    }

    @BeforeEach
    void clearChanges(){
        eList.clearFilter();
    }

    @Test
    void testSize() {
        assertEquals(5, eList.getSize());
    }

    @Test
    void testFilterByCategory(){
        eList.filterByCategory("test1");
        assertEquals(5, eList.getSize());
    }

    @Test
    void testFilterByName() {
        eList.filterByName("Test");
        assertEquals(5, eList.getSize());
    }

    @Test
    void testFilterByCost() {
        eList.filterByCost(1000, 100000);
        assertEquals(2, eList.getSize());
    }

    @Test
    void testFilterByRecurring() {
        eList.filterByRecurring(true);
        assertEquals(2, eList.getSize());
    }

    @Test
    void testAddTotal() {
        assertEquals(42.42 + 420.42 + 4200.42 + 42000.42 + 420000.42,
                eList.calculateTotalExpenses(), .01);
    }

    @Test
    void testUpdateScheduled() {
        eList.updateScheduledExpenses(new Date());
        eList.clearFilter();
        assertEquals(16, eList.getSize());
    }

    @Test
    public void testLoadUserData() {
        eList.loadUserData("Test");
        assertEquals(4, eList.getSize());
        assertEquals(50.0, eList.getExpense(1).getCost());
    }
}