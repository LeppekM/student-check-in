package HelperClasses;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries.<p>
 *
 * If the entered text matches a part of the supplied entries these are
 * going to be displayed in a popup. Further the matching part of the entry is
 * going to be displayed in a special style, defined by
 * {@link #textOccurrenceStyle textOccurenceStyle}. The maximum number of
 * displayed entries in the popup is defined by
 * {@link #maxEntries maxEntries}.<br>
 * By default the pattern matching is not case-sensitive. This behaviour is
 * defined by the {@link #caseSensitive caseSensitive}
 * .<p>
 *
 * The AutoCompleteTextField also has a List of
 * {@link #filteredEntries filteredEntries} that is equal to the search results
 * if search results are not empty, or {@link #filteredEntries filteredEntries}
 * is equal to {@link #entries entries} otherwise. If
 * {@link #popupHidden popupHidden} is set to true no popup is going to be
 * shown. This list can be used to bind all entries to another node (a ListView
 * for example) in the following way:
 * <pre>
 * <code>
 * AutoCompleteTextField auto = new AutoCompleteTextField(entries);
 * auto.setPopupHidden(true);
 * SimpleListProperty filteredEntries = new SimpleListProperty(auto.getFilteredEntries());
 * listView.itemsProperty().bind(filteredEntries);
 * </code>
 * </pre>
 *
 * @author Caleb Brinkman
 * @author Fabian Ochmann
 */
public class AutoCompleteTextField extends JFXTextField {

    /**
     * The existing autocomplete entries.
     */
    private SortedSet<String> entries;

    /**
     * The set of filtered entries:<br>
     * Equal to the search results if search results are not empty, equal to
     * {@link #entries entries} otherwise.
     */
    private final ObservableList<String> filteredEntries
            = FXCollections.observableArrayList();

    /**
     * The popup used to select an entry.
     */
    private ContextMenu entriesPopup;

    /**
     * Indicates whether the search is case-sensitive or not. <br>
     * Default: false
     */
    private final boolean caseSensitive = false;

    /**
     * Indicates whether the Popup should be hidden or displayed. Use this if
     * you want to filter an existing list/set (for example values of a
     * {@link javafx.scene.control.ListView ListView}). Do this by binding
     * {@link #getFilteredEntries() getFilteredEntries()} to the list/set.
     */
    private final boolean popupHidden = false;

    /**
     * The CSS style that should be applied on the parts in the popup that match
     * the entered text. <br>
     * Default: "-fx-font-weight: bold; -fx-fill: red;"
     * <p>
     * Note: This style is going to be applied on an
     * {@link javafx.scene.text.Text Text} instance. See the <i>JavaFX CSS
     * Reference Guide</i> for available CSS Properties.
     */
    private final String textOccurrenceStyle = "-fx-font-weight: bold; "
            + "-fx-fill: red;";

    /**
     * The maximum Number of entries displayed in the popup.<br>
     * Default: 10
     */
    private final int maxEntries = 10;

    public void initEntrySet(SortedSet<String> entrySet) {
        this.entries = entrySet == null ? new TreeSet<>() : entrySet;
        this.filteredEntries.addAll(entries);

        entriesPopup = new ContextMenu();
        textProperty().addListener((observableValue, s, s2) -> {
            if (getText().isEmpty()) {
                filteredEntries.clear();
                filteredEntries.addAll(entries);
                entriesPopup.hide();
            } else {
                LinkedList<String> searchResult = new LinkedList<>();

                //Check if the entered Text is part of some entry
                String text = getText();
                Pattern pattern;
                if (isCaseSensitive()) {
                    pattern = Pattern.compile(".*" + text + ".*");
                } else {
                    pattern = Pattern.compile(".*" + text + ".*",
                            Pattern.CASE_INSENSITIVE);
                }

                for (String entry : entries) {
                    Matcher matcher = pattern.matcher(entry);
                    if (matcher.matches()) {
                        searchResult.add(entry);
                    }
                }

                if (entrySet != null && !entrySet.isEmpty()) {
                    filteredEntries.clear();
                    filteredEntries.addAll(searchResult);

                    //Only show popup if not in filter mode
                    if (!isPopupHidden()) {
                        populatePopup(searchResult, text);
                        if (!entriesPopup.isShowing()) {
                            entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                        }
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> entriesPopup.hide());
    }


    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult, String text) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int count = Math.min(searchResult.size(), getMaxEntries());
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            TextFlow entryFlow = getTextFlow(text, result);

            CustomMenuItem item = new CustomMenuItem(entryFlow, true);
            item.setOnAction(actionEvent -> {
                setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }

    private TextFlow getTextFlow(String text, String result) {
        int occurrence;

        if (isCaseSensitive()) {
            occurrence = result.indexOf(text);
        } else {
            occurrence = result.toLowerCase().indexOf(text.toLowerCase());
        }

        //Part before occurrence (might be empty)
        Text pre = new Text(result.substring(0, occurrence));
        //Part of (first) occurrence
        Text in = new Text(result.substring(occurrence,
                occurrence + text.length()));
        in.setStyle(getTextOccurrenceStyle());
        //Part after occurrence
        Text post = new Text(result.substring(occurrence + text.length()
        ));

        return new TextFlow(pre, in, post);
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public String getTextOccurrenceStyle() {
        return textOccurrenceStyle;
    }

    public boolean isPopupHidden() {
        return popupHidden;
    }

    public ObservableList<String> getFilteredEntries() {
        return filteredEntries;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

}