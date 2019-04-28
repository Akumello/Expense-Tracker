package sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.Expense;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {

    Expense e;

    @BeforeEach
    void setUp() {
        e = new Expense("Test", 42.42, "test", new Date(), null, "foo bar");
    }

    @Test
    void testScheduled() {
        e.setScheduled(true);
        assert(e.isScheduled());
    }

    @Test
    void testName() {
        e.setName("hello");
        assertEquals("hello", e.getName());
    }

    @Test
    void testCost() {
        e.setCost(420.69);
        assertEquals(420.69, e.getCost(), .01);
    }

    @Test
    void testCategory() {
        e.setCategory("world");
        assertEquals("world", e.getCategory());
    }

    @Test
    void testDate() {
        Date today = new Date();
        e.setDate(today);
        assertEquals(today, e.getDate());
    }

    @Test
    void testFrequency() {
        e.setFrequency(5);
        assertEquals(5, e.getFrequency());
    }

    @Test
    void testNote() {
        e.setNote("bar foo");
        assertEquals("bar foo", e.getNote());
    }

    @Test
    void testNextOccurrence() {
        e.setNextOccurrence(new Date(e.getDate().getTime() + e.getFrequency()));
        assertEquals(new Date(e.getDate().getTime() + e.getFrequency()),e.getNextOccurrence());
    }
}