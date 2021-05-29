module TASBattleLauncher {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	opens de.pfannekuchen.tasbattlelauncher to javafx.fxml;
	exports de.pfannekuchen.tasbattlelauncher;
}