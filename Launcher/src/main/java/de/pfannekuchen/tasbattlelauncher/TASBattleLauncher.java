package de.pfannekuchen.tasbattlelauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import de.pfannekuchen.accountapi.MicrosoftAccount;
import de.pfannekuchen.accountapi.MojangAccount;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Entry Point for the TAS Battle Launcher (JavaFX Application)
 * @author Pancake
 */
public class TASBattleLauncher extends Application {

	public static final File accountsFile = new File("accounts");
	public static Object mcaccount;
	private static Label accountlabel;
	
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
		accountlabel = (Label) ((HBox) ((VBox) ((BorderPane) stage.getScene().getRoot()).getLeft()).getChildren().get(0)).getChildren().get(0);
		/* Thread for Loading an Account from the Accounts File */
		Thread accountLoader = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					/* Read Lines from File */
					accountsFile.createNewFile();
					List<String> lines = Files.readAllLines(accountsFile.toPath());
					if (lines.size() > 0) {
						/* Try to Log into the Account */
						try {
							String name;
							if (lines.get(0).equalsIgnoreCase("Microsoft Token")) {
								mcaccount = new MicrosoftAccount(lines.get(1));
								name = ((MicrosoftAccount) mcaccount).getUsername();
								// Write updated Stuff into the File
								PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
								writer.println("Microsoft Token");
								writer.println(((MicrosoftAccount) mcaccount).getAccountToken());
								writer.close();
							} else {
								mcaccount = new MojangAccount(lines.get(1), UUID.fromString(lines.get(2)));
								name = ((MojangAccount) mcaccount).getUsername();
								// Write updated Stuff into the File
								PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
								writer.println("Mojang Access Token");
								writer.println(((MojangAccount) mcaccount).getAccessToken());
								writer.println(((MojangAccount) mcaccount).getClientUuid().toString());
								writer.close();
							}
							Platform.runLater(() -> {
								accountlabel.setText(name);
							});
						} catch (Exception e) {
							System.err.println("Could not load MC Account from File");
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		accountLoader.setName("Account-Loader Thread");
		accountLoader.setDaemon(true);
		accountLoader.start();
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
		loginStage = new Stage();
		loginStage.setScene(new Scene(new FXMLLoader(getClass().getResource("Login.fxml")).load()));
		loginStage.show();
	}
	
	private static Stage loginStage;
	@FXML private ProgressIndicator indicator;
	@FXML private Label errorlabel;
	@FXML private TextField textField;
	@FXML private PasswordField passwordField;
	
	/**
	 * Automatically called by the Gui once you click on "Sign-in" in the Login Dialog Pane
	 * TODO: Sign into the Mojang Account
	 */
	@FXML private void login_signin() {
		indicator.setVisible(true);
		Thread login_thread = new Thread(() -> {
			try {
				MojangAccount account = new MojangAccount(textField.getText(), passwordField.getText());
				TASBattleLauncher.mcaccount = account;
				/* Write Account to File */
				PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
				writer.println("Mojang Access Token");
				writer.println(account.getAccessToken());
				writer.println(account.getClientUuid().toString());
				writer.close();
				/* Close Dialog */
				Platform.runLater(() -> {
					accountlabel.setText(account.getUsername());
					loginStage.close();
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					errorlabel.setText("Error: Invalid Credentials");
					errorlabel.setVisible(true);
				});
				e.printStackTrace();
			}
			indicator.setVisible(false);
		});
		login_thread.setDaemon(true);
		login_thread.setName("Login Thread");
		login_thread.start();
	}
	
	/**
	 * Automatically called by the Gui once you click on "Microsoft Login" in the Login Dialog Pane
	 * TODO: Sign into the Microsoft Account
	 */
	@FXML private void login_microsoft() {
		indicator.setVisible(true);
		Thread login_thread = new Thread(() -> {
			try {
				MicrosoftAccount account = new MicrosoftAccount();
				if (!account.ownsMinecraft()) throw new Exception("Account does not own the Game");
				TASBattleLauncher.mcaccount = account;
				/* Write Account to File */
				PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
				writer.println("Microsoft Token");
				writer.println(account.getAccountToken());
				writer.close();
				/* Close Dialog */
				Platform.runLater(() -> {
					accountlabel.setText(account.getUsername());
					loginStage.close();
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					errorlabel.setText("Error: " + (e.getMessage().isEmpty() ? "Check Console." : e.getMessage()));
					errorlabel.setVisible(true);
				});
				e.printStackTrace();
			}
			indicator.setVisible(false);
		});
		login_thread.setDaemon(true);
		login_thread.setName("Login Thread");
		login_thread.start();
	}
	
}
