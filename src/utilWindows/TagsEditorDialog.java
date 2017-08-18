package utilWindows;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Photo;

//XXX existing tags don't have to be in listview, could be in label
//DONE add new tag to current tags list

/**
 * The dialog used to edit tags of already managed photos.
 * 
 * @author Basti
 *
 */
public class TagsEditorDialog {

	private TextField textField;
	private ListView<String> allCurrentTagsView;
	private Stage stage;

	private List<Photo> selectedPhotos;
	private List<String> newTagsFromTextField;
	private ObservableList<String> selectedPhotoTagsList;

	private ObservableList<String> allExistingTags;

	public TagsEditorDialog(List<Photo> selectedPhotos, List<String> filterList) {

		this.selectedPhotos = selectedPhotos;
		filterList.remove("Kein Filter");
		this.allExistingTags = FXCollections.observableArrayList(filterList);

		showTagsDialog();
	}

	/**
	 * Initialises the window for the TagsEditor.
	 */
	private Pane initialise() {
		AnchorPane root = new AnchorPane();

		VBox mainVBox = new VBox(15);
		mainVBox.setPrefHeight(600);
		mainVBox.setPrefWidth(407);
		mainVBox.setPadding(new Insets(10));

		Label title = new Label("Schlagworte editieren");

		Label selectedTagsLabel = new Label("Alle Schlagwörter der gewählten Photos");
		selectedTagsLabel.setPrefWidth(200);
		selectedTagsLabel.setWrapText(true);

		Label allCurrentLabel = new Label("Alle Schlagwörter");
		allCurrentLabel.setPrefWidth(200);

		HBox labelBox = new HBox(10);
		labelBox.setPrefHeight(150);
		labelBox.getChildren().addAll(selectedTagsLabel, allCurrentLabel);

		ListView<String> selectedPhotoTagsView = new ListView<>();
		selectedPhotoTagsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		allCurrentTagsView = new ListView<>();
		allCurrentTagsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		HBox listBox = new HBox(10);
		listBox.prefHeight(365.0);
		listBox.getChildren().addAll(selectedPhotoTagsView, allCurrentTagsView);

		Button removeBtn = new Button("Schlagwort(e) entfernen");
		removeBtn.setAlignment(Pos.CENTER_RIGHT);
		removeBtn.setTextAlignment(TextAlignment.CENTER);
		removeBtn.setOnAction((event) -> {
			// FIXME checke ob filter danach noch existieren
			// DONE remove tag implementieren
			List<String> selectedTags = selectedPhotoTagsView.getSelectionModel().getSelectedItems();

			selectedPhotos.forEach(photo -> photo.getTags().removeAll(selectedTags));

		});

		textField = new TextField();
		// textField.setPrefWidth(250);

		Label textFieldLabel = new Label(
				"Sie können mehrere Schlagwörter durch Komma trennen \n(z.B. Urlaub, Familie)");
		textFieldLabel.setWrapText(true);
		// textFieldLabel.setPrefWidth(150);
		textFieldLabel.setPrefHeight(150);

		HBox inputHBox = new HBox(10);
		inputHBox.setPrefHeight(200);
		inputHBox.setPrefWidth(200);
		inputHBox.getChildren().addAll(textField, textFieldLabel);

		Button addBtn = new Button("Schlagworte hinzufügen");
		addBtn.setOnAction((event) -> {
			addTags();
			stage.close();
		});

		mainVBox.getChildren().addAll(title, labelBox, listBox, removeBtn, textFieldLabel, textField, addBtn);

		root.getChildren().add(mainVBox);

		selectedPhotoTagsList = selectedPhotos.stream().flatMap(x -> x.getTags().stream()).distinct()
				.collect(Collectors.toCollection(FXCollections::observableArrayList));

		selectedPhotoTagsView.setItems(selectedPhotoTagsList);
		allCurrentTagsView.setItems(allExistingTags);

		return root;

	}

	/**
	 * Adds the new tags to photos in the main TableView and to the ListView in the
	 * editor window. Allows for input of multiple tags at once.
	 */
	private void addTags() {
		// DONE empty tags get added, why?
		newTagsFromTextField = Arrays.asList(textField.getText().replaceAll("\\s*", "").split(","));

		Set<String> tmp = new TreeSet<>(allCurrentTagsView.getSelectionModel().getSelectedItems());
		tmp.addAll(newTagsFromTextField);

		// Add tags to the selected photos in the main photolist if the tag is
		// not yet set for that photo
		selectedPhotos.forEach(photo -> {

			tmp.stream().filter(newTag -> !photo.getTags().contains(newTag) && !newTag.equals(""))
					.forEach(photo.getTags()::add);

		});

		// Add tags to ListView in the dialog window if they not yet exist
		tmp.stream().filter(newTag -> !selectedPhotoTagsList.contains(newTag)).forEach(selectedPhotoTagsList::add);

		Alert addedTagAlert = new Alert(AlertType.CONFIRMATION);
		addedTagAlert.setTitle("Schlagworte hinzugefügt");
		addedTagAlert.setHeaderText(null);
		addedTagAlert.setContentText("Hinzugefügte Schlagworte: " + tmp.toString().replaceAll("\\[|\\]", ""));
		addedTagAlert.show();

	}

	/**
	 * Displays the TagsEditorDialog and waits until it's closed before returning
	 * control.
	 */
	public void showTagsDialog() {

		Scene scene = new Scene(initialise());
		stage = new Stage();

		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER && !textField.getText().isEmpty()) {
					addTags();
					stage.close();
				}
			}
		});

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.setResizable(false);
		textField.requestFocus();

		stage.showAndWait();

	}

	/**
	 * Getter for the newly added tags.
	 * 
	 * @return List of new tags
	 */
	public List<String> getNewTags() {
		return newTagsFromTextField;
	}

}
