package utilWindows;

import java.io.IOException;
import java.util.ResourceBundle;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Photo;
import utils.PropertiesInitialiser;

public class EXIFWindow {

	private Stage stage;
//	private Photo photo;

	private Metadata metadata = null;
	private ExifSubIFDDirectory exifSubDirectory = null;
	private ExifIFD0Directory exifID0Directory = null;
	
	private final String noValue = "Keine Angabe";
	
	private String exposureTime = noValue;
	private String apeture = noValue;
	private String makeString = noValue;
	private String modelString = noValue;
	private String isoString = noValue;
	private String flashString = noValue;
	
	private ResourceBundle resources;

	public EXIFWindow(Photo photo) {

//		this.photo = photo;

		resources = PropertiesInitialiser.getResources();
		
		getMetaData(photo);

		initExifWindow();
	}

	private void initExifWindow() {

		VBox root = new VBox(15);
		root.setPadding(new Insets(15));

		Label title = new Label(resources.getString("titleLabel"));
		title.setStyle("-fx-font-size: 12pt; -fx-font-weight: bold;");

		HBox dataBox = new HBox(15);

		VBox labels = new VBox(10);
		Label make = new Label(resources.getString("makeLabel"));
		Label model = new Label(resources.getString("modelLabel"));
		Label exposure = new Label(resources.getString("exposureLabel"));
		Label aperture = new Label(resources.getString("apertureLabel"));
		Label iso = new Label(resources.getString("isoLabel"));
		Label flash = new Label(resources.getString("flashLabel"));

		labels.getChildren().addAll(make, model, exposure, aperture, iso, flash);

		VBox values = new VBox(10);
		Label makeValue = new Label(makeString);
		Label modelValue = new Label(modelString);
		Label exposureValue = new Label(exposureTime);
		Label apertureValue = new Label(apeture);
		Label isoValue = new Label(isoString);
		Label flashValue = new Label(flashString);

		values.getChildren().addAll(makeValue, modelValue, exposureValue, apertureValue, isoValue, flashValue);

		dataBox.getChildren().addAll(labels, values);

		HBox.setMargin(values, new Insets(0, 15, 0, 0));

		Button okBtn = new Button("Ok");
		okBtn.setOnAction(event -> stage.close());
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.getButtons().add(okBtn);

		root.getChildren().addAll(title, dataBox, buttonBar);
		VBox.setMargin(dataBox, new Insets(0, 0, 0, 15));

		Scene scene = new Scene(root);

		stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(resources.getString("titleLabel"));
		stage.initModality(Modality.APPLICATION_MODAL);

	}

	private void getMetaData(Photo photo) {

		//XXX add gif and png info
		try {
			metadata = ImageMetadataReader.readMetadata(photo.getPath().toFile());

			exifSubDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			exifID0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

			if (exifID0Directory != null && exifSubDirectory != null) {
				makeString = exifID0Directory.getDescription(ExifIFD0Directory.TAG_MAKE);
				modelString = exifID0Directory.getDescription(ExifIFD0Directory.TAG_MODEL);
				exposureTime = exifSubDirectory.getDescription(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
				apeture = exifSubDirectory.getDescription(ExifSubIFDDirectory.TAG_APERTURE);
				isoString = exifSubDirectory.getDescription(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
				flashString = exifSubDirectory.getDescription(ExifSubIFDDirectory.TAG_FLASH);
			}
		} catch (IOException | ImageProcessingException e) {
			e.getStackTrace();
		}
	}

	public void showExifWindow() {

		stage.show();
	}

}
