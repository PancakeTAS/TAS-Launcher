package de.pfannekuchen.tasbattlelauncher;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

/**
 * Entry Point for the TAS Battle Launcher (JavaFX Application)
 * @author Pancake
 */
public class TASBattleLauncher extends Application {

	/**
	 * You cannot start JavaFX from the Class itself, so here is a static method to do that
	 */
	public static void start() {
		launch(); // Launch JavaFX in this Thread
	}
	
	/**
	 * Main of Gui
	 */
	@Override public void start(Stage stage) throws Exception {
		/* Load FXML File and display it */
		FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
		stage.setScene(new Scene(loader.load()));
		stage.show();
	}
	
	/**
	 * Automatically called by the Gui once you click on "Start Client"
	 * TODO: Start the Minecraft Client and or Set it up
	 */
	@FXML private void startClient() {
		
	}
	
	/**
	 * Automatically called by the Gui once you click on "Settings"
	 * TODO: Open Settings Menu here
	 */
	@FXML private void openSettings() {
		
	}

	/**
	 * Automatically called by the Gui once you click on "Bedwars"
	 * TODO: Fetch Bedwars Information and Display Pane
	 */
	@FXML private void selectBedwars() {
		
	}
	
	/**
	 * Automatically called by the Gui once you click on "Skywars"
	 * TODO: Fetch Skywars Information and Display Pane
	 */
	@FXML private void selectSkywars() {
		
	}
	
	/**
	 * Automatically called by the Gui once you click on "Login"
	 * TODO: Show a Pop-up where you can log into Microsoft or Mojang Account
	 */
	@FXML private void openLoginDialog() throws IOException {
		Stage stage = new Stage();
		stage.setScene(new Scene(new FXMLLoader(getClass().getResource("Login.fxml")).load()));
		stage.show();
	}
	
	@FXML private ProgressIndicator indicator;
	@FXML private Label errorlabel;
	
	/**
	 * Automatically called by the Gui once you click on "Sign-in" in the Login Dialog Pane
	 * TODO: Sign into the Mojang Account
	 */
	@FXML private void login_signin() {
		indicator.setVisible(true);
	}
	
	/**
	 * Automatically called by the Gui once you click on "Microsoft Login" in the Login Dialog Pane
	 * TODO: Sign into the Microsoft Account
	 */
	@FXML private void login_microsoft() {

	}
	
}
