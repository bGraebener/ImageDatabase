package utilWindows.wizard.controller;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Photo;
import utilWindows.wizard.view.LocationChooserWindow;
import utilWindows.wizard.view.WizardPreviewWindow;
import utilWindows.wizard.view.WizardTagWindow;
import utils.BasicOperations;
import utils.PropertiesInitialiser;

//FIXME Refactor!!!!!

/**
 * Controller for the main Wizard Window Container.
 * 
 * @author Basti
 *
 */
public class WizardController implements Initializable {

	@FXML
	private Button cancelBtn;
	@FXML
	private Button nextBtn;
	@FXML
	private Button backBtn;
	@FXML
	private VBox mainContainer;

	private Stage currentStage;

	private List<Path> listOfFiles;
	private List<Photo> importedList;
	private List<String> tagsList;
	private ArrayList<String> filterList;
	private ArrayList<Photo> copyList;

	private LocationChooserWindow locationChooser;
	private WizardPreviewWindow previewWindow;
	private WizardTagWindow tagsWindow;

	private Stage stage;
	private ProgressBar pb;

	private ResourceBundle resources;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		locationChooser = new LocationChooserWindow(mainContainer);

		mainContainer.getChildren().add(0, locationChooser.getImagesLocationPane());

		this.resources = PropertiesInitialiser.getResources();
		
		backBtn.setDisable(true);
		nextBtn.requestFocus();

	}

	/**
	 * Eventhandler for the nextPane Button. Checks the name of the current Pane
	 * and sets the next one accordingly.
	 * 
	 * @param event
	 */
	@FXML
	private void nextPane(ActionEvent event) {

		switch (mainContainer.getChildren().get(0).getId()) {

		case "firstPane":
			if (locationChooser.skipPreview()) {
				switchToTagsWindow();
			} else {
				switchToPreviewWindow();
			}
			backBtn.setDisable(false);
			break;

		case "secondPane":
			switchToTagsWindow();
			break;

		case "thirdPane":

			tagsList = tagsWindow.getTags();

			// DONE copy physical files here

			copyFiles();

			currentStage.close();
			break;
		}
	}

	/**
	 * Eventhandler for the previousPane Button. Checks the name of the current
	 * Pane and sets the next one accordingly.
	 * 
	 * @param event
	 */
	@FXML
	private void previousPane(ActionEvent event) {
		// DONE zurueck button implementieren

		switch (mainContainer.getChildren().get(0).getId()) {

		case "thirdPane":
			if (locationChooser.skipPreview()) {
				locationChooser = new LocationChooserWindow(mainContainer);
				mainContainer.getChildren().set(0, locationChooser.getImagesLocationPane());
				backBtn.setDisable(true);
			} else {
				switchToPreviewWindow();
			}
			break;

		case "secondPane":
			locationChooser = new LocationChooserWindow(mainContainer);
			mainContainer.getChildren().set(0, locationChooser.getImagesLocationPane());
			backBtn.setDisable(true);
			break;
		}
	}

	/**
	 * Utility mathod to switch from the current window to the PreviewWindow.
	 */
	private void switchToPreviewWindow() {
		listOfFiles = locationChooser.getListOfPaths();

		// Alert alert;

		if (listOfFiles == null || listOfFiles.isEmpty()) {
			Alert alert = BasicOperations.showErrorAlert("Keine Dateien gefunden",
					"In dem gewählten Ordner befinden sich keine Dateien der unterstützten Formate.",
					"Bitte wählen sie einen anderen Ordner!");
			alert.showAndWait();
			return;
		}

		previewWindow = new WizardPreviewWindow(mainContainer, listOfFiles);

		mainContainer.getChildren().set(0, previewWindow.getPreviewWindow());
		currentStage.setTitle("Vorschau");

	}

	/**
	 * Utility mathod to switch from the current window to the TagsWindow.
	 */
	private void switchToTagsWindow() {
		if (locationChooser.skipPreview()) {

			if (locationChooser.getListOfPaths() == null || locationChooser.getListOfPaths().isEmpty()) {
				Alert alert = BasicOperations.showErrorAlert(resources.getString("noFilesFoundAlertTitle"),
						resources.getString("noFilesFoundAlertHeader"),
						resources.getString("noFilesFoundAlertContent"));
				alert.showAndWait();
				return;
			}

			importedList = locationChooser.getListOfPaths().stream().map(x -> new Photo(x))
					.collect(Collectors.toList());
		} else {

			importedList = previewWindow.getImportedList();
		}

		if (importedList == null || importedList.isEmpty()) {
			Alert alert = BasicOperations.showErrorAlert(resources.getString("noPhotosSelectedAlertTitle"), null,
					resources.getString("noPhotosSelectedAlertContent"));
			alert.showAndWait();
			return;
		}

		tagsWindow = new WizardTagWindow(mainContainer, filterList);

		mainContainer.getChildren().set(0, tagsWindow.getTagsWindow());
		currentStage.setTitle(resources.getString("currentStageTitle"));
		nextBtn.setText(resources.getString("nextButtonImport"));
	}

	/**
	 * EventHandler for cancel wizard button. Deletes any elements possibly
	 * already stored in the Lists and closes the window.
	 * 
	 * @param event
	 */
	@FXML
	private void cancelWizard(ActionEvent event) {

		clearLists();
		this.currentStage.close();
	}

	/**
	 * Copies the selected files to the previously specified photo location.
	 * Asks whether to rename an existing file.
	 */
	private void copyFiles() {

		// FIXME progressbar Window

		showProgressBarWindow();
		stage.show();

		copyList = new ArrayList<>();

		// XXX change to loop to skip if no rename

		for (int i = 0; i < importedList.size(); i++) {
			double secondCounter = i;
			Optional<Photo> newPhoto = BasicOperations.copyFile(importedList.get((int) secondCounter));

			if (newPhoto.isPresent()) {
				copyList.add(newPhoto.get());
				tagsList.addAll(newPhoto.get().getTags());
			}
		}
		Platform.runLater(() -> {
			stage.close();
		});

	}

	private void showProgressBarWindow() {

		VBox progressBox = new VBox();
		Label importProgressLabel = new Label(resources.getString("copyPhotosLabel"));
		pb = new ProgressBar();
		pb.setPrefWidth(300);

		VBox.setMargin(importProgressLabel, new Insets(15));

		progressBox.getChildren().addAll(importProgressLabel, pb);
		Scene scene = new Scene(progressBox);
		stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(resources.getString("copyPhotosLabel"));

	}

	private void clearLists() {
		if (importedList != null) {
			importedList.clear();
		}

		if (tagsList != null) {
			tagsList.clear();
		}

		if (copyList != null) {
			copyList.clear();
		}
	}

	public List<Photo> getCopyList() {
		// DONE add guard for NPE
		if (copyList != null && tagsList != null) {
			copyList.forEach(x -> x.getTags().addAll(tagsList));
		}
		return copyList;
	}

	public List<String> getTagsList() {
		return tagsList;
	}

	public Button getNextBtn() {
		return nextBtn;
	}

	public void setFilterList(ArrayList<String> filterList) {
		this.filterList = filterList;
	}

	public void setCurrentStage(Stage currentStage) {
		this.currentStage = currentStage;
		this.currentStage.setOnCloseRequest(ev -> clearLists());
	}

}
