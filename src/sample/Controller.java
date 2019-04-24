package sample;
import java.io.FileNotFoundException;
import java.net.URL;

import javafx.collections.FXCollections;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.textfield.TextFields;

import java.util.ResourceBundle;
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
        view_endDateColumn.setCellValueFactory(new PropertyValueFactory<>("stopDate"));
        view_freqColumn.setCellValueFactory(new PropertyValueFactory<>("displayFrequency"));

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
    @FXML TableColumn<Expense, String> view_endDateColumn;
    @FXML TableColumn<Expense, String> view_noteColumn;
    @FXML TableColumn<Expense, Long> view_freqColumn;

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
    private Button view_editButton;
    @FXML
    private TabPane tabPane;


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
            Expense randExpense = new Expense("Item"+i, i, "Grocery " + i, new Date(), new Date(), "A note");
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
            /*
            expenseList.sortByDate();
            Date today = new Date();
            ObservableList<Expense> holder = expenseList.getList();
            int i = 0;
            while(holder.get(i).getDate().before(today)){
                i++;
            }
            holder.remove(i, holder.size());
            expenseList.setList(holder);

             */
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
        /*
        Date today = new Date();
        ObservableList<Expense> holder = expenseList.getList();
        if(today.before(end)){
            expenseList.updateScheduledExpenses(end);
        }
         */
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

    @FXML TextField settings_usernameField;

    @FXML
    private void loginButtonAction(ActionEvent event)
    {
        String user = settings_usernameField.getText();

        System.out.println(expenseList.toString());
        expenseList.loadUserData(user);
        System.out.println(expenseList.toString());
        updateTable();
    }

    /**
     * Saves the user's data to an external file.
     */
    @FXML
    public void saveUserData(ActionEvent event) throws FileNotFoundException
    {
        expenseList.saveUserData(settings_usernameField.getText());
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
    @FXML private AnchorPane addPane;
    @FXML private Button editButton;

    ArrayList<String> possibleWords = new ArrayList<String>();

    Timer timer = new Timer();

    public static final LocalDate LOCAL_DATE (String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }

    @FXML
    private void editAction()
    {

        add_successfulAdd.setVisible(false);
        add_unSuccessfulAdd.setVisible(false);
        tabPane.getSelectionModel().select(1);
        Expense itemToEdit = null;
        try {
        itemToEdit = view_tableView.getSelectionModel().getSelectedItem();
        }
        catch (Exception ex)
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("You must select an item to edit");
            emptyCostAlert.show();
        }
        view_tableView.getItems().remove(view_tableView.getSelectionModel().getSelectedIndex());


        //Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        Date date = itemToEdit.getDate();
        Date endDate = itemToEdit.getStopDate();

        // NAME
        add_nameInput.setText(itemToEdit.getName());

        // CATEGORY
        add_categoryInput.setText(itemToEdit.getCategory());

        // COST
        add_costInput.setText(Double.toString(itemToEdit.getCost()));

        // START DATE
        add_dateInput.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // NAME
        add_noteInput.setText(itemToEdit.getName());

        if(itemToEdit.isScheduled())
        {
            add_isRecurring.setSelected(true);
            add_frequencyInput.setText(Long.toString(TimeUnit.MILLISECONDS.toDays(itemToEdit.getFrequency())));

            // END DATE
            add_stopDateInput.setValue(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        updateTable();
    }

    @FXML
    private void saveButtonAction(ActionEvent event)
    {
        // Only perform the save action if the required fields in the form are filled out and contain valid info
        if(!isThereEmptyFields() && isThereValidFields())
        {
            // Convert localDate input to util.Date for the start date
            LocalDate localDate = add_dateInput.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date startDate = Date.from(instant);

            // Convert localDate input to util.Date for the end date
            localDate = add_stopDateInput.getValue();
            instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date stopDate = Date.from(instant);

            // Add an the correct type of expense to the expense list
            if(!add_isRecurring.isSelected())
            {
                Expense newExpense = new Expense(add_nameInput.getText(), Double.parseDouble(add_costInput.getText()), add_categoryInput.getText(),
                        startDate, add_noteInput.getText());

                expenseList.addExpense(newExpense);
                add_unSuccessfulAdd.setVisible(false);
                add_successfulAdd.setVisible(true);
                System.out.println("" + expenseList.toString());

                // For the search feature
                possibleWords.add(add_categoryInput.getText());
            }
            else
            {
                Expense newExpense = new Expense(add_nameInput.getText(), Double.parseDouble(add_costInput.getText()), add_categoryInput.getText(),
                        startDate, stopDate, add_noteInput.getText(), TimeUnit.DAYS.toMillis(Long.parseLong(add_frequencyInput.getText())));

                expenseList.addExpense(newExpense);
                add_unSuccessfulAdd.setVisible(false);
                add_successfulAdd.setVisible(true);
                //timer.schedule(displaySuccessful, 5001);
                System.out.println("" + expenseList.toString());

                possibleWords.add(add_categoryInput.getText());
            }
        }
        else
        {
            add_successfulAdd.setVisible(false);
            add_unSuccessfulAdd.setVisible(true);
        }

        updateTable();
    }

    // Verifies that the filled out fields are valid
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

    // Verifies that the correct fields are filled out
    private boolean isThereEmptyFields()
    {
        // Verify that the cost input is filled out
        if(add_costInput.getText() == null || add_costInput.getText().trim().isEmpty())
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter cost");
            emptyCostAlert.show();
            return true;
        }

        // Verify that the name input is filled out
        if(add_nameInput.getText() == null || add_costInput.getText().trim().isEmpty())
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter name for item");
            emptyCostAlert.show();
            return true;
        }

        // Verify that the date input is filled out
        if(add_dateInput.getValue() == null)
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter valid dates");
            emptyCostAlert.show();
            return true;
        }

        // Verify that the category input is filled out
        if(add_categoryInput.getText() == null || add_costInput.getText().trim().isEmpty())
        {
            Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
            emptyCostAlert.setContentText("Please enter category");
            emptyCostAlert.show();
            return true;
        }

        // Verify that the frequency and stop date inputs are filled out
        if(add_isRecurring.isSelected()) {
            if (add_frequencyInput.getText() == null || add_costInput.getText().trim().isEmpty()) {
                Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
                emptyCostAlert.setContentText("Please enter frequency");
                emptyCostAlert.show();
                return true;
            }
            if(add_stopDateInput.getValue() == null)
            {
                Alert emptyCostAlert = new Alert(Alert.AlertType.WARNING);
                emptyCostAlert.setContentText("Please enter valid dates");
                emptyCostAlert.show();
                return true;
            }
        }

        // Note is always optional

        return false;
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
        add_noteInput.clear();
        add_categoryInput.clear();
        add_costInput.clear();
        add_dateInput.getEditor().clear();
        add_frequencyInput.clear();
        add_stopDateInput.getEditor().clear();
        add_noteInput.clear();
        add_successfulAdd.setVisible(false);
        add_unSuccessfulAdd.setVisible(false);
    }

    public void categoryAutoFill()
    {
        TextFields.bindAutoCompletion(add_categoryInput, possibleWords);
    }


    //*/ End Genesis
    @FXML Button updateButton;

    @FXML
    public void updateButtonEvent(){
        expenseList.updateScheduledExpenses(new Date());
        updateChartByCategory();
    }

    @FXML Tab viewPane;
    @FXML Tab graphPane;
    @FXML Tab settingsPane;
    private  SingleSelectionModel<Tab> tabSelector;

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    public void startUpdater(){

        final Runnable updater = new Runnable(){
            public void run(){
                //Stuff we want to happen every 5 seconds goes here
                System.out.println("struggle");
                //updateScheduledExpenses();
            }};
        final ScheduledFuture<?> updaterHandle = scheduler.scheduleAtFixedRate(updater, 5, 5, SECONDS);
    }



    //* Graphics pane content: Jimmy
    @FXML   private BarChart<String, Double> expenseChart;
    @FXML   private CategoryAxis x;
    @FXML   private NumberAxis y;
    @FXML   private Text total;

    DecimalFormat df = new DecimalFormat("#.##");

    private void updateChartByCategory(){
        expenseChart.getData().clear();
        XYChart.Series<String, Double> set = new XYChart.Series<>();
        ArrayList<String> categoriesThatHaveAlreadyBeenComputed = new ArrayList<>();
        for(int o = 0; o < expenseList.getList().size(); o++)
        {
            if (! (categoriesThatHaveAlreadyBeenComputed.equals(expenseList.getList().get(o).getCategory()) )){
                expenseList.filterByCategory(expenseList.getExpense(o).getCategory());
                double sum = 0;
                for (int m = 0; m < expenseList.getFilteredList().size(); m++){
                    sum += expenseList.getFilteredList().get(m).getCost();
                }
                set.getData().add(new XYChart.Data<>(expenseList.getExpense(o).getCategory(), sum));
                categoriesThatHaveAlreadyBeenComputed.add(expenseList.getExpense(o).getCategory());
                System.out.println(sum);
            }
            System.out.println(expenseList.toString());
            System.out.println(categoriesThatHaveAlreadyBeenComputed.toString());
        }
        expenseChart.getData().addAll(set);
        String teal = "$" + df.format(expenseList.calculateTotalExpenses());
        total.setText(teal);
        expenseList.clearFilter();
    }
    // End Jimmy
}
