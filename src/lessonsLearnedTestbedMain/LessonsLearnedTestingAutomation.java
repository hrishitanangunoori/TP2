package lessonsLearnedTestbedMain;

import java.sql.SQLException;

import database.Database;
import entityClasses.LessonLearned;
import entityClasses.LessonsLearnedList;

/**
 * Title: LessonsLearnedTestingAutomation Class.
 * 
 * Description: An automated test suite for validating the backend CRUD operations,
 * search filters, and input constraint logic of the Lessons Learned system.
 * Programmatically initializes test scenarios, executes database and model methods,
 * and asserts expected results while logging pass/fail metrics to the console.
 * 
 * Note: Resets the database state upon execution to ensure test consistency.
 * 
 * Copyright: Team 16 2026
 * 
 * @author Hrishita Nangunoori
 * @version 1.01 2026-10-05 Refactored test setup, user roles, and lock state validations
 */

public class LessonsLearnedTestingAutomation {

	static int numPassed = 0;	// Counter for passed test cases
	static int numFailed = 0;	// Counter for failed test cases

	static Database db = new Database();

	// Sample user accounts for testing access controls and filtering
	private static final String HRISHITA = "hrishita1";
	private static final String RHEA = "rhea1";

	// Invalid lesson ID used to test error handling
	private static final int NONEXISTENT_ID = 1000;

	private static final String LESSON1_TITLE = "H2 Database file locked between test runs.";

	// Primary key references maintained across test cases to simulate end-to-end workflows
	static int lesson1Id = -1;		// Unlocked record owned by hrishita1
	static int lesson2Id = -1;		// Record updated and subsequently deleted during testing
	static int lesson3Id = -1;		// Unlocked record owned by rhea1
	static int lesson4Id = -1;		// Locked record owned by rhea1

	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nLessons Learned Testing Automation");

		try {
			db.connectToDatabase();
			// Resets the test database to ensure consistent execution environment
			db.clearLessonsLearnedForTesting();
		} catch (SQLException e) {
			System.out.println("Could not connect to the database: " + e.getMessage());
			return;
		}

		/************** US-1.1 / US-1.2: Create **************/
		testTC1_1_AddValidLesson();
		testTC1_2a_BlankTitleAndDescription();
		testTC1_2b_BlankTitleOnly();
		testTC1_2c_DescriptionTooShort();
		testTC1_2d_BlankCategory();

		/************** US-2.1: Read - List **************/
		testTC2_1_ListOnlyMyLessons();

		/************** US-2.2: Read - Single Item Detail **************/
		testTC2_2a_ViewUnlockedLesson();
		testTC2_2b_ViewLockedLesson();

		/************** US-2.3: Read - Subset via Search **************/
		testTC2_3a_SearchMatches();
		testTC2_3b_SearchNoMatches();

		/************** US-3.1 / US-3.2: Update **************/
		testTC3_1_AddEffortAndTime();
		testTC3_2a_LettersInEffortField();
		testTC3_2b_NegativeNumberInTimeField();

		/************** US-4.1 / US-4.2: Delete **************/
		testTC4_1_DeleteUnlockedLesson();
		testTC4_2a_DeleteLockedLesson();
		testTC4_2b_DeleteNonExistentLesson();

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);

		db.closeConnection();
	}


	/**
	 * Logs test results to the console and increments pass/fail counters.
	 */
	private static void report(String testCaseId, boolean passed, String detail) {
		System.out.println("____________________________________________________________________________\n");
		System.out.println("Test case: " + testCaseId);
		if (passed) {
			System.out.println("***Success*** " + detail);
			numPassed++;
		} else {
			System.out.println("***Failure*** " + detail);
			numFailed++;
		}
	}


	/**
	 * Validates entity constraints before attempting database insertion.
	 * Returns the generated record ID on success, or -1 if validation fails.
	 */
	private static int addLikeTheModelDoes(LessonLearned lesson) {
		if (!lesson.validate().isEmpty()) return -1;
		return db.createLessonLearned(lesson);
	}


	/**
	 * Retrieves total count of records currently in the database.
	 */
	private static int countAll() {
		return db.getAllLessonsLearned().size();
	}


	/**
	 * Compares all user-defined attributes of two lesson objects for equality.
	 */
	private static boolean sameFields(LessonLearned a, LessonLearned b) {
		return a != null && b != null
				&& a.getTitle().equals(b.getTitle())
				&& a.getCategory().equals(b.getCategory())
				&& a.getDescription().equals(b.getDescription())
				&& a.getRecommendation().equals(b.getRecommendation())
				&& a.getSubmittedBy().equals(b.getSubmittedBy());
	}


	/* ============================== US-1.1 / US-1.2: Create ============================== */

	/**
	 * Verifies successful creation and database insertion of a valid lesson record.
	 */
	private static void testTC1_1_AddValidLesson() {
		LessonLearned lesson = new LessonLearned(
				LESSON1_TITLE,
				"Environment",
				"Two tests opened the database file at once, causing a database lock error.",
				"Close the database connection after each test.",
				HRISHITA);

		String validation = lesson.validate();
		int id = -1;
		if (validation.isEmpty()) {
			id = db.createLessonLearned(lesson);
		}
		lesson1Id = id; // Saved for subsequent tests

		LessonLearned readBack = (id > 0) ? db.getLessonLearnedById(id) : null;

		boolean passed = validation.isEmpty() && id > 0 && sameFields(lesson, readBack);

		report("TC-1.1", passed,
				"Create a valid lesson: validate()=\"" + validation + "\", createLessonLearned id="
				+ id + " (expected > 0), read-back matches every input field = "
				+ sameFields(lesson, readBack));
	}

	/**
	 * Verifies rejection when both title and description fields are empty.
	 */
	private static void testTC1_2a_BlankTitleAndDescription() {
		int before = countAll();
		LessonLearned lesson = new LessonLearned("", "Environment", "",
				"Close the database connection after each test", HRISHITA);

		String validation = lesson.validate();
		String expected = "Error: Title and Description cannot be empty.\n";
		int id = addLikeTheModelDoes(lesson);
		int after = countAll();

		boolean passed = validation.equals(expected) && id == -1 && after == before;
		report("TC-1.2a", passed, "validate() returned \"" + validation + "\"; expected \""
				+ expected + "\"; createLessonLearned() not called: lesson count before="
				+ before + ", after=" + after);
	}

	/**
	 * Verifies rejection when only the title field is empty.
	 */
	private static void testTC1_2b_BlankTitleOnly() {
		int before = countAll();
		LessonLearned lesson = new LessonLearned("", "Environment",
				"Two tests opened the database file at once, causing a database lock error.",
				"Close the database connection after each test", HRISHITA);

		String validation = lesson.validate();
		String expected = "Error: Title cannot be empty.\n";
		int id = addLikeTheModelDoes(lesson);
		int after = countAll();

		boolean passed = validation.equals(expected) && id == -1 && after == before;
		report("TC-1.2b", passed, "validate() returned \"" + validation + "\"; expected \""
				+ expected + "\"; createLessonLearned() not called: lesson count before="
				+ before + ", after=" + after);
	}

	/**
	 * Verifies rejection when the description does not meet minimum length requirements.
	 */
	private static void testTC1_2c_DescriptionTooShort() {
		int before = countAll();
		LessonLearned lesson = new LessonLearned("H2 database lock", "Environment", "Test",
				"Close the database connection after each test", HRISHITA);

		String validation = lesson.validate();
		int id = addLikeTheModelDoes(lesson);
		int after = countAll();

		boolean passed = validation.contains(
				"Error: Description must be at least 10 characters long so it is meaningful.")
				&& id == -1 && after == before;
		report("TC-1.2c", passed, "validate() returned \"" + validation
				+ "\"; createLessonLearned() not called: lesson count before=" + before
				+ ", after=" + after);
	}

	/**
	 * Verifies rejection when the category field is left empty.
	 */
	private static void testTC1_2d_BlankCategory() {
		int before = countAll();
		LessonLearned lesson = new LessonLearned("H2 database lock", "",
				"Two tests opened the database file at once causing a lock error",
				"Close the database connection after each test", HRISHITA);

		String validation = lesson.validate();
		int id = addLikeTheModelDoes(lesson);
		int after = countAll();

		boolean passed = validation.contains("Error: Category cannot be empty.")
				&& id == -1 && after == before;
		report("TC-1.2d", passed, "validate() returned \"" + validation
				+ "\"; createLessonLearned() not called: lesson count before=" + before
				+ ", after=" + after);
	}


	/* ============================== US-2.1: Read - List ============================== */

	/**
	 * Verifies filtering functionality by contributor username.
	 */
	private static void testTC2_1_ListOnlyMyLessons() {
		LessonLearned lesson2 = new LessonLearned(
				"Forgot to clear password after one-time reset", "Testing",
				"The old password still worked after a one-time password reset because the "
				+ "hash was never cleared.",
				"Clear password hash immediately upon successful password reset.", HRISHITA);
		lesson2Id = addLikeTheModelDoes(lesson2);

		LessonLearned lesson3 = new LessonLearned(
				"Typo for the Button Text", "UI",
				"The Save Button was misspelled on the screen.",
				"Double check the label for text on the button.", RHEA);
		lesson3Id = addLikeTheModelDoes(lesson3);

		LessonsLearnedList all = db.getAllLessonsLearned();
		LessonsLearnedList hrishitaLessons = all.filterBySubmittedBy(HRISHITA);
		LessonsLearnedList rheaLessons = all.filterBySubmittedBy(RHEA);

		boolean passed = all.size() == 3 && hrishitaLessons.size() == 2
				&& rheaLessons.size() == 1;
		report("TC-2.1", passed, "getAllLessonsLearned().size()=" + all.size()
				+ " (expected 3); filterBySubmittedBy(\"" + HRISHITA + "\").size()="
				+ hrishitaLessons.size() + " (expected 2); filterBySubmittedBy(\"" + RHEA
				+ "\").size()=" + rheaLessons.size() + " (expected 1)");
	}


	/* ====================== US-2.2: Read - Single Item Detail ====================== */

	/**
	 * Verifies loading single record details for an unlocked lesson.
	 */
	private static void testTC2_2a_ViewUnlockedLesson() {
		LessonLearned lesson = db.getLessonLearnedById(lesson1Id);
		boolean passed = lesson != null && !lesson.isLocked()
				&& lesson.getTitle().equals(LESSON1_TITLE);
		report("TC-2.2a", passed, "getLessonLearnedById(" + lesson1Id + ") returned "
				+ (lesson != null ? "\"" + lesson.getTitle() + "\"" : "null")
				+ ", isLocked()=" + (lesson != null ? lesson.isLocked() : "null")
				+ " (expected false)");
	}

	/**
	 * Verifies reading details and lock flag status for a locked lesson.
	 */
	private static void testTC2_2b_ViewLockedLesson() {
		LessonLearned lesson = new LessonLearned(
				"Lock Test", "Testing",
				"Testing the Lock feature on the UI.",
				"Do not edit this record.", RHEA);
		lesson.setLocked(true);
		lesson4Id = addLikeTheModelDoes(lesson);

		LessonLearned readBack = (lesson4Id > 0) ? db.getLessonLearnedById(lesson4Id) : null;
		boolean passed = readBack != null && sameFields(lesson, readBack) && readBack.isLocked();
		report("TC-2.2b", passed, "getLessonLearnedById(" + lesson4Id + ") fields match input="
				+ sameFields(lesson, readBack) + ", isLocked()="
				+ (readBack != null ? readBack.isLocked() : "null") + " (expected true)");
	}


	/* ====================== US-2.3: Read - Subset via Search ====================== */

	/**
	 * Verifies keyword search when matching records exist.
	 */
	private static void testTC2_3a_SearchMatches() {
		LessonsLearnedList results = db.searchLessonsLearned("H2");

		boolean passed = results.size() == 1 && results.getAll().get(0).getId() == lesson1Id;
		report("TC-2.3a", passed, "searchLessonsLearned(\"H2\").size()=" + results.size()
				+ " (expected 1); the single result is Lesson 1 (id " + lesson1Id + ") = "
				+ (results.size() == 1 && results.getAll().get(0).getId() == lesson1Id));
	}

	/**
	 * Verifies keyword search returns an empty list when no matches are found.
	 */
	private static void testTC2_3b_SearchNoMatches() {
		LessonsLearnedList results = db.searchLessonsLearned("SQL");
		boolean passed = results != null && results.isEmpty();
		report("TC-2.3b", passed, "searchLessonsLearned(\"SQL\") returned "
				+ (results == null ? "null" : "a list of size " + results.size())
				+ " (expected a non-null, empty list)");
	}


	/* ====================== US-3.1 / US-3.2: Update ====================== */

	/**
	 * Verifies updating effort and time values on an existing record.
	 */
	private static void testTC3_1_AddEffortAndTime() {
		LessonLearned lesson = db.getLessonLearnedById(lesson2Id);
		if (lesson == null) {
			report("TC-3.1", false, "Lesson 2 (id " + lesson2Id + ") could not be loaded.");
			return;
		}
		lesson.setEffort(6.5);
		lesson.setTime(4.0);
		String validation = lesson.validate();
		boolean updated = validation.isEmpty() && db.updateLessonLearned(lesson);

		LessonLearned readBack = db.getLessonLearnedById(lesson2Id);
		boolean passed = updated && readBack != null && readBack.getEffort() == 6.5
				&& readBack.getTime() == 4.0;
		report("TC-3.1", passed, "validate()=\"" + validation + "\", updateLessonLearned()="
				+ updated + ", read-back effort=" + (readBack != null ? readBack.getEffort() : "?")
				+ ", time=" + (readBack != null ? readBack.getTime() : "?"));
	}

	/**
	 * Verifies rejection when non-numeric text is entered in numeric fields.
	 */
	private static void testTC3_2a_LettersInEffortField() {
		String result = LessonLearned.validateNumericField("Effort", "five");
		String expected = "Error: Effort and time must be positive numbers.\n";
		boolean passed = result.equals(expected);
		report("TC-3.2a", passed, "validateNumericField(\"Effort\", \"five\") returned \""
				+ result + "\"; expected \"" + expected + "\"");
	}

	/**
	 * Verifies rejection when negative values are provided for time or effort fields.
	 */
	private static void testTC3_2b_NegativeNumberInTimeField() {
		String rawResult = LessonLearned.validateNumericField("Time", "-2.5");
		String expected = "Error: Effort and time must be positive numbers.\n";

		LessonLearned lesson = db.getLessonLearnedById(lesson2Id);
		if (lesson == null) {
			report("TC-3.2b", false, "Lesson 2 (id " + lesson2Id + ") could not be loaded.");
			return;
		}
		lesson.setEffort(-1.0);
		String objectResult = lesson.validate();

		boolean passed = rawResult.equals(expected) && objectResult.contains(expected.trim());
		report("TC-3.2b", passed, "validateNumericField(\"Time\", \"-2.5\")=\"" + rawResult
				+ "\"; validate() with effort=-1.0 returned \"" + objectResult + "\"");
	}


	/* ====================== US-4.1 / US-4.2: Delete ====================== */

	/**
	 * Verifies successful deletion of an unlocked lesson record.
	 */
	private static void testTC4_1_DeleteUnlockedLesson() {
		String canDelete = db.canDeleteLessonLearned(lesson2Id);
		boolean deleted = canDelete.isEmpty() && db.deleteLessonLearned(lesson2Id);
		LessonLearned readBack = db.getLessonLearnedById(lesson2Id);

		boolean passed = canDelete.isEmpty() && deleted && readBack == null;
		report("TC-4.1", passed, "canDeleteLessonLearned(" + lesson2Id + ")=\"" + canDelete
				+ "\", deleteLessonLearned()=" + deleted
				+ ", getLessonLearnedById() after delete="
				+ (readBack == null ? "null (gone)" : "still exists"));
	}

	/**
	 * Verifies deletion protection safeguards for locked records.
	 */
	private static void testTC4_2a_DeleteLockedLesson() {
		String canDelete = db.canDeleteLessonLearned(lesson4Id);
		String expected = "Error: Cannot delete a locked lesson.\n";
		LessonLearned stillThere = db.getLessonLearnedById(lesson4Id);

		boolean passed = canDelete.equals(expected) && stillThere != null && stillThere.isLocked();
		report("TC-4.2a", passed, "canDeleteLessonLearned(" + lesson4Id + ")=\"" + canDelete
				+ "\"; expected \"" + expected + "\"; lesson still exists and is locked="
				+ (stillThere != null && stillThere.isLocked()));
	}

	/**
	 * Verifies error handling when attempting to delete a non-existent record ID.
	 */
	private static void testTC4_2b_DeleteNonExistentLesson() {
		String canDelete;
		boolean threw = false;
		try {
			canDelete = db.canDeleteLessonLearned(NONEXISTENT_ID);
		} catch (Exception e) {
			threw = true;
			canDelete = e.toString();
		}
		String expected = "Error: No lesson exists with that id.\n";

		boolean passed = !threw && canDelete.equals(expected);
		report("TC-4.2b", passed, "canDeleteLessonLearned(" + NONEXISTENT_ID + ")=\""
				+ canDelete + "\"; expected \"" + expected + "\"; threw an exception=" + threw);
	}
}