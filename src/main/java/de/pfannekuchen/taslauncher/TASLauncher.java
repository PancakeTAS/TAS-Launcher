package de.pfannekuchen.taslauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.pfannekuchen.accountapi.MicrosoftAccount;
import de.pfannekuchen.accountapi.MojangAccount;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Entry Point for the TAS Launcher (JavaFX Application)
 * @author Pancake
 */
public class TASLauncher extends Application {

	public static final File accountsFile = new File(System.getProperty("user.home") + "/accounts");
	public static Object mcaccount;
	public static boolean offlineMode;
	private static Label accountlabel;
	private static Stage stage;
	
	/**
	 * You cannot start JavaFX from the Class itself, so here is a static method to do that
	 */
	public static void start() {
		launch(); // Launch JavaFX in this Thread
	}
	
	/**
	 * Main of Gui
	 */
	@Override @SuppressWarnings("unchecked") public void start(Stage stage) throws Exception {
		/* Load FXML File and display it */
		FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
		stage.setScene(new Scene(loader.load()));
		stage.setResizable(false);
		hijackImageView(stage.getScene().getRoot());
		stage.getIcons().add(new Image(getClass().getResourceAsStream("tas_icon.png")));
		stage.setTitle("Minecraft TAS Launcher");
		stage.show();
		TASLauncher.stage = stage;
		accountlabel = (Label) ((HBox) ((VBox) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getChildren().get(1);
		/* Load the Configuration File */
		init(new File(System.getProperty("user.home") + "/launcher.properties"));
		/* Load Launcher Data */
		try {
			readMainPage();
			readPages();
		} catch (Exception e) {
			offlineMode = true;
			if (offlineMode) System.err.println("Launcher started in Offline Mode");
			e.printStackTrace();
		}
		for (String s : LOTAS_CAT) ((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(0)).getChildren().get(1)).getContent()).getChildren().get(1)).getLeft()).getItems().add(s.split(":")[0]);
		for (String s : PLAYBACK_CAT) ((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).getChildren().get(2)).getContent()).getChildren().get(1)).getLeft()).getItems().add(s.split(":")[0]);
		for (String s : TASBATTLE_CAT) ((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(2)).getChildren().get(2)).getContent()).getChildren().get(1)).getLeft()).getItems().add(s.split(":")[0]);
		for (String s : UNSUP_CAT) ((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(3)).getChildren().get(1)).getContent()).getChildren().get(1)).getLeft()).getItems().add(s.split(":")[0]);
		((TextArea) ((AnchorPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(0)).getChildren().get(1)).getContent()).getChildren().get(2)).getChildren().get(0)).setText(LOTAS);
		((TextArea) ((AnchorPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).getChildren().get(2)).getContent()).getChildren().get(2)).getChildren().get(0)).setText(PLAYBACK);
		((TextArea) ((AnchorPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(2)).getChildren().get(2)).getContent()).getChildren().get(2)).getChildren().get(0)).setText(TASBATTLE);
		((TextArea) ((AnchorPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(3)).getChildren().get(1)).getContent()).getChildren().get(2)).getChildren().get(0)).setText(UNSUPPORTED);
		/* Thread for Loading an Account from the Accounts File */
		Thread accountLoader = new Thread(() -> {
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
							if (mcaccount == null) throw new Exception();
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
						accountsFile.delete();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		accountLoader.setName("Account-Loader Thread");
		accountLoader.setDaemon(true);
		if (!offlineMode) accountLoader.start();
		// Show "TAS Old/Beta" only when "Show Experimental" is enabled
		Platform.runLater(() -> {
			if (!getBoolean("root", "showexperimental")) ((VBox) ((HBox) stage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getChildren().get(0)).getChildren().get(4).setVisible(false);
			((CheckBox) ((VBox) ((HBox) stage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getChildren().get(0)).getChildren().get(5)).setSelected(getBoolean("root", "showexperimental"));
		});
		
		readInstances();
	}
	
	/* ================================== Events for Right Side of Main Menu Pane ================================== */
	
	/**
	 * Automatically called by the Gui once you click on "Start Client"
	 */
	@SuppressWarnings("unchecked")
	private static void startClient(ComboBox<String> box) {
		int selected = box.getSelectionModel().getSelectedIndex();
		if (selected == -1) selected = 0;
		try {
			if (((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(0)).isVisible()) launch(LOTAS_STRING[selected]);
			else if (((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).isVisible()) launch(PLAYBACK_STRING[selected]);
			else if (((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(2)).isVisible()) launch(TASBATTLE_STRING[selected]);
			else if (((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(3)).isVisible()) launch(UNSUP_STRING[selected]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ================================== Events for Left Side of Main Menu Pane ================================== */
	
	/* Following Methods are automatically being called from the System whenever they select a new Category on the left. */
	/* Clicking them will show the correct menu */
	@FXML private HBox tas;
	@FXML private HBox tasplayback;
	@FXML private HBox tasbattle;
	@FXML private HBox tasoldbeta;
	@FXML private CheckBox experimentalCheckbox;
	
	@FXML private void openTASMenu()  { tas.getStyleClass().set(1, "selected"); tasplayback.getStyleClass().set(1, null); tasbattle.getStyleClass().set(1, null); tasoldbeta.getStyleClass().set(1, null); showPane(0); }
	@FXML private void openTASPlaybackMenu()  { tas.getStyleClass().set(1, null); tasplayback.getStyleClass().set(1, "selected"); tasbattle.getStyleClass().set(1, null); tasoldbeta.getStyleClass().set(1, null); showPane(1); }
	@FXML private void openTASBattleMenu()  { tas.getStyleClass().set(1, null); tasplayback.getStyleClass().set(1, null); tasbattle.getStyleClass().set(1, "selected"); tasoldbeta.getStyleClass().set(1, null); showPane(2); }
	@FXML private void openTASOldBetaMenu()  { tas.getStyleClass().set(1, null); tasplayback.getStyleClass().set(1, null); tasbattle.getStyleClass().set(1, null); tasoldbeta.getStyleClass().set(1, "selected"); showPane(3); }
	
	@FXML private void toggleExperimental() {
		setBoolean("root", "showexperimental", experimentalCheckbox.isSelected());
		save();
		((VBox) ((HBox) stage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getChildren().get(0)).getChildren().get(4).setVisible(getBoolean("root", "showexperimental"));
		openTASMenu();
	}
	
	/**
	 * This Method is not automatically being called! 
	 * This Hides all Panes but keeps the indexed one
	 */
	public void showPane(int index) {
		ObservableList<Node> items = ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren();
		for (int i = 0; i < items.size(); i++)  items.get(i).setVisible(i == index);
	}
	
	/**
	 * Automatically called by the Gui once you click on "Login". Opens a menu where you can easily Log Into your Account
	 */
	@FXML private void openLoginDialog() throws IOException {
		loginStage = new Stage();
		loginStage.setScene(new Scene(new FXMLLoader(getClass().getResource("Login.fxml")).load()));
		loginStage.setResizable(false);
		hijackImageView(loginStage.getScene().getRoot());
		loginStage.getIcons().add(new Image(getClass().getResourceAsStream("tas_icon.png")));
		loginStage.setTitle("Minecraft TAS Launcher - Login");
		loginStage.show();
	}
	
	/* ================================== Events for Login Pane ================================== */
	
	private static Stage loginStage;
	@FXML private ImageView microsoftbtn;
	@FXML private ImageView mojangbtn;
	@FXML private ImageView signinbtn;
	@FXML private Label errorlabel;
	@FXML private TextField textField;
	@FXML private PasswordField passwordField;
	
	/**
	 * Automatically called by the Gui once you click on "Mojang Account" in the Login Dialog Pane
	 */
	@FXML
	private void login_mojang() {
		textField.setVisible(true);
		passwordField.setVisible(true);
		microsoftbtn.setVisible(false);
		mojangbtn.setVisible(false);
		errorlabel.setVisible(false);
		signinbtn.setVisible(true);
	}
	
	/**
	 * Automatically called by the Gui once you click on "Sign-in" in the Login Dialog Pane
	 */
	@FXML private void login_signin() {
		Thread login_thread = new Thread(() -> {
			try {
				MojangAccount account = new MojangAccount(textField.getText(), passwordField.getText());
				TASLauncher.mcaccount = account;
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
		});
		login_thread.setDaemon(true);
		login_thread.setName("Login Thread");
		login_thread.start();
	}
	
	/**
	 * Automatically called by the Gui once you click on "Microsoft Login" in the Login Dialog Pane
	 */
	@FXML private void login_microsoft() {
		Thread login_thread = new Thread(() -> {
			try {
				MicrosoftAccount account = new MicrosoftAccount();
				if (!account.ownsMinecraft()) throw new Exception("Account does not own the Game");
				TASLauncher.mcaccount = account;
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
		});
		login_thread.setDaemon(true);
		login_thread.setName("Login Thread");
		login_thread.start();
	}
	
	/* ================================== More Methods ================================== */
	
	/**
	 * Recursivly loop through every single ImageView and add a Mouse Moved Event handler if it is a button
	 */
	@SuppressWarnings("unchecked")
	public static void hijackImageView(Parent pane) {
		((Pane) pane).getChildren().forEach((t) -> {
			if (t instanceof Pane) hijackImageView((Parent) t);
			else if (t instanceof ScrollPane) hijackImageView((Parent) ((ScrollPane) t).getContent());
			else if (t instanceof ImageView) {
				if (((ImageView) t).getImage().getUrl().toLowerCase().contains("button_texture")) {
					t.addEventHandler(MouseEvent.MOUSE_ENTERED, (mouseevent) -> {
						t.setOpacity(0.8);
					});	
					t.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseevent) -> {
						t.setOpacity(0.2);
						new Thread(() -> {
							try {
								startClient((ComboBox<String>) ((BorderPane) t.getParent().getChildrenUnmodifiable().get(1)).getLeft());
								Thread.sleep(500);
							} catch (Exception e) {
								e.printStackTrace();
							}
							Platform.runLater(() -> {
								Platform.exit();
							});
						}).start();
					});	
					t.addEventHandler(MouseEvent.MOUSE_EXITED, (mouseevent) -> {
						t.setOpacity(1);
					});	
				}
			}
		});
	}
	
	/**
__          __  _     _____                _           
\ \        / / | |   |  __ \              | |          
 \ \  /\  / /__| |__ | |__) |___  __ _  __| | ___ _ __ 
  \ \/  \/ / _ \ '_ \|  _  // _ \/ _` |/ _` |/ _ \ '__|
   \  /\  /  __/ |_) | | \ \  __/ (_| | (_| |  __/ |   
    \/  \/ \___|_.__/|_|  \_\___|\__,_|\__,_|\___|_|   
                                                              
	 */
	
	public static final int version = 1;
	public static String[] LOTAS_CAT;
	public static String[] PLAYBACK_CAT;
	public static String[] TASBATTLE_CAT;
	public static String[] UNSUP_CAT;
	
	@SuppressWarnings("rawtypes") public static List[] LOTAS_STRING;
	@SuppressWarnings("rawtypes") public static List[] PLAYBACK_STRING;
	@SuppressWarnings("rawtypes") public static List[] TASBATTLE_STRING;
	@SuppressWarnings("rawtypes") public static List[] UNSUP_STRING;
	
	public static String LOTAS;
	public static String PLAYBACK;
	public static String TASBATTLE;
	public static String UNSUPPORTED;
	
	/**
	 * Loads the File from the Server and Loads all kinds of Data off it.
	 * @throws IOException
	 */
	public static void readMainPage() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/Launcher.tl").openStream()));
		Queue<String> lines = new LinkedList<>();
		while (true) {
			String s = reader.readLine();
			if (s.startsWith(";") && s.length() == 1) break;
			if (!s.trim().startsWith("#")) lines.add(s);
		}
		String _line;
		int _gameCount;
		/* Load LoTAS Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		LOTAS_CAT = new String[_gameCount];
		LOTAS_STRING = new List[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			LOTAS_CAT[i] = lines.poll();
		}
		/* Load TAS Replay Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		PLAYBACK_CAT = new String[_gameCount];
		PLAYBACK_STRING = new List[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			PLAYBACK_CAT[i] = lines.poll();
		}
		/* Load TAS Battle Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		TASBATTLE_CAT = new String[_gameCount];
		TASBATTLE_STRING = new List[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			TASBATTLE_CAT[i] = lines.poll();
		}
		/* Load TAS Unsupported Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		UNSUP_CAT = new String[_gameCount];
		UNSUP_STRING = new List[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			UNSUP_CAT[i] = lines.poll();
		}
		reader.close();
	}
	
	/**
	 * This Part of Code reads all Minecraft Instances into Minecraft Strings, which can then be easily launched
	 */
	public static void readInstances() {
		new Thread(() -> {
			try {
				for (int i = 0; i < LOTAS_CAT.length; i++) LOTAS_STRING[i] = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/" + LOTAS_CAT[i].split(":")[1]).openStream())).lines().toList();
				for (int i = 0; i < PLAYBACK_CAT.length; i++) PLAYBACK_STRING[i] = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/" + PLAYBACK_CAT[i].split(":")[1]).openStream())).lines().toList();
				for (int i = 0; i < TASBATTLE_CAT.length; i++) TASBATTLE_STRING[i] = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/" + TASBATTLE_CAT[i].split(":")[1]).openStream())).lines().toList();
				for (int i = 0; i < UNSUP_CAT.length; i++) UNSUP_STRING[i] = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/" + UNSUP_CAT[i].split(":")[1]).openStream())).lines().toList();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public static void readPages() throws MalformedURLException, IOException {
		LOTAS = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/LoTAS.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		PLAYBACK = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/TAS.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		TASBATTLE = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/TASBattle.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		UNSUPPORTED = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/OLD.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
	}
	
	/*
 _    _ _   _ _     
| |  | | | (_) |    
| |  | | |_ _| |___ 
| |  | | __| | / __|
| |__| | |_| | \__ \
 \____/ \__|_|_|___/
 
	 */
	
	@SuppressWarnings("resource")
	public static void unzipJar(String destDir, String jarPath) throws IOException {
		byte[] buffer = new byte[1024];
		final ZipInputStream zis = new ZipInputStream(new FileInputStream(jarPath));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			File newFile = new File(destDir, zipEntry.getName());
			if (zipEntry.isDirectory()) {
				if (!newFile.isDirectory() && !newFile.mkdirs()) {
					throw new IOException("Failed to create directory " + newFile);
				}
			} else {
				// fix for Windows-created archives
				File parent = newFile.getParentFile();
				if (!parent.isDirectory() && !parent.mkdirs()) {
					throw new IOException("Failed to create directory " + parent);
				}

				// write file content
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
	
	public static Properties props = new Properties();
    private static File configuration;

    public static void setString(String category, String key, String value) {
        props.setProperty(category + ":" + key, value);
    }

    public static void setInt(String category, String key, int value) {
        props.setProperty(category + ":" + key, value + "");
    }

    public static void setBoolean(String category, String key, boolean value) {
        props.setProperty(category + ":" + key, Boolean.toString(value));
    }

    public static boolean getBoolean(String category, String key) {
        return Boolean.valueOf(props.getProperty(category + ":" + key, "false"));
    }

    public static String getString(String category, String key) {
        return props.getProperty(category + ":" + key, "null");
    }

    public static int getInt(String category, String key) {
        return Integer.valueOf(props.getProperty(category + ":" + key, "-1"));
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter(configuration);
            props.store(writer, "TAS Launcher Configuration File");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(File configuration) throws IOException {
        TASLauncher.configuration = configuration;
        if (!configuration.exists()) configuration.createNewFile();
        props = new Properties();
        FileReader reader = new FileReader(configuration);
        props.load(reader);
        reader.close();
    }
    
    /*

 __  __ _                            __ _   _____           _                       
|  \/  (_)                          / _| | |_   _|         | |                      
| \  / |_ _ __   ___  ___ _ __ __ _| |_| |_  | |  _ __  ___| |_ __ _ _ __   ___ ___ 
| |\/| | | '_ \ / _ \/ __| '__/ _` |  _| __| | | | '_ \/ __| __/ _` | '_ \ / __/ _ \
| |  | | | | | |  __/ (__| | | (_| | | | |_ _| |_| | | \__ \ || (_| | | | | (_|  __/
|_|  |_|_|_| |_|\___|\___|_|  \__,_|_|  \__|_____|_| |_|___/\__\__,_|_| |_|\___\___|
                                                                                      
     */
	
    public static void launch(List<String> data) throws Exception {
		String name = data.get(0);
		File javahome = new File(System.getProperty("user.home"), ".launcher/" + data.get(1));
		if (!javahome.exists()) {
			javahome.mkdirs();
			URL downloadJRE;
			if (System.getProperty("os.name").toLowerCase().contains("win")) downloadJRE = new URL("http://mgnet.work/launcher/J" + data.get(1) + "-win.zip");
			else downloadJRE = new URL("http://mgnet.work/launcher/J" + data.get(1) + "-unix.zip");
			Files.copy(downloadJRE.openStream(), new File(System.getProperty("user.home"), ".launcher/" + data.get(1) + ".zip").toPath());
			unzipJar(javahome.getAbsolutePath(), new File(System.getProperty("user.home"), ".launcher/" + data.get(1) + ".zip").getAbsolutePath());
			System.out.println("Done downloading Jar");
		}
		List<URL> libraries = readUrls(data.get(2));
		String main = data.get(3);
		String args = data.get(4);
		List<URL> mods = readUrls(data.get(5));
		File dotMcFolder = new File(System.getProperty("user.home"), ".minecraft_" + name);
		
		String username;
		String uuid;
		String accesstoken;
		if (TASLauncher.mcaccount instanceof MojangAccount) {
			username = ((MojangAccount) TASLauncher.mcaccount).getUsername();
			uuid = ((MojangAccount) TASLauncher.mcaccount).getUuid().toString().replaceAll("-", "");
			accesstoken = ((MojangAccount) TASLauncher.mcaccount).getAccessToken();
		} else {
			username = ((MicrosoftAccount) TASLauncher.mcaccount).getUsername();
			uuid = ((MicrosoftAccount) TASLauncher.mcaccount).getUuid().toString().replaceAll("-", "");
			accesstoken = ((MicrosoftAccount) TASLauncher.mcaccount).getAccessToken();
		}
		
		args = args.replaceAll("%GAMEDIR%", dotMcFolder.getAbsolutePath()
				.replaceAll("\\\\", "\\/"))
				.replaceAll("%ASSETSDIR%", dotMcFolder.getAbsolutePath().replaceAll("\\\\", "\\/") + "/assets")
				.replaceAll("%USERNAME%", username)
				.replaceAll("%UUID%", uuid)
				.replaceAll("%ACCESSTOKEN%", accesstoken);
		String javaw = null;
		if (System.getProperty("os.name").toLowerCase().contains("win")) javaw = new File(javahome, "bin/java.exe").getAbsolutePath();
		else javaw = new File(javahome, "bin/java").getAbsolutePath();
		if (!dotMcFolder.exists()) {
			if (TASLauncher.offlineMode) {
				System.err.println("Cannot install Instance because there is not internet connection");
				return;
			}
			System.out.println("Installing Instance..");
			dotMcFolder.mkdirs();
			// Download Libs
			new File(dotMcFolder, "libs").mkdirs();
			for (int i = 0; i < libraries.size(); i++) {
				// Extract Natives
				if (libraries.get(i).getPath().contains("natives")) {
					File natives = new File(dotMcFolder, "libs/jar_" + i + ".natives");
					Files.copy(libraries.get(i).openStream(), natives.toPath());
					TASLauncher.unzipJar(new File(dotMcFolder, "libs/").getAbsolutePath(), natives.getAbsolutePath());
				} else {
					String[] lib = libraries.get(i).getFile().split("\\/");
					Files.copy(libraries.get(i).openStream(), new File(dotMcFolder, "libs/" + lib[lib.length - 1]).toPath());
				}
			}
			// Download Music Data
			try {
				Files.copy(new URL("http://mgnet.work/launcher/assets.zip").openStream(), new File(dotMcFolder, "assets.zip").toPath(), StandardCopyOption.REPLACE_EXISTING);
				TASLauncher.unzipJar(new File(dotMcFolder, "assets").getAbsolutePath(), new File(dotMcFolder, "assets.zip").getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Done downloading sounds...");
		}
		new File(dotMcFolder, "assets").mkdirs();
		new File(dotMcFolder, "libs").mkdirs();
		new File(dotMcFolder, "mods").mkdirs();
		new File(dotMcFolder, "mods").listFiles((dir, filename) -> new File(dir, filename).delete());
		for (int i = 0; i < mods.size(); i++) {
			String[] mod = mods.get(i).getFile().split("\\/");
			Files.copy(mods.get(i).openStream(), new File(dotMcFolder, "mods/" + mod[mod.length - 1]).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		File outputLog = new File(dotMcFolder, "latest.log");
		outputLog.createNewFile();
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(dotMcFolder);
		builder.redirectOutput(outputLog);
		builder.redirectError(outputLog);
		List<String> items = new ArrayList<String>(Arrays.asList(javaw, "-cp", "libs/*", "-Djava.library.path=libs/", main));
		items.addAll(Arrays.asList(args.split(" ")));
		builder.command(items);
		builder.start();
    }
    
	private static List<URL> readUrls(String string) throws MalformedURLException {
		List<URL> urls = new ArrayList<>();
		for (String url : string.split(" ")) {
			if (url != null) urls.add(new URL(url));
		}
		return urls;
	}
    
}
