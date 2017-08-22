package utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.TransferMode;
import mainWindow.controller.MainWindowController;

/**
 * Class that initialises the ListView that holds the filter items used to
 * filter the photo list.
 * 
 * @author Basti
 *
 */
public class FilterListViewInitialiser {

	private MainWindowController mainWindowController;
	private ResourceBundle resources;

	public FilterListViewInitialiser(MainWindowController mainWindowController) {

		this.mainWindowController = mainWindowController;
		
		resources = PropertiesInitialiser.getResources();
	}

	/**
	 * Initialises the ListView that holds the filters gathered from all photos
	 * managed by the application. Sets a drag event handler, that can be used to
	 * tag photos individually or in bulk.
	 * 
	 * @return a List of all distinct filters
	 */
	public ObservableList<String> initFilterListView() {
		// Filter List
		ObservableList<String> filterList = getAllFilter();
		mainWindowController.getFilterListView().setItems(filterList);
		mainWindowController.getFilterListView().getSelectionModel().select(0);
		mainWindowController.getFilterListView().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		mainWindowController.getFilterListView().setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<String>() {

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

			/*******************************************************************************************************************/
			// Add filter by dragging files over the filter list
			cell.setOnDragOver(dragEvent -> {
				if (cell.getItem() != null && !cell.getItem().isEmpty() && !cell.getItem().equals(resources.getString("noFilter"))) {
					dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);

				}
			});

			// add tags to every photo dragged and dropped over this filter cell
			cell.setOnDragDropped(dragEvent -> {
				mainWindowController.getMainTableView().getSelectionModel().getSelectedItems()
						.forEach(x -> x.getTags().add(cell.getItem()));
				
				//cheat to update the tagsList and make it visible
				mainWindowController.getFilterListView().getSelectionModel().clearAndSelect(1);
				mainWindowController.getFilterListView().getSelectionModel().clearAndSelect(0);
//				mainTableView.getSelectionModel().clearAndSelect(0);
			});

			cell.setOnMouseClicked(mouseEvent -> {
				mainWindowController.getMainTableView().getSelectionModel().clearAndSelect(0);
			});

			cell.setContextMenu(createFilterListCellContextMenu());

			return cell;
		});

		return filterList;
	}

	/**
	 * Method that creates the ContextMenu that is attached to each individual
	 * filter cell in the filter list view.
	 * 
	 * @return the created ContextMenu
	 */
	private ContextMenu createFilterListCellContextMenu() {

		Tooltip tooltip = new Tooltip();
		tooltip.setStyle(" -fx-font-size: 12px; ");

		// DONE after filter gets deleted, isn't removed from filter view list
		MenuItem removeFilter = new MenuItem(resources.getString("filterListRemoveFilterMenuItem"));
		removeFilter.setOnAction(event -> {
			Alert alert = BasicOperations.showConfirmationAlert(resources.getString("filterListRemoveAlertTitle"), null,
					resources.getString("filterListRemoveAlertContent"));
			Optional<ButtonType> removeFilterAlert = alert.showAndWait();

			if (removeFilterAlert.isPresent() && removeFilterAlert.get() == ButtonType.OK) {

				mainWindowController.getPhotoList().forEach(x -> x.getTags()
						.remove(mainWindowController.getFilterListView().getSelectionModel().getSelectedItem()));
				if (!mainWindowController.getFilterListView().getSelectionModel().getSelectedItem()
						.equals("Kein Filter")) {
					mainWindowController.getFilterList()
							.remove(mainWindowController.getFilterListView().getSelectionModel().getSelectedItem());
				}
				mainWindowController.getFilterListView().getSelectionModel().clearAndSelect(0);
			}
		});
		
		MenuItem sortFilterList = new MenuItem(resources.getString("sortFilterListMenuItem"));
		sortFilterList.setOnAction(event -> {
			
			mainWindowController.getFilterList().remove(resources.getString("noTags"));
			mainWindowController.getFilterList().remove(resources.getString("noFilter"));
			mainWindowController.getFilterList().sort(String::compareTo);
			mainWindowController.getFilterList().addAll(0, Arrays.asList(resources.getString("noFilter"), resources.getString("noTags")));
			
		});
		
		return new ContextMenu(removeFilter, sortFilterList);
	}

	/**
	 * Gathers and collects all distinct tags from each photo in the list of photos.
	 * 
	 * @return the ObservableList of Filters
	 */
	private ObservableList<String> getAllFilter() {

		ObservableList<String> filterList = FXCollections.observableArrayList();

		Set<String> tmp = new TreeSet<>();

		mainWindowController.getPhotoList().forEach(photo -> tmp.addAll(photo.getTags()));
		filterList.add(resources.getString("noFilter"));
		filterList.add(resources.getString("noTags"));
		filterList.addAll(tmp);

		return filterList;
	}
}
