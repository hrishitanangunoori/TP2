package entityClasses;

import java.time.LocalDate;

/*******
 * <p> Title: LessonLearned Class </p>
 * 
 * <p> Description: This LessonLearned class represents a single lesson-learned entity in the
 * system.  It captures a situation that occurred (description), the recommendation that resulted
 * from it (recommendation), and the metadata needed to track who submitted it and when. </p>
 * 
 * <p> Copyright: Team 16 2026 </p>
 * 
 * @author Hrishita Nangunoori
 * 
 * @version 1.00		2026-09-28 Initial implementation for HW2
 * 
 */

public class LessonLearned {

	/*
	 * These are the private attributes for this entity object
	 */
	private int id;					
	private String title;				
	private String category;		
	private String description;		
	private String recommendation;		
	private String submittedBy;		
	private String dateSubmitted;		
	private double effort;				
	private double time;				
	private boolean locked;		
										


	/*****
	 * <p> Method: LessonLearned() </p>
	 * 
	 * <p> Description: This default constructor is used by the database layer when reconstructing
	 * a LessonLearned object from a stored row. It leaves all fields at their default values so
	 * they can be set individually. </p>
	 */
	public LessonLearned() {
		this.id = -1;
		this.effort = 0.0;
		this.time = 0.0;
		this.locked = false;
	}


	/*****
	 * <p> Method: LessonLearned(String title, String category, String description,
	 * 		String recommendation, String submittedBy) </p>
	 * 
	 * <p> Description: This constructor is used to create a brand-new lesson learned that has not
	 * yet been persisted. The id is set to -1 (meaning "not yet assigned") and the submission date
	 * is automatically set to today. The database layer is responsible for assigning the real id
	 * once the record is inserted. </p>
	 * 
	 * @param title specifies the short summary of this lesson
	 * 
	 * @param category specifies the category this lesson falls under
	 * 
	 * @param description specifies what happened / the situation behind this lesson
	 * 
	 * @param recommendation specifies the actual lesson learned / recommendation going forward
	 * 
	 * @param submittedBy specifies the userName of the contributor submitting this lesson
	 * 
	 */
	public LessonLearned(String title, String category, String description, String recommendation,
			String submittedBy) {
		this.id = -1;
		this.title = title;
		this.category = category;
		this.description = description;
		this.recommendation = recommendation;
		this.submittedBy = submittedBy;
		this.dateSubmitted = LocalDate.now().toString();
		this.effort = 0.0;
		this.time = 0.0;
		this.locked = false;
	}


	/*****
	 * <p> Method: LessonLearned(int id, String title, String category, String description,
	 * 		String recommendation, String submittedBy, String dateSubmitted, double effort,
	 * 		double time, boolean locked) </p>
	 * 
	 * <p> Description: This constructor is used by the database layer to reconstruct a
	 * LessonLearned object that was read back from a stored row, where every field is already
	 * known. </p>
	 * 
	 * @param id specifies the database-assigned primary key for this lesson
	 * 
	 * @param title specifies the short summary of this lesson
	 * 
	 * @param category specifies the category this lesson falls under
	 * 
	 * @param description specifies what happened / the situation behind this lesson
	 * 
	 * @param recommendation specifies the actual lesson learned / recommendation going forward
	 * 
	 * @param submittedBy specifies the userName of the contributor who submitted this lesson
	 * 
	 * @param dateSubmitted specifies the date, in yyyy-MM-dd format, this lesson was submitted
	 * 
	 * @param effort specifies the effort (e.g., person-hours) recorded against this lesson
	 * 
	 * @param time specifies the time spent (e.g., hours) recorded against this lesson
	 * 
	 * @param locked specifies whether another role has locked this lesson against edit/delete
	 * 
	 */
	public LessonLearned(int id, String title, String category, String description,
			String recommendation, String submittedBy, String dateSubmitted, double effort,
			double time, boolean locked) {
		this.id = id;
		this.title = title;
		this.category = category;
		this.description = description;
		this.recommendation = recommendation;
		this.submittedBy = submittedBy;
		this.dateSubmitted = dateSubmitted;
		this.effort = effort;
		this.time = time;
		this.locked = locked;
	}


	/*****
	 * <p> Method: String validate() </p>
	 * 
	 * <p> Description: This method checks every field required for a lesson learned to be
	 * considered well-formed. It follows the same convention used elsewhere in this project (see
	 * passwordPopUpWindow.Model.evaluatePassword): if the object is valid, an empty string is
	 * returned; if it is not valid, a human-readable message describing every problem found is
	 * returned so the caller can display something genuinely helpful to the user, rather than a
	 * generic "invalid input" message. </p>
	 * 
	 * @return an empty string if this lesson is valid, otherwise a message describing every
	 * validation failure found, separated by newlines.
	 * 
	 */
	public String validate() {
		StringBuilder errors = new StringBuilder();

		boolean titleBlank = (title == null || title.trim().isEmpty());
		boolean descriptionBlank = (description == null || description.trim().isEmpty());

		if (titleBlank && descriptionBlank) {
			errors.append("Error: Title and Description cannot be empty.\n");
		} else {
			if (titleBlank) {
				errors.append("Error: Title cannot be empty.\n");
			} else if (title.length() > 100) {
				errors.append("Error: Title cannot be longer than 100 characters.\n");
			}
			if (descriptionBlank) {
				errors.append("Error: Description cannot be empty.\n");
			} else if (description.trim().length() < 10) {
				errors.append("Error: Description must be at least 10 characters long so it is "
						+ "meaningful.\n");
			}
		}

		if (category == null || category.trim().isEmpty()) {
			errors.append("Error: Category cannot be empty.\n");
		}

		if (recommendation == null || recommendation.trim().isEmpty()) {
			errors.append("Error: Recommendation cannot be empty.\n");
		} else if (recommendation.trim().length() < 10) {
			errors.append("Error: Recommendation must be at least 10 characters long so it is "
					+ "meaningful.\n");
		}

		if (submittedBy == null || submittedBy.trim().isEmpty()) {
			errors.append("Error: SubmittedBy (the contributor's userName) cannot be empty.\n");
		}

		if (effort < 0 || time < 0) {
			errors.append("Error: Effort and time must be positive numbers.\n");
		}

		return errors.toString();
	}


	/*****
	 * <p> Method: static String validateNumericField(String fieldLabel, String rawValue) </p>
	 * 
	 * <p> Description: Validates a raw String value -- exactly the kind of value a GUI text field
	 * produces -- intended to become the effort or time attribute. This exists separately from
	 * validate() because a non-numeric String (e.g., the user typed letters) can never be assigned
	 * to the effort/time double fields in the first place; it must be caught before parsing, not
	 * after. Follows the same "empty string means valid" convention as validate(). </p>
	 * 
	 * @param fieldLabel specifies which field is being checked, e.g. "Effort" or "Time", used only
	 * to make the returned message specific
	 * 
	 * @param rawValue specifies the raw text entered by the user
	 * 
	 * @return an empty string if rawValue is a non-negative number, otherwise
	 * "Error: Effort and time must be positive numbers."
	 * 
	 */
	public static String validateNumericField(String fieldLabel, String rawValue) {
		if (rawValue == null || rawValue.trim().isEmpty()) {
			return "Error: " + fieldLabel + " cannot be empty.\n";
		}
		try {
			double value = Double.parseDouble(rawValue.trim());
			if (value < 0) {
				return "Error: Effort and time must be positive numbers.\n";
			}
		} catch (NumberFormatException e) {
			return "Error: Effort and time must be positive numbers.\n";
		}
		return "";
	}


	// Getters for every attribute, matching the style used in entityClasses.User

	public int getId() { return id; }
	public String getTitle() { return title; }
	public String getCategory() { return category; }
	public String getDescription() { return description; }
	public String getRecommendation() { return recommendation; }
	public String getSubmittedBy() { return submittedBy; }
	public String getDateSubmitted() { return dateSubmitted; }
	public double getEffort() { return effort; }
	public double getTime() { return time; }

	/*****
	 * <p> Method: boolean isLocked() </p>
	 * 
	 * <p> Description: Indicates whether this lesson has been locked against edit/delete by
	 * another role. HW2 provides no screen that sets this to true; it is only ever set directly by
	 * test data to exercise US-2.2 and US-4.2. </p>
	 * 
	 * @return true if this lesson is locked, else false
	 * 
	 */
	public boolean isLocked() { return locked; }


	// Setters for every mutable attribute (id and dateSubmitted are controlled by the database layer)

	public void setId(int id) { this.id = id; }
	public void setTitle(String title) { this.title = title; }
	public void setCategory(String category) { this.category = category; }
	public void setDescription(String description) { this.description = description; }
	public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
	public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
	public void setDateSubmitted(String dateSubmitted) { this.dateSubmitted = dateSubmitted; }
	public void setEffort(double effort) { this.effort = effort; }
	public void setTime(double time) { this.time = time; }

	/*****
	 * <p> Method: void setLocked(boolean locked) </p>
	 * 
	 * <p> Description: Sets whether this lesson is locked. This setter exists only for use by
	 * test data setup to seed a locked lesson, since HW2 builds no Curator-role screen that would
	 * normally be responsible for locking a lesson. </p>
	 * 
	 * @param locked specifies the new locked state
	 * 
	 */
	public void setLocked(boolean locked) { this.locked = locked; }


	/*****
	 * <p> Method: String toString() </p>
	 * 
	 * <p> Description: Produces a single-line, human-readable summary of this lesson, useful for
	 * quick display in lists and for debugging. </p>
	 * 
	 * @return a formatted String summarizing this lesson
	 * 
	 */
	@Override
	public String toString() {
		return "[" + id + "] " + title + "  (" + category + ")  submitted by " + submittedBy +
				" on " + dateSubmitted;
	}
}