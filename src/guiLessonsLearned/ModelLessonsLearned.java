package guiLessonsLearned;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

import database.Database;
import entityClasses.LessonLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ModelLessonsLearned Class. </p>
 * 
 * <p> Description: This Model class contains the business logic for the Lessons Learned page: it
 * reads from and writes to the Database via the CRUD methods added for HW2, constructs and validates
 * LessonLearned objects from the current form input, and commits the results (either success or a
 * specific, helpful error message) directly to the View's static fields -- the same pattern
 * passwordPopUpWindow.Model uses to update passwordPopUpWindow.View. </p>
 * 
 * <p> Copyright: Team 16 2026 </p>
 * 
 * @author Hrishita Nangunoori
 * 
 * @version 1.00		2026-10-04 Initial version for HW2 Task 4
 * @version 1.01 		2026-10-24 Version for TP2 
 * 
 */

public class ModelLessonsLearned {
	
	/**
	 * Default constructor that is unused.
	 */
	public ModelLessonsLearned() {
	}

	// The id of the lesson currently loaded into the form, or -1 if the form represents a new,
	// not-yet-created lesson. This is how the page knows whether button_Add or button_Update is
	// the correct operation to perform next, and which lesson button_Delete should remove.
	protected static int selectedId = -1;

	// Parallel list of ids matching, in order, what is currently shown in the ListView, so that
	// selecting row i in the list can be mapped back to the correct database id without having to
	// parse ids out of the display text.
	private static List<Integer> displayedIds = new ArrayList<Integer>();

	private static Database theDatabase = applicationMain.FoundationsMain.database;


	/**********
	 * <p> Method: void refreshFullList() </p>
	 * 
	 * <p> Description: Loads every lesson learned in the database and displays it in the
	 * ListView. Supports US-2.1 (list) in its unfiltered form. </p>
	 * 
	 */
	protected static void refreshFullList() {
		LessonsLearnedList all = theDatabase.getAllLessonsLearned();
		displayList(all);
	}


	/**********
	 * <p> Method: void refreshMyList() </p>
	 * 
	 * <p> Description: Loads only the lessons submitted by the currently logged-in Contributor,
	 * fulfilling US-2.1 exactly as written ("I can see a list of all the lessons learned I have
	 * added to the system"). </p>
	 * 
	 */
	protected static void refreshMyList() {
		LessonsLearnedList all = theDatabase.getAllLessonsLearned();
		LessonsLearnedList mine = all.filterBySubmittedBy(ViewLessonsLearned.theUser.getUserName());
		displayList(mine);
	}


	/**********
	 * <p> Method: void search(String keyword) </p>
	 * 
	 * <p> Description: Searches the database for lessons whose title, description, or
	 * recommendation contains the given keyword, and displays the resulting subset -- which may
	 * be empty, one match, or many, per US-2.3. </p>
	 * 
	 * @param keyword specifies the text to search for
	 * 
	 */
	protected static void search(String keyword) {
		LessonsLearnedList results = theDatabase.searchLessonsLearned(keyword);
		displayList(results);
		if (results.isEmpty()) {
			setStatus("No lessons matched \"" + keyword + "\".", false);
		} else {
			setStatus(results.size() + " lesson(s) matched \"" + keyword + "\".", true);
		}
	}


	/**
	 * Shared helper that populates both the ListView and the parallel displayedIds list from a
	 * LessonsLearnedList, so selecting an item later can be mapped back to the right id.
	 */
	private static void displayList(LessonsLearnedList lessons) {
		displayedIds.clear();
		List<String> displayStrings = new ArrayList<String>();
		for (LessonLearned l : lessons.getAll()) {
			displayedIds.add(l.getId());
			String lockedTag = l.isLocked() ? "  [LOCKED]" : "";
			displayStrings.add(l.toString() + lockedTag);
		}
		ViewLessonsLearned.setListItems(displayStrings);
	}


	/**********
	 * <p> Method: void selectFromList(int index) </p>
	 * 
	 * <p> Description: Loads the lesson at the given position in the currently displayed list
	 * into the form for examination and possible editing, fulfilling US-2.2. If the selected
	 * lesson is locked, the form fields are disabled so no edits can be made, and this is
	 * reflected in the status label. </p>
	 * 
	 * @param index specifies the position, within the currently displayed list, of the lesson to
	 * load
	 * 
	 */
	protected static void selectFromList(int index) {
		if (index < 0 || index >= displayedIds.size()) return;

		int id = displayedIds.get(index);
		LessonLearned lesson = theDatabase.getLessonLearnedById(id);
		if (lesson == null) {
			setStatus("Error: That lesson no longer exists. Refreshing the list.", false);
			refreshFullList();
			return;
		}

		selectedId = lesson.getId();
		ViewLessonsLearned.label_SelectedId.setText("Editing Lesson #" + lesson.getId()
				+ (lesson.isLocked() ? "  (LOCKED -- read only)" : ""));
		ViewLessonsLearned.text_Title.setText(lesson.getTitle());
		ViewLessonsLearned.text_Category.setText(lesson.getCategory());
		ViewLessonsLearned.text_Description.setText(lesson.getDescription());
		ViewLessonsLearned.text_Recommendation.setText(lesson.getRecommendation());
		ViewLessonsLearned.text_Effort.setText(String.valueOf(lesson.getEffort()));
		ViewLessonsLearned.text_Time.setText(String.valueOf(lesson.getTime()));

		boolean editable = !lesson.isLocked();
		ViewLessonsLearned.text_Title.setEditable(editable);
		ViewLessonsLearned.text_Category.setEditable(editable);
		ViewLessonsLearned.text_Description.setEditable(editable);
		ViewLessonsLearned.text_Recommendation.setEditable(editable);
		ViewLessonsLearned.text_Effort.setEditable(editable);
		ViewLessonsLearned.text_Time.setEditable(editable);
		ViewLessonsLearned.button_Update.setDisable(!editable);
		ViewLessonsLearned.button_Delete.setDisable(!editable);
		
		ViewLessonsLearned.checkBox_Lock.setSelected(lesson.isLocked());
		ViewLessonsLearned.checkBox_Lock.setDisable(true);

		setStatus("", true);
	}


	/**********
	 * <p> Method: void clearForm() </p>
	 * 
	 * <p> Description: Resets the form to represent a brand-new, not-yet-created lesson. Used
	 * when the page is first opened and whenever the user presses "Clear / New". </p>
	 * 
	 */
	protected static void clearForm() {
		selectedId = -1;
		ViewLessonsLearned.checkBox_Lock.setSelected(false);
		ViewLessonsLearned.checkBox_Lock.setDisable(false);
		ViewLessonsLearned.label_SelectedId.setText("New Lesson (nothing selected)");
		ViewLessonsLearned.text_Title.setText("");
		ViewLessonsLearned.text_Category.setText("");
		ViewLessonsLearned.text_Description.setText("");
		ViewLessonsLearned.text_Recommendation.setText("");
		ViewLessonsLearned.text_Effort.setText("");
		ViewLessonsLearned.text_Time.setText("");
		ViewLessonsLearned.text_Title.setEditable(true);
		ViewLessonsLearned.text_Category.setEditable(true);
		ViewLessonsLearned.text_Description.setEditable(true);
		ViewLessonsLearned.text_Recommendation.setEditable(true);
		ViewLessonsLearned.text_Effort.setEditable(true);
		ViewLessonsLearned.text_Time.setEditable(true);
		ViewLessonsLearned.button_Update.setDisable(false);
		ViewLessonsLearned.button_Delete.setDisable(false);
		setStatus("", true);
	}


	/**********
	 * <p> Method: void addLesson() </p>
	 * 
	 * <p> Description: Builds a LessonLearned from the current form fields, validates it using
	 * LessonLearned.validate() (US-1.2), and if valid, persists it via
	 * Database.createLessonLearned() (US-1.1). On success the list is refreshed and the form is
	 * cleared; on failure, the exact validation message is shown in red. </p>
	 * 
	 */
	protected static void addLesson() {
		LessonLearned lesson = new LessonLearned(
				ViewLessonsLearned.text_Title.getText(),
				ViewLessonsLearned.text_Category.getText(),
				ViewLessonsLearned.text_Description.getText(),
				ViewLessonsLearned.text_Recommendation.getText(),
				ViewLessonsLearned.theUser.getUserName());
		
   		lesson.setLocked(ViewLessonsLearned.checkBox_Lock.isSelected());

		String effortError = applyNumericField(lesson, true);
		if (!effortError.isEmpty()) { setStatus(effortError, false); return; }

		String validation = lesson.validate();
		if (!validation.isEmpty()) {
			setStatus(validation, false);
			return;
		}

		int id = theDatabase.createLessonLearned(lesson);
		if (id > 0) {
			setStatus("Success! Lesson #" + id + " was added.", true);
			clearForm();
			refreshFullList();
		} else {
			setStatus("Error: The lesson could not be saved. Please try again.", false);
		}
	}


	/**********
	 * <p> Method: void updateLesson() </p>
	 * 
	 * <p> Description: Validates the currently edited lesson and, if valid, persists the changes
	 * via Database.updateLessonLearned(), fulfilling US-3.1 and US-3.2. </p>
	 * 
	 */
	protected static void updateLesson() {
		if (selectedId <= 0) {
			setStatus("Error: Select a lesson from the list before saving changes.", false);
			return;
		}

		LessonLearned lesson = new LessonLearned(
				selectedId,
				ViewLessonsLearned.text_Title.getText(),
				ViewLessonsLearned.text_Category.getText(),
				ViewLessonsLearned.text_Description.getText(),
				ViewLessonsLearned.text_Recommendation.getText(),
				ViewLessonsLearned.theUser.getUserName(),
				java.time.LocalDate.now().toString(),
				0, 0, false);
		

		String effortError = applyNumericField(lesson, true);
		if (!effortError.isEmpty()) { setStatus(effortError, false); return; }

		String validation = lesson.validate();
		if (!validation.isEmpty()) {
			setStatus(validation, false);
			return;
		}

		boolean updated = theDatabase.updateLessonLearned(lesson);
		if (updated) {
			setStatus("Success! Lesson #" + selectedId + " was updated.", true);
			clearForm();
			refreshFullList();
		} else {
			setStatus("Error: The update could not be saved. Please try again.", false);
		}
	}


	/**********
	 * <p> Method: void deleteLesson() </p>
	 * 
	 * <p> Description: Checks whether the currently selected lesson may be deleted using
	 * Database.canDeleteLessonLearned() and, if so, deletes it, fulfilling US-4.1 and US-4.2
	 * (including the specific, required locked-lesson error message). </p>
	 * 
	 */
	protected static void deleteLesson() {
		if (selectedId <= 0) {
			setStatus("Error: Select a lesson from the list before deleting.", false);
			return;
		}

		String canDelete = theDatabase.canDeleteLessonLearned(selectedId);
		if (!canDelete.isEmpty()) {
			setStatus(canDelete, false);
			return;
		}

		boolean deleted = theDatabase.deleteLessonLearned(selectedId);
		if (deleted) {
			setStatus("Lesson deleted.", true);
			clearForm();
			refreshFullList();
		} else {
			setStatus("Error: The lesson could not be deleted. Please try again.", false);
		}
	}


	/**
	 * Parses the raw Effort/Time text fields using LessonLearned.validateNumericField() (US-3.2),
	 * and if both are valid, sets them on the given lesson. Returns "" on success, or the first
	 * error message encountered.
	 */
	private static String applyNumericField(LessonLearned lesson, boolean required) {
		String effortText = ViewLessonsLearned.text_Effort.getText();
		String timeText = ViewLessonsLearned.text_Time.getText();

		// Effort/time are optional on initial creation (they default to 0), but if the user
		// typed something, it must be valid.
		if (effortText == null || effortText.trim().isEmpty()) effortText = "0";
		if (timeText == null || timeText.trim().isEmpty()) timeText = "0";

		String effortError = LessonLearned.validateNumericField("Effort", effortText);
		if (!effortError.isEmpty()) return effortError;

		String timeError = LessonLearned.validateNumericField("Time", timeText);
		if (!timeError.isEmpty()) return timeError;

		lesson.setEffort(Double.parseDouble(effortText.trim()));
		lesson.setTime(Double.parseDouble(timeText.trim()));
		return "";
	}


	/**
	 * Writes a message into the status label, in green for success or red for an error --
	 */
	private static void setStatus(String message, boolean success) {
		ViewLessonsLearned.label_Status.setText(message);
		ViewLessonsLearned.label_Status.setTextFill(success ? Color.GREEN : Color.RED);
	}
}