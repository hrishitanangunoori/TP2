package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: LessonsLearnedList Class </p>
 * 
 * <p> Description: This class represents a collection of LessonLearned objects. A single
 * LessonsLearnedList instance can represent the complete set of lessons currently in the
 * database, or it can represent any subset of them (for example, the results of a search or a
 * filter by category). The subset may be empty, may contain one element, or may be arbitrarily
 * large -- there is no fixed upper limit. </p>
 * 
 * <p> Copyright: Team 16 2026 </p>
 * 
 * @author Hrishita Nangunoori
 * 
 * @version 1.00		2026-09-28 Initial implementation for HW2
 * 
 */

public class LessonsLearnedList {

	/*
	 * The underlying storage for this list. This is never null; an empty list represents zero
	 * lessons rather than the absence of a list.
	 */
	private List<LessonLearned> lessons;


	/*****
	 * <p> Method: LessonsLearnedList() </p>
	 * 
	 * <p> Description: This default constructor establishes an empty list of lessons. This is the
	 * correct starting point for building up a subset (e.g., search results) one lesson at a
	 * time. </p>
	 */
	public LessonsLearnedList() {
		this.lessons = new ArrayList<LessonLearned>();
	}


	/*****
	 * <p> Method: LessonsLearnedList(List&lt;LessonLearned&gt; lessons) </p>
	 * 
	 * <p> Description: This constructor wraps an already-populated list of lessons, typically one
	 * produced by the database layer (e.g., the result of "get all lessons" or "search for
	 * lessons matching a keyword"). </p>
	 * 
	 * @param lessons specifies the list of LessonLearned objects this object will hold. If null is
	 * passed, this object holds an empty list instead of throwing an exception.
	 * 
	 */
	public LessonsLearnedList(List<LessonLearned> lessons) {
		this.lessons = (lessons != null) ? lessons : new ArrayList<LessonLearned>();
	}


	/*****
	 * <p> Method: void add(LessonLearned lesson) </p>
	 * 
	 * <p> Description: Adds a single lesson to this list. </p>
	 * 
	 * @param lesson specifies the LessonLearned to add
	 * 
	 */
	public void add(LessonLearned lesson) {
		lessons.add(lesson);
	}


	/*****
	 * <p> Method: boolean removeById(int id) </p>
	 * 
	 * <p> Description: Removes, from this in-memory list only, the lesson with the specified id.
	 * This does not affect the database; it is intended for adjusting an in-memory subset. </p>
	 * 
	 * @param id specifies the id of the lesson to remove
	 * 
	 * @return true if a lesson with that id was found and removed, else false
	 * 
	 */
	public boolean removeById(int id) {
		return lessons.removeIf(l -> l.getId() == id);
	}


	/*****
	 * <p> Method: LessonLearned getById(int id) </p>
	 * 
	 * <p> Description: Finds and returns the lesson in this list with the specified id. </p>
	 * 
	 * @param id specifies the id to search for
	 * 
	 * @return the matching LessonLearned, or null if no lesson in this list has that id
	 * 
	 */
	public LessonLearned getById(int id) {
		for (LessonLearned l : lessons) {
			if (l.getId() == id) return l;
		}
		return null;
	}


	/*****
	 * <p> Method: List&lt;LessonLearned&gt; getAll() </p>
	 * 
	 * <p> Description: Returns the underlying list of lessons held by this object. This may be the
	 * complete set of all lessons, or any subset, depending on how this object was populated. </p>
	 * 
	 * @return the List of LessonLearned objects held by this object; never null, possibly empty
	 * 
	 */
	public List<LessonLearned> getAll() {
		return lessons;
	}


	/*****
	 * <p> Method: int size() </p>
	 * 
	 * <p> Description: Returns the number of lessons currently held by this object. </p>
	 * 
	 * @return the number of lessons in this list
	 * 
	 */
	public int size() {
		return lessons.size();
	}


	/*****
	 * <p> Method: boolean isEmpty() </p>
	 * 
	 * <p> Description: Indicates whether this list currently holds any lessons. This is expected
	 * to be true, for example, when a search or filter matches nothing. </p>
	 * 
	 * @return true if this list holds no lessons, else false
	 * 
	 */
	public boolean isEmpty() {
		return lessons.isEmpty();
	}


	/*****
	 * <p> Method: LessonsLearnedList filterByCategory(String category) </p>
	 * 
	 * <p> Description: Produces a new LessonsLearnedList containing only the lessons in this list
	 * whose category matches the specified category (case-insensitive). </p>
	 * 
	 * @param category specifies the category to filter by
	 * 
	 * @return a new LessonsLearnedList containing only the matching lessons; may be empty
	 * 
	 */
	public LessonsLearnedList filterByCategory(String category) {
		LessonsLearnedList result = new LessonsLearnedList();
		if (category == null) return result;
		for (LessonLearned l : lessons) {
			if (l.getCategory() != null && l.getCategory().equalsIgnoreCase(category)) {
				result.add(l);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: LessonsLearnedList filterBySubmittedBy(String userName) </p>
	 * 
	 * <p> Description: Produces a new LessonsLearnedList containing only the lessons in this list
	 * that were submitted by the specified userName. This supports US-2.1, which scopes a
	 * contributor's "list of all lessons" to the lessons that contributor personally added. </p>
	 * 
	 * @param userName specifies the submittedBy userName to filter by
	 * 
	 * @return a new LessonsLearnedList containing only the matching lessons; may be empty
	 * 
	 */
	public LessonsLearnedList filterBySubmittedBy(String userName) {
		LessonsLearnedList result = new LessonsLearnedList();
		if (userName == null) return result;
		for (LessonLearned l : lessons) {
			if (userName.equals(l.getSubmittedBy())) {
				result.add(l);
			}
		}
		return result;
	}


	/*****
	 * <p> Method: LessonsLearnedList search(String keyword) </p>
	 * 
	 * <p> Description: Produces a new LessonsLearnedList containing only the lessons in this list
	 * whose title, description, or recommendation contains the specified keyword
	 * (case-insensitive). </p>
	 * 
	 * @param keyword specifies the text to search for
	 * 
	 * @return a new LessonsLearnedList containing only the matching lessons; may be empty
	 * 
	 */
	public LessonsLearnedList search(String keyword) {
		LessonsLearnedList result = new LessonsLearnedList();
		if (keyword == null || keyword.trim().isEmpty()) return result;
		String needle = keyword.trim().toLowerCase();
		for (LessonLearned l : lessons) {
			boolean matches =
					(l.getTitle() != null && l.getTitle().toLowerCase().contains(needle)) ||
					(l.getDescription() != null && l.getDescription().toLowerCase().contains(needle)) ||
					(l.getRecommendation() != null && l.getRecommendation().toLowerCase().contains(needle));
			if (matches) result.add(l);
		}
		return result;
	}
}