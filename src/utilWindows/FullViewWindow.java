package utilWindows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mainWindow.controller.MainWindowController;
import model.Photo;

public class FullViewWindow {

	private TableView<Photo> mainTableView;
	private Stage newStage;
	private int startIndex;
	private MainWindowController mainWindowController;

	public FullViewWindow(MainWindowController mainWindowController) {
		this.mainWindowController = mainWindowController;
		
		this.mainTableView = mainWindowController.getMainTableView();
		
		startIndex = mainWindowController.getMainTableView().getSelectionModel().getSelectedIndex();
		
		initFullViewWindow();
	}

	// TODO resize image when window is resized
	private void initFullViewWindow() {
		
		BorderPane borderPane = new BorderPane();
		
		ImageView imageView = new ImageView(mainTableView.getSelectionModel().getSelectedItem().getImage());
		imageView.setStyle("-fx-background-color: BLACK");
		imageView.setFitHeight(mainTableView.getScene().getWindow().getHeight());
		imageView.setFitWidth(mainTableView.getScene().getWindow().getWidth());
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		imageView.setCache(true);
		
		VBox rightBox = new VBox();	
		Button nextImage = new Button();		
//		nextImage.setText("Weiter");
		nextImage.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/res/right-arrow.png"), 50, 0, true, true))); 
		rightBox.getChildren().add(nextImage);
		
		nextImage.setOnAction(event -> {
			startIndex = ++startIndex % (mainWindowController.getPhotoList().size());
			mainTableView.getSelectionModel().clearAndSelect(startIndex);
			imageView.setImage(mainTableView.getSelectionModel().getSelectedItem().getImage());
			newStage.setTitle(mainTableView.getSelectionModel().getSelectedItem().getPath().getFileName().toString());
			
		});
		
		VBox leftBox = new VBox();	
		Button prevImage = new Button();
//		prevImage.setText("Zurück");
		prevImage.setGraphic(new ImageView(new Image(FullViewWindow.class.getResourceAsStream("/res/left-arrow.png"), 50, 0, true, true))); 		
		leftBox.getChildren().add(prevImage);
		
		prevImage.setOnAction(event -> {
			--startIndex;
			startIndex = startIndex < 0 ? (mainWindowController.getPhotoList().size() - 1) : startIndex;
			mainTableView.getSelectionModel().clearAndSelect(startIndex);
			imageView.setImage(mainTableView.getSelectionModel().getSelectedItem().getImage());
			newStage.setTitle(mainTableView.getSelectionModel().getSelectedItem().getPath().getFileName().toString());
		});
		
		borderPane.setCenter(imageView);		
		borderPane.setLeft(leftBox);		
		borderPane.setRight(rightBox);
		borderPane.setStyle("-fx-background-color: BLACK");
		
		newStage = new Stage();
		newStage.setWidth(mainTableView.getScene().getWindow().getWidth()+200);
		newStage.setHeight(mainTableView.getScene().getWindow().getHeight());
		
		Scene scene = new Scene(borderPane, Color.BLACK);
		VBox.setMargin(nextImage, new Insets(newStage.getHeight()/2 -20, 5, 0, 0));
		VBox.setMargin(prevImage, new Insets(newStage.getHeight()/2 -20, 0, 0, 5));
		
		newStage.setScene(scene);
	}

	public void displayPhotoFullView() {
		
		newStage.show();
	}

}
