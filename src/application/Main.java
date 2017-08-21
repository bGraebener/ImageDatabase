package application;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mainWindow.controller.MainWindowController;
import utils.BasicOperations;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainWindow/view/MainWindow.fxml"));

			BorderPane root = loader.load();
			MainWindowController controller = loader.getController();

			Scene scene = new Scene(root);

			// FIXME refactor this
			controller.bottomSeparator.prefWidthProperty().bind(scene.widthProperty());
			controller.mainTableView.requestFocus();
			controller.setStage(primaryStage);

			primaryStage.setScene(scene);
			primaryStage.setTitle("Photo Organiser");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/res/icon.png")));

			primaryStage.show();

			primaryStage.setOnCloseRequest((event) -> {

//				List<Photo> serialiseList = new ArrayList<>(controller.getPhotoList());
				if (BasicOperations.closeApplication(new ArrayList<>(controller.getPhotoList()))) {

				} else {
					event.consume();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
