package utilWindows.wizard.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


/**
 * A Class that constructs a Pane for the Import Photos Wizard. It manages the
 * setting of tags for all imported Photos.
 * 
 * @author Basti
 *
 */
public class WizardTagWindow {

	private Insets defaultPadding;
	private TextField tagsTextField;
	private CheckBox currentDateAsTag;
	private ListView<String> allCurrentTagsListView;
	private ArrayList<String> filterList;

	private Pane mainContainer;

	public WizardTagWindow(Pane mainContainer, ArrayList<String> filterList) {

		this.mainContainer = mainContainer;
		this.filterList = filterList;
		filterList.remove("Kein Filter");
		
		defaultPadding = new Insets(35);
	}

	/**
	 * Constructs the Pane that allows the user to set tags for all imported photos.
	 * @return the constructed Pane
	 */
	public Pane getTagsWindow() {

		Label generalInfoLabel = new Label(
				"F�gen sie Schlagworte hinzu, die f�r alle importierten Bilder gelten sollen! \nSie k�nnen mehrere Schlagw�rter durch Komma trennen. (z.B. Familie, Urlaub, Sommer)\n"
				+ "Individuelle Schlagw�rter k�nnen sie sp�ter einzelnen Fotos hinzuf�gen.");

		tagsTextField = new TextField();
		tagsTextField.setPromptText("Hier Schlagw�rter hinzuf�gen");
		tagsTextField.requestFocus();

		Label listViewInfo = new Label("F�gen sie schon vorhandene Schlagw�rten hinzu: ");
		
		allCurrentTagsListView = new ListView<>();
		allCurrentTagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		allCurrentTagsListView.setItems(FXCollections.observableArrayList(filterList));
		
		
		currentDateAsTag = new CheckBox("Aktuelles Datum als Schlagwort hinzuf�gen");
//		currentDateAsTag.setSelected(true);

		VBox root = new VBox(20, generalInfoLabel, tagsTextField, listViewInfo, allCurrentTagsListView, currentDateAsTag);
		root.setPadding(defaultPadding);
		root.setId("thirdPane");
		root.prefHeightProperty().bind(mainContainer.heightProperty().subtract(40));

		return root;

	}

	/**
	 * Gets the user inputed String in the TextField, splits and normalises the input and adds distinct tags to a List.
	 * @return the List of tags
	 */
	public List<String> getTags() {

		Set<String> tags = new TreeSet<>();

		String allTags = tagsTextField.getText();

		if (!allTags.isEmpty()) {
			tags.addAll(Arrays.asList(allTags.replaceAll("\\s+", "").split(",")));
		}
		
		tags.addAll(allCurrentTagsListView.getSelectionModel().getSelectedItems());
		
		if(currentDateAsTag.isSelected()) {
			tags.add(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).toString());
		}

		return new ArrayList<>(tags);

	}

}
