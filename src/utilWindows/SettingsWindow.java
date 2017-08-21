package utilWindows;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.BasicOperations;
import utils.PropertiesInitialiser;

//TODO implement changing of mainFolder
public class SettingsWindow {

	private Insets defaultPadding;
	private Stage stage;
	private TextField pluginNameTextField, externalPluginPathTextField;
	private ComboBox<KeyCode> tagsShortcutComboBoxModifier, tagsShortcutComboBox, importShortcutComboBoxModifier,
			importShortcutComboBox, renameShortcutComboBoxModifier, renameShortcutComboBox,
			photoInfoShortcutComboBoxModifier, photoInfoShortcutComboBox, deletePhotoShortcutComboBoxModifier, deletePhotoShortcutComboBox;

	public SettingsWindow() {

		defaultPadding = new Insets(20);

		initContainerWindow();

	}

	public void showSettings() {
		stage.show();
	}

	private void initContainerWindow() {

		BorderPane root = new BorderPane();
		root.setPadding(defaultPadding);

		Label title = new Label("Einstellungen");
		title.setStyle("-fx-font-size: 14pt;");
		title.setPadding(new Insets(0, 0, 5, 0));

		ListView<String> subMenuList = new ListView<>();
		subMenuList.setPrefWidth(140);
		subMenuList
				.setItems(FXCollections.observableArrayList("Allgemein", /*"Aussehen",*/ "Shortcuts" /*, "Externe Programme"*/));
		subMenuList.getSelectionModel().select(0);
		subMenuList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			switch (newValue) {

			case "Allgemein":

				Node general = getGeneralNode();
				root.setCenter(general);
				break;

			case "Aussehen":

				Node appearance = getAppearanceNope();
				root.setCenter(appearance);
				break;

			case "Externe Programme":
				Node external = getExternalNode();
				root.setCenter(external);
				break;

			case "Shortcuts":
				Node shortcuts = getShortCutsNode();
				root.setCenter(shortcuts);
			}
		});

		ButtonBar buttonBar = new ButtonBar();
		Button cancelBtn = new Button("Abbrechen");
		cancelBtn.setOnAction(event -> stage.close());

		Button okBtn = new Button("Ok");
		okBtn.setOnAction(event -> {
			applySettings();
			BasicOperations.showInformationAlert("Einstellungen geändert", null,
					"Die geänderten Einstellungen werden erst nach einem Neustart gültig!").show();
			stage.close();
		});

		Button applyBtn = new Button("Anwenden");
		applyBtn.setOnAction(event -> applySettings());

		buttonBar.getButtons().addAll(okBtn, applyBtn, cancelBtn);

		root.setTop(title);
		root.setCenter(getGeneralNode());
		root.setBottom(buttonBar);
		root.setLeft(subMenuList);

		Scene scene = new Scene(root, 820, 400);
		stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setMinWidth(850);

		subMenuList.requestFocus();

		stage.show();

	}

	private void applySettings() {

		PropertiesInitialiser.setEditTagsKeyCode(tagsShortcutComboBox.getValue().getName());
		PropertiesInitialiser.setEditTagsModifier(tagsShortcutComboBoxModifier.getValue().getName());

		PropertiesInitialiser.setImportShortCutKeyCode(importShortcutComboBox.getValue().getName());
		PropertiesInitialiser.setImportShortCutModifier(importShortcutComboBoxModifier.getValue().getName());

		PropertiesInitialiser.setRenameShortCutKeyCode(renameShortcutComboBox.getValue().getName());
		PropertiesInitialiser.setRenameShortCutModifier(renameShortcutComboBoxModifier.getValue().getName());

		PropertiesInitialiser.setPhotoInfoShortCutKeyCode(photoInfoShortcutComboBox.getValue().getName());
		PropertiesInitialiser.setPhotoInfoShortCutModifier(photoInfoShortcutComboBoxModifier.getValue().getName());
		
		PropertiesInitialiser.setDeletePhotoShortCutKeyCode(deletePhotoShortcutComboBox.getValue().getName());
		PropertiesInitialiser.setDeletePhotoShortCutModifier(deletePhotoShortcutComboBoxModifier.getValue().getName());

		PropertiesInitialiser.storeConfigValues();

	}

	private Node getShortCutsNode() {

		List<KeyCode> keyCodes = Arrays.asList(KeyCode.A, KeyCode.B, KeyCode.C, KeyCode.D, KeyCode.E, KeyCode.F,
				KeyCode.G, KeyCode.H, KeyCode.I, KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.M, KeyCode.N, KeyCode.O,
				KeyCode.P, KeyCode.Q, KeyCode.R, KeyCode.S, KeyCode.T, KeyCode.U, KeyCode.V, KeyCode.W, KeyCode.X,
				KeyCode.Y, KeyCode.Z);

		ObservableList<KeyCode> obKeyCodes = FXCollections.observableArrayList(keyCodes);

		VBox root = new VBox(15);

		Label title = new Label("Shortcuts festlegen");
		title.setStyle("-fx-font-size: 12pt;");
		

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		FlowPane tagsShortPane = new FlowPane(10, 10);
		Label tagsShortcutLabel = new Label("Schlagwörter editieren: ");
		tagsShortcutComboBoxModifier = new ComboBox<>(FXCollections.observableArrayList(KeyCode.CONTROL, KeyCode.ALT));
		tagsShortcutComboBoxModifier.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getEditTagsModifier()));

		tagsShortcutComboBox = new ComboBox<>(FXCollections.observableArrayList(obKeyCodes));
		tagsShortcutComboBox.getSelectionModel().select(KeyCode.getKeyCode(PropertiesInitialiser.getEditTagsKeyCode()));
		obKeyCodes.remove(KeyCode.getKeyCode(PropertiesInitialiser.getEditTagsKeyCode()));

		tagsShortPane.getChildren().addAll(tagsShortcutComboBoxModifier, tagsShortcutComboBox);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		FlowPane importShortPane = new FlowPane(10, 10);
		Label importShortcutLabel = new Label("Fotos importieren: ");
		importShortcutComboBoxModifier = new ComboBox<>(
				FXCollections.observableArrayList(KeyCode.CONTROL, KeyCode.ALT));
		importShortcutComboBoxModifier.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getImportShortCutModifier()));

		importShortcutComboBox = new ComboBox<>(FXCollections.observableArrayList(obKeyCodes));
		importShortcutComboBox.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getImportShortCutKeyCode()));
		obKeyCodes.remove(KeyCode.getKeyCode(PropertiesInitialiser.getImportShortCutKeyCode()));

		importShortPane.getChildren().addAll(importShortcutComboBoxModifier,
				importShortcutComboBox);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		FlowPane renameShortPane = new FlowPane(10, 10);
		Label renameShortCutLabel = new Label("Dateien umbennen: ");
		renameShortcutComboBoxModifier = new ComboBox<>(
				FXCollections.observableArrayList(KeyCode.CONTROL, KeyCode.ALT));
		renameShortcutComboBoxModifier.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getRenameShortCutModifier()));

		renameShortcutComboBox = new ComboBox<>(FXCollections.observableArrayList(obKeyCodes));
		renameShortcutComboBox.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getRenameShortCutKeyCode()));
		obKeyCodes.remove(KeyCode.getKeyCode(PropertiesInitialiser.getRenameShortCutKeyCode()));
		renameShortPane.getChildren().addAll(renameShortcutComboBoxModifier,
				renameShortcutComboBox);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		FlowPane photoInfoShortPane = new FlowPane(10, 10);
		Label photoInfoShortCutLabel = new Label("Fotoinformationen anzeigen: ");
		photoInfoShortcutComboBoxModifier = new ComboBox<>(
				FXCollections.observableArrayList(KeyCode.CONTROL, KeyCode.ALT));
		photoInfoShortcutComboBoxModifier.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getPhotoInfoShortCutModifier()));

		photoInfoShortcutComboBox = new ComboBox<>(FXCollections.observableArrayList(obKeyCodes));
		photoInfoShortcutComboBox.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getPhotoInfoShortCutKeyCode()));
		obKeyCodes.remove(KeyCode.getKeyCode(PropertiesInitialiser.getPhotoInfoShortCutKeyCode()));
		photoInfoShortPane.getChildren().addAll(photoInfoShortcutComboBoxModifier,
				photoInfoShortcutComboBox);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		FlowPane deletePhotoShortPane = new FlowPane(10, 10);
		Label deletePhotoShortCutLabel = new Label("Fotos löschen: ");
		deletePhotoShortcutComboBoxModifier = new ComboBox<>(
				FXCollections.observableArrayList(KeyCode.CONTROL, KeyCode.ALT));
		deletePhotoShortcutComboBoxModifier.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getDeletePhotoShortCutModifier()));

		deletePhotoShortcutComboBox = new ComboBox<>(FXCollections.observableArrayList(obKeyCodes));
		deletePhotoShortcutComboBox.getSelectionModel()
				.select(KeyCode.getKeyCode(PropertiesInitialiser.getDeletePhotoShortCutKeyCode()));
		obKeyCodes.remove(KeyCode.getKeyCode(PropertiesInitialiser.getDeletePhotoShortCutKeyCode()));
		deletePhotoShortPane.getChildren().addAll(deletePhotoShortcutComboBoxModifier,
				deletePhotoShortcutComboBox);
		
		
		VBox labelBox = new VBox(25);
		labelBox.getChildren().addAll(tagsShortcutLabel, importShortcutLabel, renameShortCutLabel, photoInfoShortCutLabel, deletePhotoShortCutLabel);
		
		VBox comboBoxBox = new VBox(16);
		comboBoxBox.getChildren().addAll(tagsShortPane, importShortPane, renameShortPane, photoInfoShortPane, deletePhotoShortPane);
		
		HBox content = new HBox(15);
		content.getChildren().addAll(labelBox, comboBoxBox);

		root.getChildren().addAll(title, content);

		VBox.setMargin(title, new Insets(0, 0, 0, 15));
		VBox.setMargin(content, new Insets(0, 0, 0, 15));
//		VBox.setMargin(importShortPane, new Insets(0, 0, 0, 15));
//		VBox.setMargin(renameShortPane, new Insets(0, 0, 0, 15));
//		VBox.setMargin(photoInfoShortPane, new Insets(0, 0, 0, 15));
//		VBox.setMargin(deletePhotoShortPane, new Insets(0, 0, 0, 15));

		return root;
	}

	private Node getExternalNode() {

		String externalName = PropertiesInitialiser.getExternalName();
		String externalPath = PropertiesInitialiser.getExternalPath();

		VBox node = new VBox(15);

		Label generalTitle = new Label("Einstellungen zu externen Programmen");
		generalTitle.setStyle("-fx-font-size: 13pt;");

		Label externalPluginLabel = new Label("Externes Bildbearbeitungs Programm: ");

		HBox textAndBtnBox = new HBox(5);

		Label pluginNameLabel = new Label("Name: ");
		pluginNameLabel.setPadding(new Insets(3.5, 0, 0, 0));
		pluginNameLabel.setPrefWidth(50);
		pluginNameTextField = new TextField();
		pluginNameTextField.setText(externalName);
		pluginNameTextField.setPromptText("Name des Programms...");
		pluginNameTextField.setPrefWidth(150);

		Label externalPluginPathLabel = new Label("Pfad:");
		externalPluginPathLabel.setPadding(new Insets(3.5, 0, 0, 0));
		externalPluginPathTextField = new TextField();
		externalPluginPathTextField.setText(externalPath);
		externalPluginPathTextField.setPromptText("Pfad zu externem Programm...");
		externalPluginPathTextField.setPrefWidth(340);
		Button externalPluginBtn = new Button("Browse");
		externalPluginBtn.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			File externalPluginFile = fileChooser.showOpenDialog(stage);
			externalPluginPathTextField.setText(externalPluginFile.getAbsolutePath());
		});

		textAndBtnBox.getChildren().addAll(pluginNameLabel, pluginNameTextField, externalPluginPathLabel,
				externalPluginPathTextField, externalPluginBtn);

		VBox.setMargin(generalTitle, new Insets(0, 0, 0, 15));
		VBox.setMargin(externalPluginLabel, new Insets(0, 0, 0, 15));
		VBox.setMargin(textAndBtnBox, new Insets(0, 0, 0, 15));

		node.getChildren().addAll(generalTitle, externalPluginLabel, textAndBtnBox);

		return node;
	}

	private Node getGeneralNode() {
		VBox node = new VBox(15);

		Label generalTitle = new Label("Allgemeine Einstellungen");
		generalTitle.setStyle("-fx-font-size: 13pt;");

		HBox mainFolderBox = new HBox(5);
		Label mainFolderLabel = new Label("Speicherort: ");
		mainFolderLabel.setPadding(new Insets(3.5, 0, 0, 5));
		mainFolderLabel.setPrefWidth(95);
		TextField mainFolderTextField = new TextField(PropertiesInitialiser.getMainFolder());
		mainFolderTextField.setPrefWidth(480);
		Button changeMainFolderBtn = new Button("Browse");
		mainFolderBox.getChildren().addAll(mainFolderLabel, mainFolderTextField, changeMainFolderBtn);

		HBox changeLanguageBox = new HBox(5);
		Label languageLabel = new Label("Sprache ändern: ");
		languageLabel.setPadding(new Insets(3.5, 0, 0, 5));
		ComboBox<String> languageComboBox = new ComboBox<>(FXCollections.observableArrayList("Deutsch", "English"));
		languageComboBox.getSelectionModel().select(0);
		changeLanguageBox.getChildren().addAll(languageLabel, languageComboBox);

		VBox.setMargin(generalTitle, new Insets(0, 0, 0, 15));
		VBox.setMargin(mainFolderBox, new Insets(0, 0, 0, 15));
		VBox.setMargin(changeLanguageBox, new Insets(0, 0, 0, 15));

		node.getChildren().addAll(generalTitle, changeLanguageBox, mainFolderBox);

		return node;
	}

	private Node getAppearanceNope() {
		System.out.println("appearance node");
		return null;
	}

}
