package model;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

/**
 * Bean class to represent a Photo. Only stores information about the location
 * of the image file it represents and a List of tags to ensure serialisability.
 * 
 * @author Basti
 *
 */
public class Photo implements Serializable {

	private static final long serialVersionUID = 7295847979775465822L;
	
	private TreeSet<String> tagsList;
	private String location;

	private String added;

	public Photo(Path location) {

		//FIXME took out toRealPath, might fuck up other things, always check with Files.exists()
		this.location = location.toString();

		tagsList = new TreeSet<>();
		
		added = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"));
	}

	/**
	 * Returns the Photos file name as an Observable String property.
	 * @return Filename as SimpleStringProperty 
	 */
	public SimpleStringProperty getFileName() {
		return new SimpleStringProperty(getPath().getFileName().toString());
	}
	
	/**
	 * Returns the Photos location name as an Observable String property.
	 * @return Location as SimpleStringProperty 
	 */
	public SimpleStringProperty getLocation() {
		return new SimpleStringProperty(getPath().toString());
	}
	
	/**
	 * Returns the Photos tags list as an Observable List.
	 * @return Tagslist as SimpleStringProperty 
	 */
	public SimpleStringProperty getTagsAsObservableProperty(){
		return new SimpleStringProperty(getTags().toString().replaceAll("\\[|\\]", "")) ;
//		return FXCollections.observableArrayList(tagsList);
	}
	

	/**
	 * Returns the Path to the location of the Photo.
	 * 
	 * @return the Path of the Photo
	 */
	public Path getPath() {
		return Paths.get(location);
	}

	/**
	 * Getter for the Photo's list of tags
	 */
	public TreeSet<String> getTags() {
		return tagsList;
	}
	
	/**
	 * Creates an image from the Photo's location
	 * 
	 * @return the Image created
	 */
	public Image getImage() {
		return new Image("file:" + this.getLocation().get(), true);
		// return photo;
	}

	/**
	 * Creates an image from the Photo's location with a requested height of 150px
	 * and preserved ratio.
	 * 
	 * @return the Image created
	 */
	public Image getThumbNail() {
		return new Image("file:" + this.getLocation().get(), 0, 150, true, true);
	}

	@Override
	public String toString() {
		return "Photo [tags=" + tagsList + ", location=" + getLocation().get() + "]";
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Photo)) {
			return false;
		}

		Photo other = (Photo) obj;

		return this.getLocation().get().equals(other.getLocation().get());

	}

	public SimpleStringProperty getAddedProperty() {
		return new SimpleStringProperty(added);
	}
	
}
