package utilWindows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mainWindow.controller.MainWindowController;
import model.Photo;

//XXX Dimensionen stimmen nicht

/**
 * 
 * @author Basti
 *
 */
public class FullViewWindow {

	private TableView<Photo> mainTableView;
	private Stage newStage;
	private Scene scene;
	private int startIndex;
	private MainWindowController mainWindowController;
	private ImageView imageView;

	public FullViewWindow(MainWindowController mainWindowController) {
		this.mainWindowController = mainWindowController;

		this.mainTableView = mainWindowController.getMainTableView();

		startIndex = mainWindowController.getMainTableView().getSelectionModel().getSelectedIndex();

		initFullViewWindow();
	}

	// DONE resize image when window is resized
	private void initFullViewWindow() {

		BorderPane borderPane = new BorderPane();
		
		scene = new Scene(borderPane, Color.BLACK);
		imageView = new ImageView(mainTableView.getSelectionModel().getSelectedItem().getImage());

		scene.setOnScroll(event -> {
			if (event.getDeltaY() > 30) {
				previousImage();
			} else if (event.getDeltaY() < -30) {
				nextImage();
			}
		});

		imageView.setStyle("-fx-background-color: BLACK");
		imageView.fitHeightProperty().bind(scene.heightProperty().subtract(80));
		imageView.fitWidthProperty().bind(scene.widthProperty());
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		imageView.setCache(true);

		Button nextImageBtn = new Button();
		nextImageBtn.setGraphic(
				new ImageView(new Image(getClass().getResourceAsStream("/res/right-arrow.png"), 35, 0, true, true)));
		nextImageBtn.setOnAction(event -> nextImage());


		Button prevImageBtn = new Button();
		prevImageBtn.setGraphic(new ImageView(
				new Image(FullViewWindow.class.getResourceAsStream("/res/left-arrow.png"), 35, 0, true, true)));
		prevImageBtn.setOnAction(event -> previousImage());

		HBox buttons = new HBox(15);
		buttons.getChildren().addAll(prevImageBtn, nextImageBtn);
		buttons.setAlignment(Pos.CENTER);

		borderPane.setCenter(imageView);
		borderPane.setBottom(buttons);
		borderPane.setStyle("-fx-background-color: BLACK");

		newStage = new Stage();
		newStage.setWidth(mainTableView.getScene().getWindow().getWidth());
		newStage.setHeight(mainTableView.getScene().getWindow().getHeight());
		
		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE){
				newStage.close();
			}else if(event.getCode() == KeyCode.RIGHT){
				nextImage();
			}else if(event.getCode() == KeyCode.LEFT){
				previousImage();
			}
			
		});

		HBox.setMargin(nextImageBtn, new Insets(0, 0, 15, 0));
		HBox.setMargin(prevImageBtn, new Insets(0, 0, 15, 0));

		newStage.setScene(scene);
	}

	private void previousImage() {
		--startIndex;
		startIndex = startIndex < 0 ? (mainWindowController.getPhotoList().size() - 1) : startIndex;
		mainTableView.getSelectionModel().clearAndSelect(startIndex);
		imageView.setImage(mainTableView.getSelectionModel().getSelectedItem().getImage());
		newStage.setTitle(mainTableView.getSelectionModel().getSelectedItem().getPath().getFileName().toString());
	}

	private void nextImage() {
		startIndex = ++startIndex % (mainWindowController.getPhotoList().size());
		mainTableView.getSelectionModel().clearAndSelect(startIndex);
		imageView.setImage(mainTableView.getSelectionModel().getSelectedItem().getImage());
		newStage.setTitle(mainTableView.getSelectionModel().getSelectedItem().getPath().getFileName().toString());
	}

	public void displayPhotoFullView() {

		newStage.show();
	}

}
