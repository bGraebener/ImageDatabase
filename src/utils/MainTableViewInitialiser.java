package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import mainWindow.controller.MainWindowController;
import model.Photo;
import utilWindows.EXIFWindow;
import utilWindows.FullViewWindow;
import utilWindows.TagsEditorDialog;

// DONE get new name dialog
// DONE somewhere the old filename is still stored and can't be compared anymore (toRealPath in photo::getLocation was throwing error)

/**
 * Class that initialises the TableView that holds the information on the
 * managed Photos.
 * 
 * @author Basti
 *
 */
public class MainTableViewInitialiser {

	private TableView<Photo> mainTableView;
	private MainWindowController mainWindowController;
	private DataFormat customDataFormat;
	private Tooltip tooltip;
	private SortedList<Photo> sortedList;

	// private FullViewWindow = new FullViewWindow(new
	// ormat("7295847979775465822L"), new Tooltip());

	public MainTableViewInitialiser(MainWindowController mainWindowController) {

		this.mainWindowController = mainWindowController;

		mainTableView = mainWindowController.getMainTableView();

		customDataFormat = new DataFormat("7295847979775465822L");

	}

	/**
	 * Initialises the TableView that holds the list of photo. A listener is
	 * attached to the main table that manages the displaying of the thumbnail of
	 * the selected item . If multiple photos are selected, no thumbnail is
	 * displayed. A DoubleClickHandler creates a new window that displays the
	 * selected picture in full view mode. Every cell of the TableView detects a
	 * mouse hover event and attaches a Tooltip with information about the photo it
	 * contains to itself.
	 * 
	 * @param photoList
	 * 
	 * @param sortedList
	 *            the sorted list wrapper for the photo list
	 */
	public void initMainTableView(ObservableList<Photo> photoList) {

		FilteredList<Photo> filteredPhotosList = getFilteredPhotosList();
		tooltip = new Tooltip();
		tooltip.setStyle("-fx-font-size: 10pt;");

		sortedList = new SortedList<>(filteredPhotosList);
		sortedList.comparatorProperty().bind(mainTableView.comparatorProperty());

		mainTableView.setItems(sortedList);
		mainTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		mainTableView.setContextMenu(createMainTableContextMenu());
		mainTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			// multiple photos selected --> no thumbnail, no file info
			if (mainTableView.getSelectionModel().getSelectedIndices().size() > 1) {
				mainWindowController.getThumbnailView().setImage(null);
				mainWindowController.getPictureInfoLabel().setText(null);
				return;
			}

			if (newValue != null) {
				mainWindowController.getThumbnailView().setImage(newValue.getImage());
				mainWindowController.getPictureInfoLabel().setText(BasicOperations.getPhotoInfo(newValue));
			}
		});

		mainTableView.getSelectionModel().select(0);

		// drag detected in the files list for adding new tags
		mainTableView.setOnDragDetected(me -> {
			Dragboard dragboard = mainTableView.startDragAndDrop(TransferMode.ANY);
			ClipboardContent cbc = new ClipboardContent();
			cbc.put(customDataFormat, new ArrayList<Photo>(mainTableView.getSelectionModel().getSelectedItems()));
			dragboard.setContent(cbc);
			me.consume();
		});

		// drag on detected in the files list for adding new files from system
		// explorer
		mainTableView.setOnDragOver(de -> {
			Dragboard db = de.getDragboard();

			if (db.hasFiles() && db.getFiles().stream().allMatch(file -> file.getName().endsWith(".jpg"))) {
				de.acceptTransferModes(TransferMode.COPY);
			}
			de.consume();
		});

		mainTableView.setOnDragDropped(de -> {
			Dragboard db = de.getDragboard();

			boolean success = false;
			if (db.hasFiles()) {
				// DONE copy actual files and allow for tags to be set
				// TODO show pictures in question
				// ImportPreviewDialog ipd = new ImportPreviewDialog(db.getFiles());
				// List<String> newTags = ipd.showImportPhotosDialog();

				List<String> newTags = BasicOperations.getNewTags();
				db.getFiles().stream().filter(file -> file.exists()).forEach(droppedPhoto -> {

					Optional<Photo> possiblePhoto = BasicOperations.copyFile(new Photo(droppedPhoto.toPath()));

					if (possiblePhoto.isPresent()) {

						possiblePhoto.get().getTags().addAll(newTags);
						photoList.add(possiblePhoto.get());

						newTags.stream().filter(tag -> !mainWindowController.getFilterList().contains(tag))
								.forEach(mainWindowController.getFilterList()::add);
					}

				});
				success = true;
			}

			de.setDropCompleted(success);
			de.consume();
		});

		// populate the filename column
		mainWindowController.getFileNameColumn().setCellValueFactory(cellData -> cellData.getValue().getFileName());
		mainWindowController.getFileNameColumn().setCellFactory(column -> {

			TableCell<Photo, String> cell = new TableCell<Photo, String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);

					if (!empty && item != null && !item.isEmpty()) {
						setText(item);
					} else {
						setText(null);
					}
				}
			};

			// show tooltip on mouse hover
			cell.setOnMouseEntered(me -> {
				if (cell != null && (Photo) cell.getTableRow().getItem() != null && cell.getText() != null) {
					cell.setTooltip(tooltip);
					tooltip.setText(BasicOperations.getPhotoInfo((Photo) cell.getTableRow().getItem()));
				}
			});

			// DONE Full View implementieren, move to cell factory
			// DONE refactor full view mode
			// DONE set image dimensions to max screen
			cell.setOnMouseClicked(me -> {
				if (me.getClickCount() == 2) {

					FullViewWindow fullViewWindow = new FullViewWindow(mainWindowController);
					fullViewWindow.displayPhotoFullView();

				}
			});

			return cell;
		});

		mainWindowController.getTagsColumn()
				.setCellValueFactory(cellData -> cellData.getValue().getTagsAsObservableProperty());

		mainWindowController.getAddedColumn().setCellValueFactory(cellData -> cellData.getValue().getAddedProperty());
	}

	// DONE add show in system explorer
	/**
	 * Creates the ContextMenu that is attached to main TableView.
	 * 
	 * @return the created ContextMenu
	 */
	private ContextMenu createMainTableContextMenu() {

		MenuItem editTags = new MenuItem("Bearbeite Schlagworte");
		editTags.setOnAction(x -> {

			int initialIndex = mainTableView.getSelectionModel().getSelectedIndex();
			
			TagsEditorDialog tagsEditor = new TagsEditorDialog(mainTableView.getSelectionModel().getSelectedItems(),
					new ArrayList<String>(mainWindowController.getFilterList()));
			List<String> newTags = tagsEditor.getNewTags();

			if (newTags != null) {

				newTags.stream().filter(newTag -> !mainWindowController.getFilterList().contains(newTag) && !newTag.equals(""))
						.forEach(mainWindowController.getFilterList()::add);
			}

			//cheat to update the tagsList and make the changes visible
			mainWindowController.getFilterListView().getSelectionModel().clearAndSelect(1);
			mainWindowController.getFilterListView().getSelectionModel().clearAndSelect(0);
			mainTableView.getSelectionModel().clearAndSelect(initialIndex);
		});

		MenuItem deleteItems = new MenuItem("Markierte Dateien löschen");
		deleteItems.setOnAction((x) -> {
			List<Photo> itemsToDelete = mainTableView.getSelectionModel().getSelectedItems();
			boolean deleted = BasicOperations.deleteItems(mainTableView.getSelectionModel().getSelectedItems());
			if (deleted) {
				mainWindowController.getPhotoList().removeAll(itemsToDelete);
			}
		});

		MenuItem showExifData = new MenuItem("Photo Informationen anzeigen");
		showExifData.setOnAction(x -> {
			if (mainTableView.getSelectionModel().getSelectedItems().size() > 1) {
				Alert alert = BasicOperations.showErrorAlert("Multiple Dateien ausgewählt", null,
						"Sie können nur Informationen für einzelne Dateien anzeigen.");
				alert.showAndWait();
				return;
			}
			EXIFWindow exifWindow = new EXIFWindow(mainTableView.getSelectionModel().getSelectedItem());
			exifWindow.showExifWindow();
			
//			BasicOperations.showExifData(mainTableView.getSelectionModel().getSelectedItems());
		});

		MenuItem renameFile = new MenuItem("Datei umbenennen");
		renameFile.setOnAction(x -> {
			if (mainTableView.getSelectionModel().getSelectedItems().size() > 1) {
				Alert alert = BasicOperations.showErrorAlert("Multiple Dateien ausgewählt", null,
						"Sie können nur einzelne Dateien umbennen.");
				alert.showAndWait();
				return;
			}

			int indexOld = mainWindowController.getPhotoList()
					.indexOf(mainTableView.getSelectionModel().getSelectedItem());
			Optional<Photo> possiblePhoto = BasicOperations
					.movePhoto(mainTableView.getSelectionModel().getSelectedItem());

			if (possiblePhoto.isPresent()) {
				mainWindowController.getPhotoList().set(indexOld, possiblePhoto.get());
				mainTableView.getSelectionModel().clearAndSelect(0);
			}

		});

		// DONE change folder location to be shown in explorer to custom folder
		MenuItem showInExplorer = new MenuItem("In System Explorer anzeigen");
		showInExplorer.setOnAction(x -> {
			Desktop desktop = Desktop.getDesktop();
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream("res/config.properties"));
				desktop.open(new File(properties.getProperty("mainFolder")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		SeparatorMenuItem separatorOne = new SeparatorMenuItem();
		SeparatorMenuItem separatorTwo = new SeparatorMenuItem();

		return new ContextMenu(editTags, showExifData, separatorOne, renameFile, deleteItems, separatorTwo,
				showInExplorer);

	}

	/**
	 * @return
	 */
	private FilteredList<Photo> getFilteredPhotosList() {
		FilteredList<Photo> filteredPhotosList = new FilteredList<>(mainWindowController.getPhotoList());
		/*
		 * filter the file list according to the filter selected in the filterlist
		 */
		filteredPhotosList.predicateProperty().bind(Bindings.createObjectBinding(() -> photo -> {
			// DONE add checkbox to select inclusive/exclusive filter

			boolean contains = false;
			boolean exclude = mainWindowController.filterExclusiv();
			List<String> filterList = mainWindowController.getFilterListView().getSelectionModel().getSelectedItems();
			String noFilterString = mainWindowController.getFilterListView().getSelectionModel().getSelectedItem();

			// FIXME when playing around with selecting two or more filter, sometimes a npe
			// gets throwm, and a photo added to the photolist in maintable
			if (!exclude) {
				/*
				 * filter photos inclusively (display photo location if at least one tag matches
				 * any of the selected filters
				 */
				contains = !Collections.disjoint(photo.getTags(), filterList) || noFilterString.equals("Kein Filter");
			} else {
				/*
				 * filter photos exclusively ( display photo location only if its tags matches
				 * every selected filter
				 */
				// DONE implement exclusive filter
				contains = photo.getTags().containsAll(filterList) || noFilterString.equals("Kein Filter");

			}
			return contains;

		}, mainWindowController.getFilterListView().getSelectionModel().selectedItemProperty()));

		return filteredPhotosList;
	}

	public SortedList<Photo> getSortedList() {
		return sortedList;
	}
}
