package viewmodel;

import com.azure.storage.blob.BlobClient;
import dao.DbConnectivityClass;
import dao.StorageUploader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class DB_GUI_Controller implements Initializable {

    StorageUploader store =new StorageUploader();
    @FXML
    private ProgressBar progressBar;
    @FXML
    TextField first_name, last_name, department, email, imageURL;

    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    // just added
    @FXML
    private MenuItem ChangePic, ClearItem, CopyItem, deleteItem, editItem, logOut, newItem, exportCSV, importCSV;
    @FXML
    private Button addBtn;
    @FXML
    private Label statusBar;
    @FXML
    private ComboBox<String> majorDropdown;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);

            // Populate Major Dropdown
            ObservableList<String> majors = FXCollections.observableArrayList("CS", "CPIS", "English", "Business");
            majorDropdown.setItems(majors);

            // Initialize button states
            initializeButtonStates();

            // Add listener for table view selection
            tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                boolean recordSelected = newValue != null;
                editItem.setDisable(!recordSelected);
                deleteItem.setDisable(!recordSelected);
            });

            // Add validation listeners for "Add" button
            addValidationListeners();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addValidationListeners() {
        // Add listeners to validate form fields
        ChangeListener<String> fieldValidator = (observable, oldValue, newValue) -> {
            boolean valid = validateFormFields();
            addBtn.setDisable(!valid); // Enable Add button if valid
        };

        first_name.textProperty().addListener(fieldValidator);
        last_name.textProperty().addListener(fieldValidator);
        department.textProperty().addListener(fieldValidator);
        //major.textProperty().addListener(fieldValidator);
        email.textProperty().addListener(fieldValidator);
        imageURL.textProperty().addListener(fieldValidator);
        majorDropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = validateFormFields();
            addBtn.setDisable(!valid); // Enable Add button if valid
        });
    }

    private boolean validateFormFields() {
        String nameRegex = "^[A-Z][a-zA-Z\\s-]*$"; // Capitalized names, optional hyphens
        String departmentRegex = "^[A-Za-z\\s]+$"; // Letters and spaces only
        String emailRegex = "^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$"; // Standard email format
        String imageUrlRegex = "^(https?|file):\\/\\/.*$"; // Valid URL starting with http(s) or file


        // Ensure all fields are non-empty and email is valid
        return !first_name.getText().isBlank()
                && !last_name.getText().isBlank()
                && !department.getText().isBlank()
                && email.getText().matches("\\S+@\\S+\\.\\S+")
                && majorDropdown.getValue() != null // Check dropdown selection
                && !imageURL.getText().isBlank();
    }

    private void initializeButtonStates() {
        // Initially disable Edit and Delete buttons
        editItem.setDisable(true);
        deleteItem.setDisable(true);

        // Validate Add button
        addBtn.setDisable(true);
    }

    @FXML
    protected void addNewRecord() {
        try {
            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    majorDropdown.getValue(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
            statusBar.setText("Record added successfully!");
        } catch (Exception e) {
            statusBar.setText("Error adding record.");
        }

    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
//        major.setText("");
        email.setText("");
        imageURL.setText("");
        majorDropdown.setValue(null); // Reset ComboBox selection
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        try {
            Person p = tv.getSelectionModel().getSelectedItem();
            int index = data.indexOf(p);
            Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                    majorDropdown.getValue(), email.getText(), imageURL.getText());
            cnUtil.editUser(p.getId(), p2);
            data.remove(p);
            data.add(index, p2);
            tv.getSelectionModel().select(index);
            statusBar.setText("Record updated successfully!");
        } catch (Exception e) {
            statusBar.setText("Error updating record.");
        }
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
//        major.setText(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    @FXML
    public void handleImportCSV(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

        if (file != null) {
            try {
                ObservableList<Person> importedData = FXCollections.observableArrayList();
                List<String> lines = Files.readAllLines(file.toPath());

                for (String line : lines) {
                    String[] values = line.split(","); // Assuming CSV fields are comma-separated

                    // Validate each row in the CSV file
                    if (values.length != 6 || Arrays.stream(values).anyMatch(String::isBlank)) {
                        throw new IllegalArgumentException("Invalid CSV format in row: " + line);
                    }

                    // Create a Person object from valid data
                    Person person = new Person(values[0], values[1], values[2], values[3], values[4], values[5]);
                    importedData.add(person);
                }

                // Add imported data to table and database
                for (Person person : importedData) {
                    cnUtil.insertUser(person);
                    cnUtil.retrieveId(person);
                    person.setId(cnUtil.retrieveId(person));
                }
                data.addAll(importedData);
                tv.setItems(data);

                statusBar.setText("CSV file imported successfully.");
            } catch (IllegalArgumentException e) {
                statusBar.setText("Error importing CSV: " + e.getMessage());
            } catch (Exception e) {
                statusBar.setText("Error importing CSV file.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleExportCSV(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        if (file != null) {
            try {
                List<String> lines = new ArrayList<>();
                for (Person person : data) {
                    lines.add(String.join(",",
                            person.getFirstName(),
                            person.getLastName(),
                            person.getDepartment(),
                            person.getMajor(),
                            person.getEmail(),
                            person.getImageURL()
                    ));
                }
                Files.write(file.toPath(), lines);

                statusBar.setText("CSV file exported successfully.");
            } catch (Exception e) {
                statusBar.setText("Error exporting CSV file.");
                e.printStackTrace();
            }
        }
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }
    @FXML
    protected void handleUploadButtonClick() {
        File file = (new FileChooser()).showOpenDialog(progressBar.getScene().getWindow());
        if (file != null) {
            Task<Void> uploadTask = createUploadTask(file, progressBar);
            progressBar.progressProperty().bind(uploadTask.progressProperty());
            new Thread(uploadTask).start();
        }
    }

    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress as a percentage
                        updateProgress(uploadedBytes, fileSize);
                    }
                }

                return null;
            }
        };
    }


}