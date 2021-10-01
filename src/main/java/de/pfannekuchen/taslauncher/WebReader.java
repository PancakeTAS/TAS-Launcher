package de.pfannekuchen.taslauncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import de.pfannekuchen.taslauncher.loader.MinecraftInstance.MinecraftString;

public class WebReader {

	public static final int version = 1;
	public static String[] LOTAS_CAT;
	public static String[] PLAYBACK_CAT;
	public static String[] TASBATTLE_CAT;
	public static String[] UNSUP_CAT;
	
	public static MinecraftString[] LOTAS_STRING;
	public static MinecraftString[] PLAYBACK_STRING;
	public static MinecraftString[] TASBATTLE_STRING;
	public static MinecraftString[] UNSUP_STRING;
	
	public static String LOTAS;
	public static String PLAYBACK;
	public static String TASBATTLE;
	public static String UNSUPPORTED;
	
	/**
	 * Loads the File from the Server and Loads all kinds of Data off it.
	 * @throws IOException
	 */
	public static void readMainPage() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/Launcher.tl").openStream()));
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
		LOTAS_STRING = new MinecraftString[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			LOTAS_CAT[i] = lines.poll();
		}
		/* Load TAS Replay Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		PLAYBACK_CAT = new String[_gameCount];
		PLAYBACK_STRING = new MinecraftString[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			PLAYBACK_CAT[i] = lines.poll();
		}
		/* Load TAS Battle Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		TASBATTLE_CAT = new String[_gameCount];
		TASBATTLE_STRING = new MinecraftString[_gameCount];
		for (int i = 0; i < _gameCount; i++) {
			TASBATTLE_CAT[i] = lines.poll();
		}
		/* Load TAS Unsupported Games */
		_line = lines.poll();
		_gameCount = Integer.parseInt(_line);
		UNSUP_CAT = new String[_gameCount];
		UNSUP_STRING = new MinecraftString[_gameCount];
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
				for (int i = 0; i < LOTAS_CAT.length; i++) LOTAS_STRING[i] = MinecraftString.fromFile(new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/" + LOTAS_CAT[i].split(":")[1]).openStream())).lines().toList());
				for (int i = 0; i < PLAYBACK_CAT.length; i++) PLAYBACK_STRING[i] = MinecraftString.fromFile(new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/" + PLAYBACK_CAT[i].split(":")[1]).openStream())).lines().toList());
				for (int i = 0; i < TASBATTLE_CAT.length; i++) TASBATTLE_STRING[i] = MinecraftString.fromFile(new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/" + TASBATTLE_CAT[i].split(":")[1]).openStream())).lines().toList());
				for (int i = 0; i < UNSUP_CAT.length; i++) UNSUP_STRING[i] = MinecraftString.fromFile(new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/" + UNSUP_CAT[i].split(":")[1]).openStream())).lines().toList());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public static void readPages() throws MalformedURLException, IOException {
		LOTAS = new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/LoTAS.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		PLAYBACK = new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/TAS.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		TASBATTLE = new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/TASBattle.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		UNSUPPORTED = new BufferedReader(new InputStreamReader(new URL("https://data.mgnet.work/taslauncher/OLD.tl").openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
	}
	
}
