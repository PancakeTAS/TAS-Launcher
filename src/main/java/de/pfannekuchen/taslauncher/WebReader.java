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

public class WebReader {

	public static final int version = 1;
	public static String[] LOTAS_CAT;
	public static String[] PLAYBACK_CAT;
	public static String[] TASBATTLE_CAT;
	public static String[] UNSUP_CAT;
	
	public static String LOTAS;
	public static String PLAYBACK;
	public static String TASBATTLE;
	public static String UNSUPPORTED;
	
	/**
	 * Loads the File from the Server and Loads all kinds of Data off it.
	 * @throws IOException
	 */
	public static void readMainPage() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/tasbattlelauncher/main.dat").openStream()));
		Queue<String> lines = new LinkedList<>();
		while (true) {
			String s = reader.readLine();
			if (s.startsWith(";") && s.length() == 1) break;
			if (!s.trim().startsWith("#")) lines.add(s);
		}
		String _line;
		int gameCount;
		/* Load Update Data */
		_line = lines.poll();
		if (version < Integer.parseInt(_line.split(":")[0])) update(new URL("http://mgnet.work/tasbattlelauncher/" + _line.split(":")[1]));
		/* Load LoTAS Games */
		_line = lines.poll();
		gameCount = Integer.parseInt(_line);
		LOTAS_CAT = new String[gameCount];
		for (int i = 0; i < gameCount; i++) {
			LOTAS_CAT[i] = lines.poll();
		}
		/* Load TAS Replay Games */
		_line = lines.poll();
		gameCount = Integer.parseInt(_line);
		PLAYBACK_CAT = new String[gameCount];
		for (int i = 0; i < gameCount; i++) {
			PLAYBACK_CAT[i] = lines.poll();
		}
		/* Load TAS Battle Games */
		_line = lines.poll();
		gameCount = Integer.parseInt(_line);
		TASBATTLE_CAT = new String[gameCount];
		for (int i = 0; i < gameCount; i++) {
			TASBATTLE_CAT[i] = lines.poll();
		}
		/* Load TAS Unsupported Games */
		_line = lines.poll();
		gameCount = Integer.parseInt(_line);
		UNSUP_CAT = new String[gameCount];
		for (int i = 0; i < gameCount; i++) {
			UNSUP_CAT[i] = lines.poll();
		}
	}

	/**
	 * This Method updates the Client whenever the Version is lower than the required one.
	 * TODO: Implement Updater
	 */
	private static void update(URL url) {
		
	}

	public static void readPages() throws MalformedURLException, IOException {
		InputStream stream = new URL("http://mgnet.work/tasbattlelauncher/lotas.dat").openStream();
		LOTAS = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		stream = new URL("http://mgnet.work/tasbattlelauncher/playback.dat").openStream();
		PLAYBACK = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		stream = new URL("http://mgnet.work/tasbattlelauncher/tasbattle.dat").openStream();
		TASBATTLE = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		stream = new URL("http://mgnet.work/tasbattlelauncher/experimental.dat").openStream();
		UNSUPPORTED = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
	}
	
}
