package utilWindows;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//TODO implement changing of mainFolder
public class SettingsWindow {

	private Insets defaultPadding;
	private Properties properties;
	private Stage stage;

	public SettingsWindow() {

		properties = new Properties();

		try {
			properties.load(new FileInputStream("res/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		defaultPadding = new Insets(10);

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
		subMenuList.setItems(FXCollections.observableArrayList("Allgemein", "Aussehen", "Externe Programme"));
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
			}
		});

		ButtonBar buttonBar = new ButtonBar();
		Button cancelBtn = new Button("Abbrechen");
		cancelBtn.setOnAction(event -> stage.close());
		
		Button okBtn = new Button("Ok");
		okBtn.setOnAction(event -> {
			applySettings();
			stage.close();
		});
		
		Button applyBtn = new Button("Anwenden");
		applyBtn.setOnAction(event -> applySettings());
		
		buttonBar.getButtons().addAll(okBtn, applyBtn, cancelBtn);

		root.setTop(title);
		root.setCenter(getGeneralNode());
		root.setBottom(buttonBar);
		root.setLeft(subMenuList);

		Scene scene = new Scene(root);
		stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);

		subMenuList.requestFocus();

		stage.show();

	}

	private void applySettings() {
		// TODO Auto-generated method stub
		
	}

	private Node getExternalNode() {

		VBox node = new VBox(15);
		node.setPrefWidth(660);

		Label generalTitle = new Label("Einstellungen zu externen Programmen");
		generalTitle.setStyle("-fx-font-size: 13pt;");

		Label externalPluginLabel = new Label("Externes Bildbearbeitungs Programm: ");

		HBox textAndBtnBox = new HBox(5);

		Label pluginNameLabel = new Label("Name: ");
		pluginNameLabel.setPadding(new Insets(3.5, 0, 0, 0));
		pluginNameLabel.setPrefWidth(50);
		TextField pluginNameTextField = new TextField();
		pluginNameTextField.setPromptText("Name des Programms...");
		pluginNameTextField.setPrefWidth(150);

		Label externalPluginPathLabel = new Label("Pfad:");
		externalPluginPathLabel.setPadding(new Insets(3.5, 0, 0, 0));
		TextField externalPluginPathTextField = new TextField();
		externalPluginPathTextField.setPromptText("Pfad zu externem Programm...");
		externalPluginPathTextField.setPrefWidth(340);
		Button externalPluginBtn = new Button("Browse");

		textAndBtnBox.getChildren().addAll(pluginNameLabel, pluginNameTextField, externalPluginPathLabel,
				externalPluginPathTextField, externalPluginBtn);

		VBox.setMargin(generalTitle, new Insets(0, 0, 0, 15));
		VBox.setMargin(externalPluginLabel, new Insets(0, 0, 0, 15));
		VBox.setMargin(textAndBtnBox, new Insets(0, 0, 0, 15));
		// VBox.setMargin(pluginNameBox, new Insets(0, 0, 0, 15));

		node.getChildren().addAll(generalTitle, externalPluginLabel, textAndBtnBox);

		return node;
	}

	private Node getGeneralNode() {
		VBox node = new VBox(15);
		node.setPrefWidth(660);

		Label generalTitle = new Label("Allgemeine Einstellungen");
		generalTitle.setStyle("-fx-font-size: 13pt;");

		HBox mainFolderBox = new HBox(5);
		Label mainFolderLabel = new Label("Speicherort: ");
		mainFolderLabel.setPadding(new Insets(3.5, 0, 0, 5));
		mainFolderLabel.setPrefWidth(95);
		TextField mainFolderTextField = new TextField(properties.getProperty("mainFolder"));
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
