module TASBattleLauncher {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive AccountAPI;
	opens de.pfannekuchen.tasbattlelauncher to javafx.fxml;
	exports de.pfannekuchen.tasbattlelauncher;
}