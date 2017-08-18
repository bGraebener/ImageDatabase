package utilWindows;

import java.io.IOException;

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

	public EXIFWindow(Photo photo) {

//		this.photo = photo;

		getMetaData(photo);

		initExifWindow();
	}

	private void initExifWindow() {

		VBox root = new VBox(15);
		root.setPadding(new Insets(15));

		Label title = new Label("Exif - Metadaten");
		title.setStyle("-fx-font-size: 12pt; -fx-font-weight: bold;");

		HBox dataBox = new HBox(15);

		VBox labels = new VBox(10);
		Label make = new Label("Hersteller: ");
		Label model = new Label("Modell: ");
		Label exposure = new Label("Belichtungszeit: ");
		Label aperture = new Label("Blendenzahl: ");
		Label iso = new Label("ISO");
		Label flash = new Label("Blitzlichtmodus: ");

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
		stage.setTitle("EXIF - Metadaten");
		stage.initModality(Modality.APPLICATION_MODAL);

	}

	private void getMetaData(Photo photo) {

		//TODO add gif and png info
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
