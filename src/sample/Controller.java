package sample;
import java.net.URL;
import java.net.URL.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import org.controlsfx.control.textfield.TextFields;

import java.time.LocalDate;
import java.util.ResourceBundle;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Controller implements Initializable
{
    ExpenseList expenseList = ExpenseList.getExpenseList();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        //* Set up options for controls
        ObservableList<String> filterOptions = FXCollections.observableArrayList();
        filterOptions.add("No Filter");
        filterOptions.add("Name");
        filterOptions.add("Category");
        filterOptions.add("Cost");
        filterOptions.add("Date");
        filterOptions.add("Recurring");
        view_filterCombo.setItems(filterOptions);

        view_tableView.setEditable(true);
        // Load expenseList from save data

        // Set the factory values for each entry
        // These will ensure that the each field in an expense will map to the correct column
        view_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        view_amountColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        view_categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        view_dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        view_noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Prompt for user/password
        // Load the appropriate expenseList from save data

        updateTable();

    }

    //*/ Michael
    @FXML TableView<Expense> view_tableView;
    @FXML TableColumn<Expense, String> view_nameColumn;
    @FXML TableColumn<Expense, Double> view_amountColumn;
    @FXML TableColumn<Expense, String> view_categoryColumn;
    @FXML TableColumn<Expense, String> view_dateColumn;
    @FXML TableColumn<Expense, String> view_noteColumn;

    @FXML ComboBox<String> view_filterCombo;

    @FXML AnchorPane view_dateFilterPane;
    @FXML AnchorPane view_costFilterPane;
    @FXML AnchorPane view_nameFilterPane;
    @FXML AnchorPane view_categoryFilterPane;
    @FXML AnchorPane view_recurFilterPane;

    @FXML TextField view_filterStartCost;
    @FXML TextField view_filterEndCost;
    @FXML DatePicker view_filterStartDate;
    @FXML DatePicker view_filterEndDate;
    @FXML TextField view_filterName;
    @FXML TextField view_filterCategory;
    @FXML CheckBox view_filterRecur;

    @FXML
    private Button view_deleteButton;
    @FXML
    private Button view_deleteAllButton;

    @FXML
    public void updateTable()
    {
        /* Create columns for each expense entry
        TableColumn<Expense, String> nameCol = new TableColumn<Expense, String>("Name");
        TableColumn<Expense, Double> amountCol = new TableColumn<Expense, Double>("Amount");
        TableColumn<Expense, String> categoryCol = new TableColumn<Expense, String>("Category");
        TableColumn<Expense, String> dateCol = new TableColumn<Expense, String>("Date");
        //*/

        // Display the column on the table
        //view_tableView.getColumns().addAll(nameCol, amountCol, categoryCol, dateCol);


        // Create a sample expense list and populate it with data
        /* Then add that sample data to the table for testing
        expenseList.clear();
        for (int i = 0; i < 10; i++)
        {
            Expense randExpense = new Expense("Item"+i, i, "Grocery " + i, new Date(), "A note");
            expenseList.addExpense(randExpense);
        }
        //*/

        if(expenseList.getFilteredList() != null)
            view_tableView.setItems(expenseList.getFilteredList());
        else
            view_tableView.setItems(expenseList.getList());

        updateChartByCategory();
    }

    @FXML
    private void deleteItem()
    {
        try {
            Expense itemToDelete = view_tableView.getSelectionModel().getSelectedItem();

            view_tableView.getItems().remove(view_tableView.getSelectionModel().getSelectedIndex());

            System.out.println(expenseList.toString());
        }
        catch (Exception e)
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("You must select an item to delete");
            emptyCostAlert.show();
        }
    }


    @FXML
    private void changeFilterOptions(ActionEvent event)
    {
        // Make all filter option panes to invisible
        view_nameFilterPane.setVisible(false);
        view_dateFilterPane.setVisible(false);
        view_categoryFilterPane.setVisible(false);
        view_costFilterPane.setVisible(false);
        view_recurFilterPane.setVisible(false);

        // Reveal only the one that has been chosen by the user
        if(view_filterCombo.getValue().equals("No Filter")) {
            expenseList.clearFilter();
            view_tableView.setItems(expenseList.getList());
            return;
        }
        if(view_filterCombo.getValue().equals("Date")) {
            view_dateFilterPane.setVisible(true);
        }
        if(view_filterCombo.getValue().equals("Cost")) {
            view_costFilterPane.setVisible(true);
        }
        if(view_filterCombo.getValue().equals("Name")){
            view_nameFilterPane.setVisible(true);
        }
        if(view_filterCombo.getValue().equals("Recurring")){
            view_recurFilterPane.setVisible(true);
        }
        if(view_filterCombo.getValue().equals("Category")){
            view_categoryFilterPane.setVisible(true);
        }
    }

    @FXML
    private void applyCategoryFilter(ActionEvent event)
    {
        // Get input from user, then use it as the filter
        expenseList.filterByCategory(view_filterCategory.getText());

        // Apply filtered list to the table view
        view_tableView.setItems(expenseList.getFilteredList());
    }

    @FXML
    private void applyDateFilter(ActionEvent event)
    {
        // Only apply filter if the user has picked both start and end dates
        if(view_filterStartDate == null && view_filterEndDate == null)
            return;

        // Get the start and end dates from the datepicker control, then filter with them
        Date start = java.sql.Date.valueOf(view_filterStartDate.getValue());
        Date end = java.sql.Date.valueOf(view_filterEndDate.getValue());
        expenseList.filterByDate(start, end);

        // Apply filtered list to the table view
        updateTable();
    }

    @FXML
    private void applyCostFilter(ActionEvent event)
    {
        // Only apply filter if the user has picked both start and end cost
        if(view_filterStartCost == null && view_filterEndCost == null)
            return;

        // Get the min and max costs from the user, then filter with them
        double min = Double.parseDouble(view_filterStartCost.getText());
        double max = Double.parseDouble(view_filterEndCost.getText());
        expenseList.filterByCost(min, max);

        // Apply filtered list to the table view
        updateTable();
    }

    @FXML
    private void applyRecurFilter(ActionEvent event)
    {
        // Get input from user, then user it as the filter
        expenseList.filterByRecurring(view_filterRecur.isSelected());

        // Apply filtered list to the table view
        updateTable();
    }

    @FXML
    private void applyNameFilter(ActionEvent event)
    {
        // Get input from user, then user it as the filter
        expenseList.filterByName(view_filterName.getText());

        // Apply filtered list to the table view
        updateTable();
    }
    //*/ End Michael


    //* Genesis
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private TextField add_nameInput;
    @FXML private TextField add_categoryInput;
    @FXML private TextField add_costInput;
    @FXML private DatePicker add_dateInput;
    @FXML private TextField add_frequencyInput;
    @FXML private TextField add_noteInput;
    @FXML private DatePicker add_stopDateInput;
    @FXML private CheckBox add_isRecurring;
    @FXML private Label add_successfulAdd;
    @FXML private Label add_unSuccessfulAdd;
    @FXML private Label add_frequencyInfo;

    ArrayList<String> possibleWords = new ArrayList<String>();

    Timer timer = new Timer();

    @FXML
    private void saveButtonAction(ActionEvent event)
    {
        if(!isThereEmptyFields() && isThereValidFields())
        {
            LocalDate localDate = add_dateInput.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date newDate = Date.from(instant);

            localDate = add_stopDateInput.getValue();
            instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date stopDate = Date.from(instant);

           // Date newDate = new Date(add_dateInput.getValue().toEpochDay());
            //Date stopDate = new Date(add_stopDateInput.getValue().toEpochDay());

            if(!add_isRecurring.isSelected())
            {
                Expense newExpense = new Expense(add_nameInput.getText(), Double.parseDouble(add_costInput.getText()), add_categoryInput.getText(),
                        newDate, stopDate, add_noteInput.getText());

                expenseList.addExpense(newExpense);
                possibleWords.add(add_categoryInput.getText());

                add_unSuccessfulAdd.setVisible(false);
                add_successfulAdd.setVisible(true);
                System.out.println("" + expenseList.toString());
            }
            else
            {
                Expense newExpense = new Expense(add_nameInput.getText(), Double.parseDouble(add_costInput.getText()), add_categoryInput.getText(),
                        newDate, stopDate, add_noteInput.getText(), TimeUnit.DAYS.toMillis(Long.parseLong(add_frequencyInput.getText())));

                expenseList.addExpense(newExpense);
                possibleWords.add(add_categoryInput.getText());

                add_unSuccessfulAdd.setVisible(false);
                add_successfulAdd.setVisible(true);
                //timer.schedule(displaySuccessful, 5001);
                System.out.println("" + expenseList.toString());
            }
        }
        else
        {
            add_successfulAdd.setVisible(false);
            add_unSuccessfulAdd.setVisible(true);
        }

        updateTable();
    }

    private boolean isThereValidFields()
    {
        double returnVal = 0;
        try {
            Double.parseDouble(add_costInput.getText());
        }
        catch(Exception e) {
            //  Block of code to handle errors
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter valid numbers for cost and frequency");
            emptyCostAlert.show();
            return false;
        }
        try {
            if(add_isRecurring.isSelected()) {
                Double.parseDouble(add_frequencyInput.getText());
            }
        }
        catch(Exception e) {
            //  Block of code to handle errors
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter valid numbers for cost and frequency");
            emptyCostAlert.show();
            return false;
        }
        return true;
    }

    private boolean isThereEmptyFields()
    {
        if(add_costInput.getText() == null || add_costInput.getText().trim().isEmpty())
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter cost");
            emptyCostAlert.show();
            return true;
        }

        if(add_nameInput.getText() == null || add_costInput.getText().trim().isEmpty())
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter name for item");
            emptyCostAlert.show();
            return true;
        }
        if(add_dateInput.getValue() == null || add_stopDateInput.getValue() == null)
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter valid dates");
            emptyCostAlert.show();
            return true;
        }
        if(add_categoryInput.getText() == null || add_costInput.getText().trim().isEmpty())
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter category");
            emptyCostAlert.show();
            return true;
        }
        if(add_isRecurring.isSelected() && add_frequencyInput.getText() == null || add_costInput.getText().trim().isEmpty())
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter frequency");
            emptyCostAlert.show();
            return true;
        }
        if(add_noteInput.getText() == null || add_noteInput.getText().trim().isEmpty())
        {
            add_noteInput.setText("");
        }

        return false;
    }

    @FXML
    public void testFunction()
    {
        System.out.println("HELLO!");
    }

    @FXML
    private void toggleFrequency()
    {
        if(add_isRecurring.isSelected())
        {
            add_frequencyInfo.setVisible(true);
            add_frequencyInput.setVisible(true);
        }
        else
        {
            add_frequencyInfo.setVisible(false);
            add_frequencyInput.setVisible(false);
        }
    }

    @FXML
    private void clearButtonAction(ActionEvent event)
    {
        add_nameInput.clear();
        add_categoryInput.clear();
        add_costInput.clear();
        add_dateInput.getEditor().clear();
        add_frequencyInput.clear();
    }

    public void categoryAutoFill()
    {
        TextFields.bindAutoCompletion(add_categoryInput, possibleWords);
    }


    //*/ End Genesis

    private void updateScheduledExpenses(){
        Expense e;
        expenseList.filterByRecurring(true);
        for(int i = 0; i < expenseList.getFilteredList().size(); i++){
            if(expenseList.getFilteredList().get(i).needsUpdate()){
               // e = new Expense(expenseList.getFilteredList().get(i).getName(),expenseList.getFilteredList().get(i).getCost(),
                        //expenseList.getFilteredList().get(i).getCategory(),expenseList.getFilteredList().get(i).getNextOccurrence(),expenseList.getFilteredList().get(i).getFrequency());
                //Need function that puts lets user confirm stuff
                e = new Expense(hold.getName(), hold.getCost(), hold.getCategory(), hold.getDate(), hold.getStopDate(), hold.getNote(), hold.getFrequency());
                //setAddPaneFields(hold);
                expenseList.addToFiltered(e);
                expenseList.changeFromRecurring(hold);
            }
        }
    }

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    public void startUpdateFiveSeconds(){

        final Runnable updater = new Runnable(){
            public void run(){
                //Stuff we want to happen every 5 seconds goes here
                updateScheduledExpenses();
            }};
        final ScheduledFuture<?> updaterHandle = scheduler.scheduleAtFixedRate(updater, 5, 5, SECONDS);
    }
    //*/ End Genesis



    //* Graphics pane content: Jimmy
    @FXML   private BarChart<?,?> expenseChart;
    @FXML   private CategoryAxis x;
    @FXML   private NumberAxis y;
    @FXML   private Text total;

    private void setUpChartByCategory(){
        XYChart.Series set1 = new XYChart.Series();
        Expense e1 = new Expense("Apple", 1.99, "Food", new Date(), new Date(), "Golden Delicious");
        Expense e2 = new Expense("Banana", 2.99, "Food", new Date(), new Date(),"Fruit Salad");
        Expense e3 = new Expense("Watermelon", 4.99, "Food", new Date(),new Date(), "Yummy Yummy");
        Expense e4 = new Expense("Watermelon", 4.99, "Inorganic", new Date(), new Date(),"Yummy Yummy");
        expenseList.addExpense(e1);
        expenseList.addExpense(e2);
        expenseList.addExpense(e3);
        expenseList.addExpense(e4);
        ArrayList<String> categoriesThatHaveAlreadyBeenComputed = new ArrayList<>();
        double sumOfSums = 0;
        for(int o = 0; o < expenseList.getSize(); o++)
        {
            if (!(categoriesThatHaveAlreadyBeenComputed.contains(expenseList.getExpense(o).getCategory()))){
                expenseList.filterByCategory(expenseList.getExpense(o).getCategory());
                double sum = 0;
                for (int m = 0; m < expenseList.getFilteredList().size(); m++){
                    sum += expenseList.getFilteredList().get(m).getCost();
                }
                sumOfSums += sum;
                set1.getData().add(new XYChart.Data(expenseList.getExpense(o).getCategory(), sum));
                categoriesThatHaveAlreadyBeenComputed.add(expenseList.getExpense(o).getCategory());
            }

        }
        total.setText("$" + sumOfSums);
        expenseChart.getData().addAll(set1);
    }
    // End Jimmy
}
