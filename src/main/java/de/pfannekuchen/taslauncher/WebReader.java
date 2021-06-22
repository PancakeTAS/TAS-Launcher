package de.pfannekuchen.taslauncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class WebReader {

	public static final int version = 1;
	public static String[] LOTAS_CAT;
	public static String[] PLAYBACK_CAT;
	public static String[] TASBATTLE_CAT;
	public static String[] UNSUP_CAT;
	
	public static Pair<?, ?>[] LOTAS_PATCH;
	public static Pair<?, ?>[] PLAYBACK_PATCH;
	public static Pair<?, ?>[] TASBATTLE_PATCH;
	public static Pair<?, ?>[] UNSUP_PATCH;
	
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
		for (int i = 0; i < _gameCount; i++) {
			LOTAS_CAT[i] = lines.poll();
		}
		/* Load TAS Replay Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		PLAYBACK_CAT = new String[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			PLAYBACK_CAT[i] = lines.poll();
		}
		/* Load TAS Battle Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		TASBATTLE_CAT = new String[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			TASBATTLE_CAT[i] = lines.poll();
		}
		/* Load TAS Unsupported Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		UNSUP_CAT = new String[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			UNSUP_CAT[i] = lines.poll();
		}
		reader.close();
	}
	
	public static void readPages() throws MalformedURLException, IOException {
		LOTAS = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/LoTAS.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		PLAYBACK = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/TAS.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		TASBATTLE = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/TASBattle.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		UNSUPPORTED = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/launcher/OLD.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
	}
	
}
