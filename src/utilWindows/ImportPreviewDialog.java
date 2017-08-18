package utilWindows;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import model.Photo;

public class ImportPreviewDialog {

	private Insets defaultPadding;
	private List<Photo> listOfPhotos;
	private Alert dialog;
	private TextField textField;

	public ImportPreviewDialog(List<File> listOfPhotos) {

		this.listOfPhotos = listOfPhotos.stream().map(file -> new Photo(file.toPath())).collect(Collectors.toList());
		defaultPadding = new Insets(15);

	}

	// FIXME doesn't work, stops after ok, no copy, no tags
	public List<String> showImportPhotosDialog() {
		createDialog();
		dialog.show();
		;

		String newTags = textField.getText();
		System.out.println("textfield");

		if (newTags != null && !newTags.replaceAll("\\s*", "").isEmpty()) {
		}
		System.out.println("newtags: " + newTags);

		return Arrays.asList(newTags.replaceAll("\\s*", "").split(","));

	}

	private void createDialog() {

		TilePane tiles = new TilePane();
		tiles.setPadding(defaultPadding);
		tiles.setHgap(15);
		tiles.setVgap(15);

		for (Photo photo : listOfPhotos) {
			ImageView imageView = new ImageView(photo.getThumbNail());
			tiles.getChildren().add(imageView);
		}

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToWidth(true);

		scrollPane.setContent(tiles);

		Label label = new Label(
				"Fügen sie neue Schlagwörter hinzu.\nSie können mehrere Schlagworte durch Komma trennen (z.B. Urlaub, Familie)");
		label.setPadding(defaultPadding);

		TextField textField = new TextField();

		VBox root = new VBox(scrollPane, label, textField);
		root.setId("secondPane");
		scrollPane.setPrefHeight(400);
		scrollPane.setPrefWidth(750);

		dialog = new Alert(AlertType.INFORMATION);
		dialog.setTitle("Fügen sie Schlagworte hinzu.");
		dialog.setHeaderText(null);

		dialog.getDialogPane().setExpandableContent(root);
		dialog.getDialogPane().setExpanded(true);

		dialog.initModality(Modality.APPLICATION_MODAL);

	}

}
