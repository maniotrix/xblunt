package maniotrix.xblunt.transporter;

import java.io.IOException;

import maniotrix.xblunt.transporter.model.DownloadModel;
import maniotrix.xblunt.transporter.view.DownloadOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    /**
     * The data as an observable list of downloads.
     */
    private ObservableList<DownloadModel> downloadData = FXCollections.observableArrayList();
    
    /**
     * Constructor
     */
    public MainApp() {
        MainApp.useProxySocks_v5();
       
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");

        initRootLayout();

        showdownloadOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the Download overview inside the root layout.
     */
    public void showdownloadOverview() {
        try {
            // Load Download overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/DownloadOverview.fxml"));
            AnchorPane downloadOverview = (AnchorPane) loader.load();

            // Set Download overview into the center of root layout.
            rootLayout.setCenter(downloadOverview);
            
         // Give the controller access to the main app.
            DownloadOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    /**
     * Returns the data as an observable list of downloads. 
     * @return
     */
    public ObservableList<DownloadModel> getdownloadData() {
        return downloadData;
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
}
