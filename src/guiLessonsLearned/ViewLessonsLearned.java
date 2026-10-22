package guiLessonsLearned;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import javafx.scene.control.CheckBox;

/*******
 * <p> Title: ViewLessonsLearned Class. </p>
 * 
 * <p> Description: The Java/FX-based Lessons Learned Page. This page allows a Contributor to create,
 * read, update, and delete lessons learned, fulfilling the TP2 requirements. This page is patterned 
 * after the same manual-layout, singleton-Scene approach as the other role home pages in this 
 * project (e.g., ViewContributorHome). </p>
 * 
 * <p> Copyright: Team 16 2026 </p>
 * 
 * @author Hrishita Nangunoori
 * 
 * @version 1.00		2026-10-04 Initial version for HW2 Task 4
 * @version 1.01 		2026-10-24 Version for TP2
 *  
 */

public class ViewLessonsLearned {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: page title, current user, and navigation back to the Contributor home page
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_Back = new Button("Back");

	protected static Line line_Separator1 = new Line(20, 60, width - 20, 60);

	// GUI Area 2, left half: the entry form used for both Create and Update
	protected static Label label_FormTitle = new Label("Lesson Details");
	protected static Label label_SelectedId = new Label("New Lesson (nothing selected)");

	protected static Label label_Title = new Label("Title:");
	protected static TextField text_Title = new TextField();

	protected static Label label_Category = new Label("Category:");
	protected static TextField text_Category = new TextField();

	protected static Label label_Description = new Label("Description:");
	protected static TextArea text_Description = new TextArea();

	protected static Label label_Recommendation = new Label("Recommendation:");
	protected static TextArea text_Recommendation = new TextArea();

	protected static Label label_Effort = new Label("Effort (hours):");
	protected static TextField text_Effort = new TextField();

	protected static Label label_Time = new Label("Time (hours):");
	protected static TextField text_Time = new TextField();

	// This label shows validation errors or success messages, following the same red/green
	// convention used by passwordPopUpWindow.View
	protected static Label label_Status = new Label();

	protected static Button button_Add = new Button("Add New Lesson");
	protected static Button button_Update = new Button("Save Changes");
	protected static Button button_Delete = new Button("Delete Selected");
	protected static Button button_Clear = new Button("Clear / New");
	
	// Lock checkbox: only meaningful when creating a lesson. Once saved, the lock state is shown
	// read-only (see ModelLessonsLearned.selectFromList).
	protected static CheckBox checkBox_Lock = new CheckBox("Lock Lesson");

	protected static Line line_Separator2 = new Line(400, 70, 400, height - 100);

	// GUI Area 2, right half: search and the list of lessons
	protected static Label label_ListTitle = new Label("Lessons Learned");
	protected static TextField text_SearchKeyword = new TextField();
	protected static Button button_Search = new Button("Search");
	protected static Button button_ShowAll = new Button("Show All");
	protected static Button button_ShowMine = new Button("Show Only Mine");
	protected static ListView<String> list_Lessons = new ListView<String>();

	protected static Line line_Separator4 = new Line(20, height - 80, width - 20, height - 80);

	// GUI Area 3: logout and quit, matching the convention used on every role home page
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewLessonsLearned theView;

	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewLessonsLearnedScene;

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayLessonsLearned(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Lessons Learned page to be displayed. It mirrors displayContributorHome in structure:
	 * establish shared attributes, instantiate the singleton the first time, refresh the dynamic
	 * content, then show the page. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 * 
	 * @param user specifies the User for this GUI and its methods
	 * 
	 */
	public static void displayLessonsLearned(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewLessonsLearned();

		label_UserDetails.setText("User: " + theUser.getUserName());
		ModelLessonsLearned.clearForm();
		ModelLessonsLearned.refreshFullList();

		theStage.setTitle("CSE 360 Foundations: Lessons Learned");
		theStage.setScene(theViewLessonsLearnedScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewLessonsLearned() </p>
	 * 
	 * <p> Description: This constructor initializes every element of the graphical user
	 * interface exactly once, following the same singleton pattern used throughout this
	 * project. </p>
	 * 
	 */
	private ViewLessonsLearned() {

		theRootPane = new Pane();
		theViewLessonsLearnedScene = new Scene(theRootPane, width, height);
		theRootPane.getStylesheets().add(
				getClass().getResource("/applicationMain/application.css").toExternalForm());

		// GUI Area 1
		label_PageTitle.setText("Lessons Learned");
		setupLabelUI(label_PageTitle, "Arial", 24, 400, Pos.CENTER_LEFT, 20, 10);

		setupLabelUI(label_UserDetails, "Arial", 14, 300, Pos.CENTER_LEFT, 450, 15);

		setupButtonUI(button_Back, "Dialog", 14, 100, Pos.CENTER, width - 120, 10);
		button_Back.setOnAction((_) -> { ControllerLessonsLearned.performBack(); });

		// GUI Area 2, left half: the form
		setupLabelUI(label_FormTitle, "Arial", 16, 360, Pos.CENTER_LEFT, 20, 70);
		setupLabelUI(label_SelectedId, "Arial", 12, 360, Pos.CENTER_LEFT, 20, 95);

		setupLabelUI(label_Title, "Arial", 12, 100, Pos.CENTER_LEFT, 20, 120);
		setupTextFieldUI(text_Title, 20, 138, 360);

		setupLabelUI(label_Category, "Arial", 12, 100, Pos.CENTER_LEFT, 20, 168);
		setupTextFieldUI(text_Category, 20, 186, 360);

		setupLabelUI(label_Description, "Arial", 12, 200, Pos.CENTER_LEFT, 20, 216);
		text_Description.setLayoutX(20);
		text_Description.setLayoutY(234);
		text_Description.setPrefSize(360, 50);
		text_Description.setWrapText(true);

		setupLabelUI(label_Recommendation, "Arial", 12, 200, Pos.CENTER_LEFT, 20, 288);
		text_Recommendation.setLayoutX(20);
		text_Recommendation.setLayoutY(304);
		text_Recommendation.setPrefSize(360, 50);
		text_Recommendation.setWrapText(true);

		setupLabelUI(label_Effort, "Arial", 12, 170, Pos.CENTER_LEFT, 20, 362);
		setupTextFieldUI(text_Effort, 20, 378, 170);

		setupLabelUI(label_Time, "Arial", 12, 170, Pos.CENTER_LEFT, 210, 362);
		setupTextFieldUI(text_Time, 210, 378, 170);

		setupLabelUI(label_Status, "Arial", 12, 360, Pos.CENTER_LEFT, 20, 408);
		label_Status.setWrapText(true);
		label_Status.setPrefHeight(35);

		setupButtonUI(button_Add, "Dialog", 13, 170, Pos.CENTER, 20, 450);
		button_Add.setOnAction((_) -> { ControllerLessonsLearned.performAdd(); });

		setupButtonUI(button_Update, "Dialog", 13, 170, Pos.CENTER, 210, 450);
		button_Update.setOnAction((_) -> { ControllerLessonsLearned.performUpdate(); });

		setupButtonUI(button_Delete, "Dialog", 13, 170, Pos.CENTER, 20, 484);
		button_Delete.setOnAction((_) -> { ControllerLessonsLearned.performDelete(); });

		setupButtonUI(button_Clear, "Dialog", 13, 170, Pos.CENTER, 210, 484);
		button_Clear.setOnAction((_) -> { ControllerLessonsLearned.performClear(); });

		// GUI Area 2, right half: search and list
		setupLabelUI(label_ListTitle, "Arial", 16, 360, Pos.CENTER_LEFT, 420, 70);

		setupTextFieldUI(text_SearchKeyword, 420, 95, 220);
		text_SearchKeyword.setPromptText("keyword...");

		setupButtonUI(button_Search, "Dialog", 12, 110, Pos.CENTER, 650, 95);
		button_Search.setOnAction((_) -> { ControllerLessonsLearned.performSearch(); });

		setupButtonUI(button_ShowAll, "Dialog", 12, 170, Pos.CENTER, 420, 128);
		button_ShowAll.setOnAction((_) -> { ControllerLessonsLearned.performShowAll(); });

		setupButtonUI(button_ShowMine, "Dialog", 12, 170, Pos.CENTER, 600, 128);
		button_ShowMine.setOnAction((_) -> { ControllerLessonsLearned.performShowMine(); });

		list_Lessons.setLayoutX(420);
		list_Lessons.setLayoutY(164);
		list_Lessons.setPrefSize(360, 330);
		list_Lessons.getSelectionModel().selectedIndexProperty().addListener(
				(obs, oldIndex, newIndex) -> {
					if (newIndex != null && newIndex.intValue() >= 0) {
						ControllerLessonsLearned.performSelect(newIndex.intValue());
					}
				});

		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 16, 200, Pos.CENTER, 20, height - 70);
		button_Logout.setOnAction((_) -> { ControllerLessonsLearned.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 16, 200, Pos.CENTER, 250, height - 70);
		button_Quit.setOnAction((_) -> { ControllerLessonsLearned.performQuit(); });
		
		// Lock checkbox placed in empty space to the right of the "Lesson Details" heading
		checkBox_Lock.setFont(Font.font("Arial", 12));
		checkBox_Lock.setLayoutX(250);
		checkBox_Lock.setLayoutY(74);
		checkBox_Lock.setStyle("-fx-text-fill: white;");

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Back, line_Separator1,
				label_FormTitle, label_SelectedId,
				label_Title, text_Title,
				label_Category, text_Category,
				label_Description, text_Description,
				label_Recommendation, text_Recommendation,
				label_Effort, text_Effort,
				label_Time, text_Time,
				label_Status,
				button_Add, button_Update, button_Delete, button_Clear,
				line_Separator2,
				label_ListTitle, text_SearchKeyword, button_Search, button_ShowAll,
				button_ShowMine, list_Lessons,
				line_Separator4, button_Logout, button_Quit);
		
		theRootPane.getChildren().add(checkBox_Lock);
	}

	/**********
	 * <p> Method: void setListItems(java.util.List&lt;String&gt; items) </p>
	 * 
	 * <p> Description: Replaces the contents of the ListView with the given display strings,
	 * used by the Model after any Create, Read, Update, or Delete operation so the list always
	 * reflects what is actually in the database. </p>
	 * 
	 * @param items specifies the display strings to show, one per lesson
	 * 
	 */
	protected static void setListItems(java.util.List<String> items) {
		ObservableList<String> observable = FXCollections.observableArrayList(items);
		list_Lessons.setItems(observable);
	}

	/*-********************************************************************************************

	Helper methods

	 */
	
	/**********
	 * Private local method to initialize the fields for a label
	 * 
	 * @param l 	The Label object to be initialized
	 * @param ff	font used
	 * @param f		The size of the font
	 * @param w		The width of the label
	 * @param p		The alignment used
	 * @param x		The location from the x-axis
	 * @param y		The location from the y-axis
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize fields for the button UI
	 * 
	 * @param b		The button object to be initialized	
	 * @param ff	The font used for the button
	 * @param f		The size of the font
	 * @param w		The width of the button
	 * @param p		The alignment
	 * @param x		The location from the x-axis
	 * @param y		The location from the y-axis 
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize fields for the text field
	 * 
	 * @param t		The TextField object to be initialized
	 * @param x		The location from the x-axis
	 * @param y		The location from the y-axis
	 * @param w		The preferred width of the TextField
	 */
	private static void setupTextFieldUI(TextField t, double x, double y, double w) {
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setPrefWidth(w);
	}
}