package maniotrix.xblunt.transporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.controlsfx.dialog.Dialogs;
import maniotrix.xblunt.transporter.model.download.DownloadModel;
import maniotrix.xblunt.transporter.model.download.DownloadModelListWrapper;
import maniotrix.xblunt.transporter.model.download.Status;
import maniotrix.xblunt.transporter.view.DownloadOverviewController;
import maniotrix.xblunt.transporter.view.ProxyEditController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class MainApp extends Application {

	private static Stage primaryStage;
	private BorderPane rootLayout;
	/**
	 * The data as an observable list of downloads.
	 */
	public static ObservableList<DownloadModel> downloadModelList = FXCollections
			.observableArrayList();
	
	/**
	 * Constructor
	 */
	public MainApp() {

	}

	@Override
	public void start(Stage primaryStage) {
		MainApp.primaryStage = primaryStage;
		MainApp.primaryStage.setTitle("Xblunt");
		MainApp.primaryStage.setOnCloseRequest((we) -> {
			System.out.println("Stage is closing");
			createCloseDialog();
			try {
				saveOnClose();
				if (downloadModelList.size() != 0) {
					savedownloadDataToFile();
				}
				if(downloadModelList.size()==0){
					MainApp.resetDataFile();
				}
				System.out.println("Stage closed successfully");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Stage closing error");
			}
		});

		initRootLayout();

		showdownloadOverview();
		
		if (MainApp.getConfigFile().length() != 0) {
			MainApp.loaddownloadDataFromFile();
		}
		
		setListConfig();
		
	}

	@SuppressWarnings("deprecation")
	public void createCloseDialog() {
		Dialogs.create()
        .title("Xblunt")
        .masthead("Saving Data")
        .message("Operation Complete.\nPress Ok to Exit Application")
        .showInformation();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*if (MainApp.getConfigFile().length() != 0) {
			MainApp.loaddownloadDataFromFile();
		}*/
	}

	/**
	 * Shows the Download overview inside the root layout.
	 */
	public void showdownloadOverview() {
		try {
			// Load Download overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/DownloadOverview.fxml"));
			AnchorPane downloadOverview = (AnchorPane) loader.load();

			// Set Download overview into the center of root layout.
			rootLayout.setCenter(downloadOverview);

			// Give the controller access to the main app.
			DownloadOverviewController controller = loader.getController();
			controller.setMainApp(this);
			controller.getDownloadTable().setItems(downloadModelList);
			//setListConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean showProxyEditor() {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Proxy.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Proxy"+ProxyEditController.proxytype.toUpperCase());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			//dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			ProxyEditController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			//controller.setMainApp(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Returns the data as an observable list of downloads.
	 * 
	 * @return
	 */
	public ObservableList<DownloadModel> getdownloadData() {
		return downloadModelList;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void useProxySocks_v5() {
		String host = "127.0.0.1";
		String port = "9050";
		System.out.println("Using proxy: " + host + ":" + port);
		System.setProperty("socksProxyVersion", " 5");
		System.setProperty("socksProxyHost", host);
		System.setProperty("socksProxyPort", port);
	}

	/**
	 * Loads Download data from the specified file. The current Download data
	 * will be replaced.
	 * 
	 * @param file
	 */
	@SuppressWarnings("deprecation")
	public static void loaddownloadDataFromFile() {
		
		File file = MainApp.getConfigFile();
		try {
			JAXBContext context = JAXBContext
					.newInstance(DownloadModelListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			DownloadModelListWrapper wrapper = (DownloadModelListWrapper) um
					.unmarshal(file);

			downloadModelList.clear();
			downloadModelList.addAll(wrapper.getDownloads());
			 System.out.println("Program Status Loaded");
			// downloadData.get(0).getDownload().resume();

			// Save the file path to the registry.
			// setDownloadFilePath(file);

		} catch (Exception e) { // catches ANY exception
			/*
			 * Dialogs.create() .title("Error") .masthead(
			 * "Could not load data from file:\n" + file.getPath())
			 * .showException(e);
			 */
			//Dialogs.create().title("").masthead("").showTextInput("");
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the current Download data to the specified file.
	 * 
	 * @param file
	 */
	@SuppressWarnings("deprecation")
	public static void savedownloadDataToFile() {
		if(downloadModelList.size()==0){
			MainApp.resetDataFile();
		}
		File file = MainApp.getConfigFile();
		try {
			JAXBContext context = JAXBContext
					.newInstance(DownloadModelListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our Download data.
			DownloadModelListWrapper wrapper = new DownloadModelListWrapper();
			wrapper.setDownloads(downloadModelList);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);
			System.out.println("Program Status Saved");

		} catch (Exception e) { // catches ANY exception
			/*
			 * Dialogs.create() .title("Error")
			 * .masthead("Could not save data to file:\n" + file.getPath())
			 * .showException(e);
			 */
			System.out.println("Program Status not Saved .");
			e.printStackTrace();
		}
	}

	public void setListConfig() {
		int length = downloadModelList.size();
		for (int i = 0; i < length; i++) {
			String status = downloadModelList.get(i).getStatus();
			if (status == Status.Downloading.toString()) {
				downloadModelList.get(i).setStatus(Status.Paused.toString());
			} else
				continue;
		}
	}

	public void saveOnClose() {
		int length = downloadModelList.size();
		for (int i = 0; i < length; i++) {
			String status = downloadModelList.get(i).getStatus();
			if (status == Status.Downloading.toString()) {
				downloadModelList.get(i).getDownload().pause();
			} else
				continue;
		}
		if(downloadModelList.size()==0){
			MainApp.resetDataFile();
		}
	}

	public static File getConfigFile() {
		return Paths.get("configuration/xbluntdata.xml").toFile();
	}
	
	public static void resetDataFile(){
		getConfigFile().delete();
		try {
			getConfigFile().createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
