package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import model.Photo;

// DONE catch existing file exception
// XXX add renamed image to photolist
// DONE convert imported list to copied list with new pathnames
// DONE add file extensions of original file

/**
 * Utility class with static utility methods used in the application.
 * 
 * @author Basti
 *
 */
public class BasicOperations {

//	private static Properties properties = new Properties();
//
//	static {
//		try {
//			properties.load(new FileReader("res/config.properties"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Copies a File from its original location to the applications database
	 * folder. In case the photo already exists in the new location the user is
	 * asked whether to rename it or skip the copy process.
	 * 
	 * @param original
	 *            Photo with the original location
	 * @return Photo with new location
	 */
	public static Optional<Photo> copyFile(Photo original) {
		
		Properties properties = new Properties();
		try {
			properties.load(new FileReader("res/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Optional<Photo> possiblePhoto = Optional.empty();
		Path mainFolder = Paths.get(properties.getProperty("mainFolder"));
		Path fileName = original.getPath().getFileName();
	
		if (Files.exists(mainFolder.resolve(fileName))) {
	
			possiblePhoto = renameFile(original);
	
			if (possiblePhoto.isPresent()) {
				try {
					Files.copy(original.getPath(), possiblePhoto.get().getPath(), StandardCopyOption.COPY_ATTRIBUTES);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
		} else {
	
			try {
				Files.copy(original.getPath(), mainFolder.resolve(fileName), StandardCopyOption.COPY_ATTRIBUTES);
				
				possiblePhoto = Optional.of(new Photo(mainFolder.resolve(fileName)));
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}
		return possiblePhoto;
	}

	public static Optional<Photo> renameFile(Photo original) {
		
		Properties properties = new Properties();
		try {
			properties.load(new FileReader("res/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Optional<Photo> possiblePhoto = Optional.empty();
		Path mainFolder = Paths.get(properties.getProperty("mainFolder"));
		Path fileName = original.getPath().getFileName();
		String originalExtension = fileName.toString().substring(fileName.toString().lastIndexOf("."));
	
		Alert fileExistAlert = showConfirmationAlert("Achtung! Datei existiert am Zielort!",
				"Die Datei " + original.getPath().getFileName() + " exisitiert schon",
				"Wollen sie die Datei umbennen?");
		Optional<ButtonType> renameBtn = fileExistAlert.showAndWait();
	
		if (renameBtn.isPresent()) {
			if (renameBtn.get() == ButtonType.OK) {
				TextInputDialog renameDialog = BasicOperations.showTextInputDialog(null, null,
						"Geben sie einen neuen Dateinamen ein.\nDie Dateiendung wird automatisch hinzugefügt.");
				fileName = Paths.get(renameDialog.showAndWait().get());
	
				// check if user added file extension
				if (fileName.toString().lastIndexOf(".") < 0) {
					fileName = Paths.get(fileName + originalExtension);
				} else {
					System.out.println(fileName.getFileName().toString().substring(0,
							fileName.getFileName().toString().lastIndexOf(".")));
					fileName = Paths.get(fileName.getFileName().toString().substring(0,
							fileName.getFileName().toString().lastIndexOf(".")) + originalExtension);
				}
	
				possiblePhoto = Optional.of(new Photo(mainFolder.resolve(fileName)));
				// System.out.println(possiblePhoto.isPresent());
			}
	
		}
		return possiblePhoto;
	}

	/**
	 * After confirmation deletes the selected items from the main TableView and
	 * the filesystem.
	 * 
	 * @param markedItems
	 *            the list selected Photos
	 * @return
	 */
	public static boolean deleteItems(List<Photo> markedItems) {
		// DONE dateien loeschen implementieren
		boolean deleted = false;
		Alert alert = showConfirmationAlert("Dateien werden gelöscht",
				"Achtung! \nAlle Dateien werden unwiederbringlich aus der Datenbank und dem Speicherordner gelöscht!",
				"Sind sie sicher, dass Sie fortfahren wollen?");
		Optional<ButtonType> delete = alert.showAndWait();
	
		if (delete.isPresent() && delete.get() == ButtonType.OK) {
	
			markedItems.forEach(photo -> {
				try {
					Files.deleteIfExists(photo.getPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
	
			deleted = true;
		}
	
		return deleted;
	}

	/**
	 * Renames a selected file and updates the photoList.
	 * 
	 * @param selectedItem
	 *            the photo to be renamed
	 */
	public static Optional<Photo> movePhoto(Photo original) {
	
		Optional<Photo> possiblePhoto = Optional.empty();
		Path fileName = original.getPath().getFileName();
		String originalExtension = fileName.toString().substring(fileName.toString().lastIndexOf("."));
	
		TextInputDialog newNameInput = BasicOperations.showTextInputDialog("Datei umbennen",
				"Geben sie einen neuen Dateinamen ein. Die Dateiendung wird automatisch hinzugefügt.",
				"Neuer Dateiname:");
		Optional<String> newFileName = newNameInput.showAndWait();
	
		if (newFileName.isPresent() && newFileName.get() != null && !newFileName.get().equals("")) {
	
			String oldPath = original.getLocation().get().substring(0,
					original.getLocation().get().lastIndexOf(FileSystems.getDefault().getSeparator()));
	
			String newPath = oldPath + FileSystems.getDefault().getSeparator() + newFileName.get();
	
			//check that user didn't add an extension
			if (newFileName.get().lastIndexOf(".") < 0) {
				newPath = oldPath + FileSystems.getDefault().getSeparator() + newFileName.get() + originalExtension;
			} else {
				newPath = oldPath + FileSystems.getDefault().getSeparator()
						+ newFileName.get().substring(0, newFileName.get().lastIndexOf(".")) + originalExtension;
			}
	
			try {
				Files.move(original.getPath(), Paths.get(newPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			Photo newPhoto = new Photo(Paths.get(newPath));
			newPhoto.getTags().addAll(original.getTags());
			possiblePhoto = Optional.of(newPhoto);
		}
	
		return possiblePhoto;
	}

	

	/**
	 * Shows an error alert with the specified parameters.
	 * 
	 * @param title
	 *            the title to be displayed in the error dialog
	 * @param header
	 *            the header to be displayed in the information dialog
	 * @param content
	 *            the message to be displayed in the error dialog
	 * @return an Alert with the specified content
	 */
	public static Alert showErrorAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(header);
		alert.setTitle(title);
		alert.setContentText(content);
		return alert;
	}

	/**
	 * Shows an information alert with the specified parameters.
	 * 
	 * @param title
	 *            the title to be displayed in the information dialog
	 * @param header
	 *            the header to be displayed in the information dialog
	 * @param content
	 *            the message to be displayed in the information dialog
	 * @return an Alert with the specified content
	 */
	public static Alert showInformationAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}

	/**
	 * Shows an Confirmation alert with the specified parameters.
	 * 
	 * @param title
	 *            the title to be displayed in the Confirmation dialog
	 * @param header
	 *            the header to be displayed in the Confirmation dialog
	 * @param content
	 *            the message to be displayed in the Confirmation dialog
	 * @return an Alert with the specified content
	 */
	public static Alert showConfirmationAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}

	/**
	 * Shows an TextInputDialog with the specified parameters.
	 * 
	 * @param title
	 *            the title to be displayed in the TextInputDialog
	 * @param header
	 *            the header to be displayed in the TextInputDialog
	 * @param content
	 *            the message to be displayed in the TextInputDialog
	 * @return an Alert with the specified content
	 */
	public static TextInputDialog showTextInputDialog(String title, String header, String content) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		return dialog;
	}

	/**
	 * Gathers information about the selected photo file and creates a formated
	 * String. The String is used for the Tooltips in the main TableView cells.
	 * 
	 * @param photo
	 *            the selected photo
	 * @return A string with photo and file information
	 */
	public static String getPhotoInfo(Photo photo) {
		StringBuilder photoInfo = new StringBuilder();
		// DONE formatiere datum in photo info

		try {
			String lastModified = LocalDateTime
					.from(Files.getLastModifiedTime(photo.getPath()).toInstant().atZone(ZoneId.systemDefault()))
					.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"));

			String created = LocalDateTime.from(((FileTime) (Files.getAttribute(photo.getPath(), "creationTime")))
					.toInstant().atZone(ZoneId.systemDefault()))
					.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"));

			String fileSize = String.format("%.2f", Files.size(photo.getPath()) / 1_000_000f);

			photoInfo
					.append("Photoinformationen:" + "\n\nDateiname: " + photo.getPath().getFileName() + "\nDateigröße: "
							+ fileSize + " MB" + "\nDatei erstellt: " + created + "\nLetztes Mal bearbeitet: "
							+ lastModified + "\nSchlagworte: " + photo.getTags().toString().replaceAll("\\[|\\]", ""));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return photoInfo.toString();
	}

	public static List<String> getNewTags() {

		List<String> possibleTags = new ArrayList<>();

		TextInputDialog getNewTagsDialog = showTextInputDialog("Fügen sie Schlagworte hinzu.",
				"Sie können mehrere Schlagworte durch Komma trennen (z.B. Urlaub, Familie)", "Schlagworte: ");
		Optional<String> newTags = getNewTagsDialog.showAndWait();

		if (newTags.isPresent() && newTags.get() != null && !newTags.get().replaceAll("\\s*", "").isEmpty()) {
			possibleTags.addAll(Arrays.asList(newTags.get().replaceAll("\\s*", "").split(",")));
		}

		return possibleTags;
	}

	/**
	 * 
	 */
	public static String getMainFolder() {

		Properties properties = new Properties();
		try {
			properties.load(new FileReader("res/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String mainFolder = null;

		Alert getMainFolder = new Alert(AlertType.WARNING);
		getMainFolder.setTitle("Speicherort festlegen");
		getMainFolder.setHeaderText(
				"Bitte legen sie einen Ordner fest, der als Speicherort für alle Fotos dient! \nSie können diesen Ort später in den Einstellungen ändern.");
		getMainFolder.setGraphic(null);
		HBox hBox = new HBox(10);
		TextField folderTextField = new TextField();
		folderTextField.setPrefWidth(400);
		Button dirChooserBtn = new Button("Browse");
		dirChooserBtn.setOnAction((event) -> {
			DirectoryChooser dirChooser = new DirectoryChooser();

			File directory = dirChooser.showDialog(null);

			// nothing chosen
			if (directory == null || !directory.exists()) {
				return;
			}

			Path dirAsPath = directory.toPath();

			folderTextField.setText(dirAsPath.toAbsolutePath().toString().replaceAll("\\[|\\]", ""));
		});

		hBox.getChildren().addAll(folderTextField, dirChooserBtn);

		getMainFolder.getDialogPane().setExpandableContent(hBox);
		getMainFolder.getDialogPane().setExpanded(true);

		getMainFolder.showAndWait();
		if (folderTextField.getText() != null && !folderTextField.getText().isEmpty()) {
			mainFolder = folderTextField.getText();
			properties.setProperty("mainFolder", folderTextField.getText());
			try {
				properties.store(new PrintWriter("res/config.properties"), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			getMainFolder();
		}

		return mainFolder;
	}

}
