package utilWindows.wizard.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import utils.PropertiesInitialiser;

/**
 * A Class that constructs a Pane for the Import Photos Wizard. It manages the
 * View that lets the user choose the location of the files they wish to import.
 * 
 * @author Basti
 *
 */
public class LocationChooserWindow {

	private Insets defaultPadding = new Insets(15);
	private Pane mainContainer;
	private CheckBox skipPreviewCheckBox;

	private ObservableList<Path> listOfPaths;
	private ResourceBundle resources;
	private CheckBox skipExistingFilesCheckBox;

	public LocationChooserWindow(Pane mainContainer) {
		this.mainContainer = mainContainer;

		resources = PropertiesInitialiser.getResources();
	}

	public Pane getImagesLocationPane() {

		Label welcome = new Label(resources.getString("welcomeLabel"));
		welcome.setPadding(defaultPadding);

		listOfPaths = FXCollections.observableArrayList();

		HBox mainBox = new HBox(15);
		VBox buttonBox = new VBox(15);

		ListView<Path> filesListView = new ListView<>(listOfPaths);
		filesListView.setOnDragOver(event -> {

			Dragboard db = event.getDragboard();
			boolean allowed = false;

			allowed = db.getFiles().stream()
					.allMatch(file -> file.exists()
							&& (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png")
									|| file.getAbsolutePath().endsWith(".gif") || file.isDirectory()));

			if (allowed) {
				event.acceptTransferModes(TransferMode.COPY);
			}

		});

		filesListView.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				db.getFiles().forEach(file -> {
					listOfPaths.add(file.toPath());
				});
			}
		});

		filesListView.setPrefWidth(560);

		// HBox.setMargin(filesListView, new Insets(0, 0, 0, 15));

		Button browseDirBtn = new Button(resources.getString("browseDirButton"));
		browseDirBtn.setOnAction((event) -> {
			DirectoryChooser fileChooser = new DirectoryChooser();

			File directory = fileChooser.showDialog(null);

			// nothing chosen
			if (directory == null || !directory.exists()) {
				return;
			}

			listOfPaths.add(directory.toPath());

		});

		Button browseFilesBtn = new Button(resources.getString("browseFilesButton"));

		// action handler for files dialog
		browseFilesBtn.setOnAction((event) -> {
			FileChooser fileChooser = new FileChooser();
			ExtensionFilter filter = new ExtensionFilter("Image Files (*.jpg, *.png, *.gif)", "*.jpg", "*.png",
					"*.gif");
			fileChooser.getExtensionFilters().add(filter);

			List<File> listFromFilesDialog = fileChooser.showOpenMultipleDialog(null);

			// nothing chosen
			if (listFromFilesDialog == null || listFromFilesDialog.isEmpty()) {
				return;
			}

			listFromFilesDialog.stream().map(file -> file.toPath()).filter(path -> Files.exists(path))
					.forEach(listOfPaths::add);

		});

		buttonBox.getChildren().addAll(browseFilesBtn, browseDirBtn);
		mainBox.getChildren().addAll(filesListView, buttonBox);

		// ********************************************************************************
		// skip preview button
		skipPreviewCheckBox = new CheckBox(resources.getString("skipPreviewCheckBox"));

		skipExistingFilesCheckBox = new CheckBox(resources.getString("skipExistingCheckBox"));
		skipExistingFilesCheckBox.setSelected(true);
		// ********************************************************************************
		// root pane
		VBox root = new VBox(15, welcome, mainBox, skipPreviewCheckBox, skipExistingFilesCheckBox);
		VBox.setMargin(welcome, new Insets(0, 0, 0, 15));
		VBox.setMargin(mainBox, new Insets(0, 0, 0, 15));
		VBox.setMargin(skipPreviewCheckBox, new Insets(0, 0, 0, 15));
		VBox.setMargin(skipExistingFilesCheckBox, new Insets(0, 0, 0, 15));
		root.setId("firstPane");
		root.prefHeightProperty().bind(mainContainer.heightProperty().subtract(40));

		return root;
	}

	public List<Path> getListOfPaths() {
		List<Path> tmp = new ArrayList<>();

		listOfPaths.forEach(path -> {

			try {
				Files.walk(path).forEach(file -> {
					if (file.toAbsolutePath().toString().endsWith(".jpg")
							|| file.toAbsolutePath().toString().endsWith(".png")
							|| file.toAbsolutePath().toString().endsWith(".gif")) {

						if (skipExistingFilesCheckBox.isSelected()) {
							try {
								if (Files.walk(Paths.get(PropertiesInitialiser.getMainFolder()))
										.noneMatch(fileName -> fileName.getFileName().equals(file.getFileName()))) {
									tmp.add(file);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							tmp.add(file);
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		return tmp;
	}

	/**
	 * Getter for the skip preview selected property.
	 * 
	 * @return whether the CheckBox is selected or not
	 */
	public boolean skipPreview() {
		return skipPreviewCheckBox.isSelected();
	}

}
