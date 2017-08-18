package utilWindows.wizard.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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
	private List<File> listFromFilesDialog;
	private CheckBox skipPreviewCheckBox;
	private ToggleGroup tGroup;
	private RadioButton filesRadioButton, dirRadioButton;
	private Path dirAsPath;

	public LocationChooserWindow(Pane mainContainer) {
		this.mainContainer = mainContainer;
		
	}

	public Pane getImagesLocationPane() {

		Label welcome = new Label(
				"Willkommen zum Photo Organiser Wizard!\n\nHier können sie einzelne oder mehrere Dateien "
						+ "oder ganze Ordner auswählen die sie importieren möchten!");
		welcome.setPadding(defaultPadding);

		tGroup = new ToggleGroup();

		HBox firstHBox = createFilesHBox();

		HBox secondHBox = createDirectoryHBox();

		// ********************************************************************************
		// skip preview button
		skipPreviewCheckBox = new CheckBox(
				"Vorschau überspringen (importiert alle im Ordner befindlichen Dateien und Unterordner!)");
		skipPreviewCheckBox.setPadding(defaultPadding);

		// ********************************************************************************
		// root pane
		VBox root = new VBox(15, welcome, firstHBox, secondHBox, skipPreviewCheckBox);
		root.setId("firstPane");
		root.prefHeightProperty().bind(mainContainer.heightProperty().subtract(40));

		return root;
	}

	/**
	 * Creates a HBox Container that contains elements used to get the location
	 * for a folder of images.
	 * 
	 * @return the created HBox
	 */
	private HBox createDirectoryHBox() {
		dirRadioButton = new RadioButton();

		TextField dirTextField = new TextField();
		dirTextField.setPromptText("Wähle Ordner aus...");
		dirTextField.setPrefWidth(450);
		dirTextField.setDisable(true);

		Button browseDirBtn = new Button("Ordner auswählen");
		browseDirBtn.setDisable(true);

		browseDirBtn.setOnAction((event) -> {
			DirectoryChooser fileChooser = new DirectoryChooser();
			// fileChooser.setInitialDirectory(
			// // FIXME change before deploy
			// new File("C:\\D-Drive\\Fotos\\2015.07.18 (Hochzeit von Basti und
			// Orla) Germany\\sJPG\\4-Star\\"));

			File directory = fileChooser.showDialog(null);

			// nothing chosen
			if (directory == null || !directory.exists()) {
				return;
			}

			dirAsPath = directory.toPath();
			// System.out.println(dirAsPath);
			dirTextField.setText(dirAsPath.toAbsolutePath().toString());
			

		});

		dirRadioButton.setToggleGroup(tGroup);
		dirRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
			browseDirBtn.setDisable(!newValue);
			dirTextField.setDisable(!newValue);
			dirTextField.setText("");
		});

		HBox secondHBox = new HBox(10, dirRadioButton, dirTextField, browseDirBtn);
		secondHBox.setPadding(defaultPadding);
		return secondHBox;
	}

	/**
	 * Creates a HBox Container that contains elements used to get the location
	 * for individual file or files.
	 * 
	 * @return the created HBox
	 */
	private HBox createFilesHBox() {
		// ********************************************************************************
		// files chooser hbox

		filesRadioButton = new RadioButton();
		filesRadioButton.setSelected(true);

		TextField filesTextField = new TextField();
		filesTextField.setPromptText("Wähle Dateien aus...");
		filesTextField.setPrefWidth(450);

		Button browseFilesBtn = new Button("Dateien auswählen");

		// action handler for files dialog
		browseFilesBtn.setOnAction((event) -> {
			FileChooser fileChooser = new FileChooser();
			ExtensionFilter filter = new ExtensionFilter("Image Files (*.jpg, *.png, *.gif)", "*.jpg", "*.png",
					"*.gif");
			fileChooser.getExtensionFilters().add(filter);

			listFromFilesDialog = fileChooser.showOpenMultipleDialog(null);

			// nothing chosen
			if (listFromFilesDialog == null || listFromFilesDialog.isEmpty()) {
				return;
			}

			filesTextField.setText(listFromFilesDialog.toString().replaceAll("\\[|\\]", ""));
		});

		filesRadioButton.setToggleGroup(tGroup);
		filesRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
			browseFilesBtn.setDisable(!newValue);
			filesTextField.setDisable(!newValue);
			filesTextField.setText("");
		});

		HBox firstHBox = new HBox(10, filesRadioButton, filesTextField, browseFilesBtn);
		firstHBox.setPadding(defaultPadding);

		return firstHBox;
	}

	/**
	 * Utility method that walks the file system to populate a List of files
	 * ending in .jpg.
	 * 
	 * @param dirAsPath
	 *            the Path to start
	 */
	private void addFilesToList(Path dirAsPath, List<File> listOfFiles) {
		try {
			Files.walkFileTree(dirAsPath, new SimpleFileVisitor<Path>() {
	
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
	
					File imageFile = file.toFile();
	
					// DONE add all supported files
					if (imageFile.getAbsolutePath().endsWith(".jpg") || imageFile.getAbsolutePath().endsWith(".png")
							|| imageFile.getAbsolutePath().endsWith(".gif")) {
						listOfFiles.add(imageFile);
					}
	
					return FileVisitResult.CONTINUE;
				}
	
			});
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter for the List of files the user whishes to import
	 * 
	 * @return
	 */
	public List<File> getListOfFiles() {
		
		List<File> listOfFiles = new ArrayList<>();
		
		if(filesRadioButton.isSelected()){
			listOfFiles = listFromFilesDialog;
		}else{
			if(dirAsPath != null && Files.exists(dirAsPath)){
			addFilesToList(dirAsPath, listOfFiles);}
		}
		
		return listOfFiles;
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
