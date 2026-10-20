package guiLessonsLearned;

/*******
 * <p> Title: ControllerLessonsLearned Class. </p>
 * 
 * <p> Description: The Java/FX-based Lessons Learned page controller. This class contains thin
 * pass-through action methods that are called  by the View's button handlers; each one delegates the actual
 * work to ModelLessonsLearned, following the pattern established by ControllerContributorHome. </p>
 * 
 * <p> Copyright: Team 16 2026 </p>
 * 
 * @author Hrishita Nangunoori
 * 
 * @version 1.00		2026-10-04 Initial version for HW2 Task 4
 * 
 */

public class ControllerLessonsLearned {

	/**
	 * Default constructor is not used.
	 */
	public ControllerLessonsLearned() {
	}

	/**********
	 * <p> Method: performAdd() </p>
	 * 
	 * <p> Description: Invoked when the user presses "Add New Lesson". Fulfills US-1.1 / US-1.2.
	 * </p>
	 */
	protected static void performAdd() {
		ModelLessonsLearned.addLesson();
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: Invoked when the user presses "Save Changes". Fulfills US-3.1 / US-3.2.
	 * </p>
	 */
	protected static void performUpdate() {
		ModelLessonsLearned.updateLesson();
	}

	/**********
	 * <p> Method: performDelete() </p>
	 * 
	 * <p> Description: Invoked when the user presses "Delete Selected". Fulfills US-4.1 / US-4.2.
	 * </p>
	 */
	protected static void performDelete() {
		ModelLessonsLearned.deleteLesson();
	}

	/**********
	 * <p> Method: performClear() </p>
	 * 
	 * <p> Description: Invoked when the user presses "Clear / New", resetting the form so a new
	 * lesson can be entered. </p>
	 */
	protected static void performClear() {
		ModelLessonsLearned.clearForm();
	}

	/**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: Invoked when the user presses "Search". Fulfills US-2.3. </p>
	 */
	protected static void performSearch() {
		ModelLessonsLearned.search(ViewLessonsLearned.text_SearchKeyword.getText());
	}

	/**********
	 * <p> Method: performShowAll() </p>
	 * 
	 * <p> Description: Invoked when the user presses "Show All", listing every lesson in the
	 * database. </p>
	 */
	protected static void performShowAll() {
		ModelLessonsLearned.refreshFullList();
	}

	/**********
	 * <p> Method: performShowMine() </p>
	 * 
	 * <p> Description: Invoked when the user presses "Show Only Mine". Fulfills US-2.1 exactly as
	 * written in the backlog, scoped to the current Contributor's own lessons. </p>
	 */
	protected static void performShowMine() {
		ModelLessonsLearned.refreshMyList();
	}

	/**********
	 * <p> Method: performSelect(int index) </p>
	 * 
	 * <p> Description: Invoked when the user selects a row in the list. Fulfills US-2.2. </p>
	 * 
	 * @param index specifies the position, within the currently displayed list, of the lesson
	 * selected
	 */
	protected static void performSelect(int index) {
		ModelLessonsLearned.selectFromList(index);
	}

	/**********
	 * <p> Method: performBack() </p>
	 * 
	 * <p> Description: Returns the user to the Contributor Home page. </p>
	 */
	protected static void performBack() {
		guiContributor.ViewContributorHome.displayContributorHome(ViewLessonsLearned.theStage,
				ViewLessonsLearned.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: Logs out the current user and returns to the normal login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewLessonsLearned.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: Terminates the execution of the program. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}