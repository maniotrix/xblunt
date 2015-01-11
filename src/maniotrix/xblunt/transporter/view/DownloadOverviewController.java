package maniotrix.xblunt.transporter.view;

import java.io.File;
import java.nio.file.Paths;

import maniotrix.xblunt.transporter.MainApp;
import maniotrix.xblunt.transporter.model.download.DownloadModel;
import maniotrix.xblunt.transporter.model.download.Status;
import maniotrix.xblunt.transporter.util.FileUtility;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;

public class DownloadOverviewController {
	@FXML
	private TableView<DownloadModel> DownloadTable;
	@FXML
	private TableColumn<DownloadModel, String> urlColumn;
	@FXML
	private TableColumn<DownloadModel, String> sizeColumn;
	@FXML
	private TableColumn<DownloadModel, String> progressColumn;
	@FXML
	private TableColumn<DownloadModel, String> speedColumn;
	@FXML
	private TableColumn<DownloadModel, String> statusColumn;
	@FXML
	private Button pauseButton;
	@FXML
	private Button resumeButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Button clearButton;
	@FXML
	private Button addButton;
	@FXML
	private Button restartButton;
	@FXML
	private TextField urlArea;

	// private DownloadModel selectedDownloadModel = new DownloadModel();
	// public static StringProperty ControllerStatus;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public DownloadOverviewController() {
		// ControllerStatus = new SimpleStringProperty();
	}

	/*
	 * public void bindButtons() { pauseButton.disableProperty().bind(
	 * ControllerStatus.isNotEqualTo(Status.Downloading.toString()));
	 * resumeButton.disableProperty().bind(
	 * ControllerStatus.isNotEqualTo(Status.Paused.toString())); }
	 */

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize the Download table with the two columns.
		urlColumn.setCellValueFactory(cellData -> cellData.getValue()
				.FilenameProperty());
		sizeColumn.setCellValueFactory(cellData -> cellData.getValue()
				.SizeProperty());
		progressColumn.setCellValueFactory(cellData -> cellData.getValue()
				.ProgressProperty());
		speedColumn.setCellValueFactory(cellData -> cellData.getValue()
				.SpeedProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue()
				.StatusProperty());

		// bindButtons();
		// Listen for selection changes and show the download details when
		// changed.
		/*
		 * ControllerStatus.addListener((observable) -> {
		 * //MainApp.savedownloadDataToFile(); });
		 */
		/*
		 * DownloadTable.getSelectionModel().selectedItemProperty()
		 * .addListener((observable) -> { if (observable != null) {
		 * ChangeStatus(); } });
		 */
		/**
		 * will be included in next version. 
		 */
		/*DownloadTable.setOnMouseClicked(event -> {
			if(event.getButton().equals(MouseButton.PRIMARY)){
	            if(event.getClickCount() == 2){
	            	 openSelected();
	                System.out.println("Double clicked");
	                //openSelected();
	            }
	        }
		});*/

	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public MainApp getMainApp() {
		return this.mainApp;
	}

	/*
	 * public void ChangeStatus(){ DownloadModel selected; selected =
	 * this.DownloadTable.getSelectionModel().getSelectedItem(); if (selected !=
	 * null) { ControllerStatus.set(selected.getStatus());
	 * //mainApp.savedownloadDataToFile(); } }
	 */
	/**
	 * add new download to the table.
	 */
	public void handleAddButton() {
		String url = this.urlArea.getText();
		if (url != null && verifyUrl(url) == false)
			return;
		FileChooser fileChooser = new FileChooser();
		String filepath = null;
		try {
			filepath = fileChooser.showSaveDialog(mainApp.getPrimaryStage())
					.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (filepath != null) {
			mainApp.getdownloadData().add(new DownloadModel(url, 8, filepath));
		} else {
			if (filepath == null)
				mainApp.getdownloadData().add(new DownloadModel(url, 8, url));
		}
		// Add observable list data to the table
		DownloadTable.setItems(mainApp.getdownloadData());
		this.urlArea.setText("");
		this.urlArea.setPromptText("url");
		// mainApp.savedownloadDataToFile();
	}

	// Pause the selected download.
	public void handlePauseButton() {
		DownloadModel selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			selected.getDownload().pause();
			// ControllerStatus.set(selected.getStatus());
			// mainApp.savedownloadDataToFile();
		}
		
	}

	// Resume the selected download.
	public void handleResumeButton() {
		DownloadModel selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			selected.getDownload().resume();
			// ControllerStatus.set(selected.getStatus());
			// mainApp.savedownloadDataToFile();
		}
	}

	// Cancel the selected download.
	public void handleCancelButton() {
		DownloadModel selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem();
		/*if (selected.getStatus() == Status.Cancelled.toString())
			return;*/
		if (selected != null) {
			selected.getDownload().cancel();
			// ControllerStatus.set(selected.getStatus());
			// mainApp.savedownloadDataToFile();
		}

	}

	// Clear the selected download.
	public void handleClearButton() {
		DownloadModel selected;
		int selectedIndex = DownloadTable.getSelectionModel()
				.getSelectedIndex();
		if (selectedIndex >= 0) {
			selected = this.DownloadTable.getSelectionModel().getSelectedItem();
			selected.getDownload().error();
			DownloadTable.getItems().remove(selectedIndex);
			selected = null;
		}
		selected = this.DownloadTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			// ControllerStatus.set(selected.getStatus());
		}
		// System.out.println("No. of Items in table= "+)
		else
			MainApp.savedownloadDataToFile();

	}

	public void handleRestartButton() {
		// ControllerStatus.set(Status.Downloading.toString());
		DownloadModel selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			selected.resetDownload();
			selected = this.DownloadTable.getSelectionModel().getSelectedItem();
			// ControllerStatus.set(Status.Downloading.toString());
			// mainApp.savedownloadDataToFile();
		}

	}

	public static boolean verifyUrl(String url) {
		if (url.toLowerCase().startsWith("https://")
				|| url.toLowerCase().startsWith("http://"))
			return true;
		else
			return false;
	}

	public TableView<DownloadModel> getDownloadTable() {
		return DownloadTable;
	}
	
/*	//include in next version 
	public void openSelected(){
		DownloadModel selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem();
		if(selected.getDownload().getStatus()==Status.Completed){
			File file=Paths.get(selected.getFilename()).toFile();
			FileUtility.openFile(file);
			System.out.println("file opened");
		}
		
	}*/
	/*
	 * public String getControllerStatus() { return ControllerStatus.get(); }
	 * 
	 * public StringProperty getControllerStatusProperty() { return
	 * ControllerStatus; }
	 * 
	 * public void setControllerStatus(String status) {
	 * this.ControllerStatus.set(status); }
	 * 
	 * public void setControllerStatus(StringProperty controllerStatus) {
	 * ControllerStatus = controllerStatus; }
	 */
}
