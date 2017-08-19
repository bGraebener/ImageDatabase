package application;
	
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mainWindow.controller.MainWindowController;
import model.Photo;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainWindow/view/MainWindow.fxml"));		
			
			BorderPane root = loader.load();		
			MainWindowController controller = loader.getController();
			
			Scene scene = new Scene(root);			
			
			//FIXME refactor this
			controller.bottomSeparator.prefWidthProperty().bind(scene.widthProperty());
			controller.mainTableView.requestFocus();
			controller.setStage(primaryStage);
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("Photo Organiser");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/res/icon.png")));
			
			
			primaryStage.show();
			
			primaryStage.setOnCloseRequest((event) -> {
				System.out.println("saving database");
				
				List<Photo> serialiseList = new ArrayList<>(controller.getPhotoList());
				
				try (ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream("res/photolist.ser"))){
					
					outStream.writeObject(serialiseList);
					outStream.flush();
					
				}catch (IOException e){
					
				}
				
			});
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
