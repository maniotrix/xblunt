package maniotrix.xblunt.transporter.view;

import maniotrix.xblunt.transporter.MainApp;
import maniotrix.xblunt.transporter.model.DownloadModel;
import maniotrix.xblunt.transporter.model.download.Download;
import maniotrix.xblunt.transporter.model.download.Status;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
	private TextField urlArea;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public DownloadOverviewController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize the Download table with the two columns.
		urlColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getUrlName());
		sizeColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getsize());
		progressColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getprogress());
		speedColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getSpeed());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getStatus());

		// Listen for selection changes and show the person details when
		// changed.
		DownloadTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> update(newValue));

	}

	public void update(DownloadModel newValue) {
		this.updateButtons(newValue.getdownloadInstance());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		pauseButton.setDisable(true);
		resumeButton.setDisable(true);
		cancelButton.setDisable(true);
		clearButton.setDisable(true);
	}

	/**
	 * add new download to the table.
	 */
	public void handleAddButton() {
		mainApp.getdownloadData().add(
				new DownloadModel(this, this.urlArea.getText(), 8));
		// Add observable list data to the table
		DownloadTable.setItems(mainApp.getdownloadData());
		this.urlArea.setText("");
		this.urlArea.setPromptText("url");
	}

	// Pause the selected download.
	public void handlePauseButton() {
		Download selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem()
				.getdownloadInstance();
		selected.pause();
		updateButtons(selected);
	}

	// Resume the selected download.
	public void handleResumeButton() {
		Download selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem()
				.getdownloadInstance();
		selected.resume();
		updateButtons(selected);
	}

	// Cancel the selected download.
	public void handleCancelButton() {
		Download selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem()
				.getdownloadInstance();
		selected.cancel();
		updateButtons(selected);
	}

	// Clear the selected download.
	public void handleClearButton() {
		int selectedIndex = DownloadTable.getSelectionModel()
				.getSelectedIndex();
		DownloadTable.getItems().remove(selectedIndex);
		Download selected;
		selected = this.DownloadTable.getSelectionModel().getSelectedItem()
				.getdownloadInstance();
		updateButtons(selected);
	}

	/*
	 * Update each button's state based off of the currently selected download's
	 * status.
	 */
	private void updateButtons(Download selected) {
		if (selected != null) {
			Status status = selected.getstatus();
			switch (status) {
			case Downloading:
				pauseButton.setDisable(false);
				resumeButton.setDisable(true);
				cancelButton.setDisable(false);
				clearButton.setDisable(true);
				break;
			case Paused:
				pauseButton.setDisable(true);
				resumeButton.setDisable(false);
				cancelButton.setDisable(false);
				clearButton.setDisable(false);
				break;
			case Error:
				pauseButton.setDisable(true);
				resumeButton.setDisable(true);
				cancelButton.setDisable(false);
				clearButton.setDisable(false);
				break;
			default: // COMPLETE or CANCELLED
				pauseButton.setDisable(true);
				resumeButton.setDisable(true);
				cancelButton.setDisable(true);
				clearButton.setDisable(false);
			}
		} else {
			// No download is selected in table.
			pauseButton.setDisable(true);
			resumeButton.setDisable(true);
			cancelButton.setDisable(true);
			clearButton.setDisable(true);
		}
	}
}
