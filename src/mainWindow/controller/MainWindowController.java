package mainWindow.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Photo;
import utilWindows.SettingsWindow;
import utilWindows.wizard.controller.WizardController;
import utils.BasicOperations;
import utils.FilterListViewInitialiser;
import utils.MainTableViewInitialiser;
import utils.PropertiesInitialiser;

//TODO Einstellungen, Settings und Preferences
//TODO Kommentieren
//TODO Ordenerstruktur erhalten? Ordnername als Schlagwort
//TODO complete settings window 
//TODO externe plugins aufrufen
//TODO finish localizsation (tablecolumn headers, labels) 


//DONE mehr tablecolumns, (dateiname, ordner, schlagwoerter, hinzugefuegt)
//DONE implement exif-data display
//DONE Dateinamen im Full Viwe WIndow aendern
//DONE Suchfeld für (Schlagwörter) und Dateinamen 
//DONE setze speicherort fuer photos
//DONE Infozeile am unteren Ende des mainWindow (wie im windows explorer) 
//DONE Persistenz implementieren
//DONE Drag and drop copy files 
//DONE kopiere dateien beim importieren
//DONE Schlagwörter vorschlagen
//DONE Dateinamen ändern können
//DONE Drag and drop fuer dateien in dateien liste
//DONE filtern implementieren
//DONE Contextmenu aktualisieren
//DONE Drag and Drop fuer Schlagwoerter
//DONE anzahl von Bildern anzeigen
//DONE filterliste automatisch updaten

//XXX add directory change listener (need to restart!)
//XXX ändere main tableview zu listview?
//XXX mehr tooltips (bei hover over thumbnail)
//XXX add video support
//XXX manuell neuen Filter erstellen (unnoetig)
//XXX kann filter liste ein set sein?
//XXX Schlagwortliste unter Vorschaubild automatisch updaten (Bindings!!!) 

/**
 * Class that controls the interactions with user input on the main application
 * window.
 * 
 * @author Basti
 *
 */
public class MainWindowController implements Initializable {

	@FXML
	private ImageView thumbnailView;
	@FXML
	public TableView<Photo> mainTableView;
	@FXML
	private ListView<String> filterListView;
	@FXML
	private Label numOfFilesLabel, mainFolderLabel, pictureInfoLabel,  previousSearchLabel;
	@FXML
	private TableColumn<Photo, String> tagsColumn, fileNameColumn, addedColumn;
	@FXML
	private CheckBox filterExclusiv;
	@FXML
	public Separator bottomSeparator;
	@FXML
	private TextField searchFileTextField;
	@FXML
	private Button searchFileBtn, importToolBarButton;
	@FXML
	private ComboBox<String> prevSearchComboBox;
	@FXML
	private MenuItem importPhotosMenuItem, openSettingsMenuItem, closeMenuItem;

	private Stage stage;
	private ObservableList<String> filterList;
	private ObservableList<Photo> photoList;
	private String mainFolder;
	private ObservableList<String> prevSearchList;
	private FilteredList<Photo> filteredPhotosList;
	
	private ResourceBundle resources; 

	/**
	 * Method starts by loading the the config file. It then retrieves the saved
	 * photo list and generates the filter list from all items in the list of
	 * photos. The photo list is then used to populate the main table view.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		this.resources = resources;

		mainFolder = PropertiesInitialiser.getMainFolder();

		if (mainFolder == null || mainFolder.isEmpty() || !Files.exists(Paths.get(mainFolder))) {
			mainFolder = BasicOperations.getMainFolder();

		}

		// populate Photo List
		photoList = initialisePhotoList();

		// initialise filterlist and FilterListView
		FilterListViewInitialiser flvi = new FilterListViewInitialiser(this);
		filterList = flvi.initFilterListView();

		// initialise the TableView that holds the Photos
		MainTableViewInitialiser mtvi = new MainTableViewInitialiser(this);
		mtvi.initMainTableView(photoList);
		
		filteredPhotosList = mtvi.getFilteredPhotosList();
		SortedList<Photo> sortedList = new SortedList<>(filteredPhotosList);
		sortedList.comparatorProperty().bind(mainTableView.comparatorProperty());
		mainTableView.setItems(sortedList);
		

		numOfFilesLabel.textProperty()
		.bind((Bindings.concat(resources.getString("numOfFilesLabel"), Bindings.size(sortedList).asString())));
		mainFolderLabel.textProperty().bind(Bindings.format("%s %s", resources.getString("mainFolderLabel") ,PropertiesInitialiser.getMainFolder()));

		// bottomSeparator.prefWidthProperty().bind(bottomSeparator.getParent().getScene().widthProperty());

		mainTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// search for files
		prevSearchList = FXCollections.observableArrayList();

		searchFileTextField.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				searchPhoto(searchFileTextField.getText());
				prevSearchList.add(searchFileTextField.getText());
				prevSearchComboBox.getSelectionModel().select(searchFileTextField.getText());
			}
		});

		searchFileBtn.setOnAction(event -> {
			searchPhoto(searchFileTextField.getText());
			prevSearchList.add(searchFileTextField.getText());
			prevSearchComboBox.getSelectionModel().select(searchFileTextField.getText());
		});

		prevSearchComboBox.setItems(prevSearchList);
		prevSearchComboBox.setOnAction(event -> {
			searchPhoto(prevSearchComboBox.getSelectionModel().getSelectedItem());
		});

		importPhotosMenuItem
				.setAccelerator(KeyCombination.keyCombination(PropertiesInitialiser.getImportShortCutModifier() + "+"
						+ PropertiesInitialiser.getImportShortCutKeyCode()));

		openSettingsMenuItem.setText(resources.getString("openSettingsMenuItem"));
		importPhotosMenuItem.setText(resources.getString("importPhotosMenuItem"));
		closeMenuItem.setText(resources.getString("closeMenuItem"));
		importToolBarButton.setText(resources.getString("importPhotosMenuItem"));
		searchFileTextField.setPromptText(resources.getString("searchFileTextField"));
		searchFileBtn.setText(resources.getString("searchFileBtn"));
		previousSearchLabel.setText(resources.getString("previousSearchLabel"));
		
	}

	private void searchPhoto(String userSearch) {

		if (userSearch != null && !userSearch.isEmpty()) {
			mainTableView.getSelectionModel().clearSelection();
			photoList.stream().filter(
					photo -> photo.getPath().getFileName().toString().toLowerCase().contains(userSearch.toLowerCase()))
					.forEach(photo -> {
						mainTableView.getSelectionModel().select(photo);
						mainTableView.scrollTo(mainTableView.getSelectionModel().getSelectedItem());
						mainTableView.requestFocus();
					});
			;
		}
	}

	/**
	 * EventHandler for the moveItemUpBtn. Moves and selects the selected item
	 * in the filterlist up if it's not top of the list already.
	 */
	@FXML
	private void moveItemUp() {

		int selectedIndex = filterListView.getSelectionModel().getSelectedIndex();

		if (selectedIndex > 0) {
			filterList.add(selectedIndex - 1, filterList.remove(selectedIndex));
			filterListView.getSelectionModel().clearAndSelect(selectedIndex - 1);
		}
	}

	/**
	 * EventHandler for the moveItemDownBtn. Moves and selects the selected item
	 * in the filterlist down if it's not at the bottom of the list already.
	 */
	@FXML
	private void moveItemDown() {
		int selectedIndex = filterListView.getSelectionModel().getSelectedIndex();

		if (selectedIndex + 1 < filterList.size()) {
			filterList.add(selectedIndex + 1, filterList.remove(selectedIndex));
			filterListView.getSelectionModel().clearAndSelect(selectedIndex + 1);
		}
	}

	/**
	 * Gathers all photos from the predefined source location and collects it in
	 * an ObservableList to be displayed in the main TableView.
	 * 
	 * @return the ObservableList of Photos
	 */
	private ObservableList<Photo> initialisePhotoList() {

		ObservableList<Photo> returnList = null;

		// deserialize file
		try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream("res/photolist.ser"))) {

			@SuppressWarnings("unchecked")
			List<Photo> photoListFromFile = (ArrayList<Photo>) inStream.readObject();

			List<Photo> removedPhotoList = new ArrayList<>(photoListFromFile);
			// remove deleted files
			// DONE inform about removed files
			photoListFromFile
					.removeIf(photo -> !Files.exists(Paths.get(mainFolder).resolve(photo.getPath().getFileName())));

			removedPhotoList.removeAll(photoListFromFile);

			if (removedPhotoList.size() > 0) {
				StringBuilder removedFileNames = new StringBuilder();
				removedPhotoList.forEach(photo -> removedFileNames.append(photo.getPath().getFileName() + "\n"));
				Alert alert = BasicOperations.showInformationAlert(resources.getString("removedPhotosAlertTitle"),
						resources.getString("removedPhotosAlertHeader"),
						null);
				
				TextArea removedText = new TextArea(removedFileNames.toString());
				removedText.setWrapText(true);
				removedText.setEditable(false);
				alert.getDialogPane().setExpandableContent(removedText);		
				alert.getDialogPane().setExpanded(true);
				alert.showAndWait();
			}
			// add new Files from system
			Path mainFolder = Paths.get(this.mainFolder);
			try {
				Files.walk(mainFolder).forEach(file -> {

					String fileName = file.getFileName().toString();

					if (!Files.isDirectory(file)
							&& (fileName.endsWith(".jpg") || fileName.endsWith(".gif") || fileName.endsWith(".png"))) {
						Photo newPhoto = new Photo(file);

						// XXX show picture in question
						// ask for tags if photos aren't in the database yet
						if (!photoListFromFile.contains(newPhoto)) {

							photoListFromFile.add(newPhoto);
						}
					}
				});

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			returnList = photoListFromFile.stream().distinct().collect(Collectors.toCollection(FXCollections::observableArrayList));

		} catch (IOException | ClassNotFoundException e) {
			e.getStackTrace();
		}
		return returnList;

	}

	// DONE duplication when importing happening here
	// XXX Allow importing same photos multiple times after confirmation?
	// (solution: forced rename)
	/**
	 * EventHandler for the Import Photos Button. Opens the wizard to import
	 * photos. It retrieves the selected photos and the tags from the Wizard
	 * controller. It filters the tag- and imported photo list for duplicates
	 * and adds the rest to the main photolist and to the filter list.
	 */
	@FXML
	private void importPhotos() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilWindows/wizard/view/WizardMain.fxml"));

		VBox root = null;

		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		WizardController controller = loader.getController();
		Stage stage = new Stage();

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle(resources.getString("importWizardStageTitle"));
		stage.initModality(Modality.APPLICATION_MODAL);

		controller.setCurrentStage(stage);
		controller.setFilterList(new ArrayList<String>(filterList));
		controller.getNextBtn().requestFocus();

		stage.showAndWait();

		if (controller.getCopyList() != null) {
			controller.getCopyList().stream().filter(photo -> !photoList.contains(photo)).forEach(photoList::add);
		}
		if (controller.getTagsList() != null) {
			controller.getTagsList().stream().filter(tag -> !filterList.contains(tag)).forEach(filterList::add);
		}

		filterListView.getSelectionModel().clearAndSelect(0);
		mainTableView.getSelectionModel().clearAndSelect(0);
		mainTableView.requestFocus();
	}

	@FXML
	private void openSettingsWindow() {
		SettingsWindow settings = new SettingsWindow();
		settings.showSettings();
	}

	@FXML
	private void close() {
		List<Photo> serialiseList = new ArrayList<>(getPhotoList());

		if (BasicOperations.closeApplication(serialiseList)) {

			stage.close();
		}
	}

	public TableView<Photo> getMainTableView() {
		return mainTableView;
	}

	public ImageView getThumbnailView() {
		return thumbnailView;
	}

	public TableColumn<Photo, String> getFileNameColumn() {
		return fileNameColumn;
	}

	public TableColumn<Photo, String> getTagsColumn() {
		return tagsColumn;
	}

	public TableColumn<Photo, String> getAddedColumn() {
		return addedColumn;
	}

	public ListView<String> getFilterListView() {
		return filterListView;
	}

	public ObservableList<String> getFilterList() {
		return filterList;
	}

	public ObservableList<Photo> getPhotoList() {
		return photoList;
	}

	public Label getPictureInfoLabel() {
		return pictureInfoLabel;
	}

	public FilteredList<Photo> getFilteredPhotosList() {
		return filteredPhotosList;
	}

	public boolean isFilterExclusive() {
		return filterExclusiv.isSelected();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
