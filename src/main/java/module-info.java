module TASLauncher {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive AccountAPI;
	opens de.pfannekuchen.taslauncher to javafx.fxml;
	exports de.pfannekuchen.taslauncher;
}