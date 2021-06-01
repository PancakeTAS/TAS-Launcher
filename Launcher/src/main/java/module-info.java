module TASBattleLauncher {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.web;
	requires transitive AccountAPI;
	requires transitive jdk.jsobject;
	opens de.pfannekuchen.tasbattlelauncher to javafx.fxml;
	exports de.pfannekuchen.tasbattlelauncher;
}