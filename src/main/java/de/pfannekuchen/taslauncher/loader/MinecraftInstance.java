package de.pfannekuchen.taslauncher.loader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.pfannekuchen.taslauncher.TASLauncher;
import de.pfannekuchen.taslauncher.util.ZipUtils;

/**
 * Simple Minecraft Instance
 * @author Pancake
 */
public class MinecraftInstance {

	/**
	 * Serializable Minecraft Instance
	 * @author Pancake
	 */
	public static class MinecraftString {
		
		public MinecraftString(String name, String jre, List<URL> libs, String mainclass, String args, List<URL> mods) throws IOException {
			this.name = name;
			// get jre
			this.javahome = new File(System.getProperty("user.home"), ".launcher/" + jre);
			if (!this.javahome.exists()) {
				this.javahome.mkdirs();
				URL downloadJRE;
				if (System.getProperty("os.name").toLowerCase().contains("win")) downloadJRE = new URL("http://mgnet.work/launcher/J" + jre + "-win.zip");
				else downloadJRE = new URL("http://mgnet.work/launcher/J" + jre + "-unix.zip");
				Files.copy(downloadJRE.openStream(), new File(System.getProperty("user.home"), ".launcher/" + jre + ".zip").toPath());
				ZipUtils.unzipJar(javahome.getAbsolutePath(), new File(System.getProperty("user.home"), ".launcher/" + jre + ".zip").getAbsolutePath());
				System.out.println("Done downloading Jar");
			}
			this.libs = libs;
			this.mainclass = mainclass;
			this.args = args;
			this.mods = mods;
		}
		
		public String name;
		public File javahome;
		public List<URL> libs;
		public String mainclass;
		public String args;
		public List<URL> mods;
		
		public MinecraftInstance getInstance() throws IOException {
			return new MinecraftInstance(name, javahome, libs, mainclass, args, mods);
		}
		
		public static MinecraftString fromFile(List<String> lines) throws IOException {
			return new MinecraftString(lines.get(0), lines.get(1), readUrls(lines.get(2)), lines.get(3), lines.get(4), readUrls(lines.get(5)));
		}
		
		private static List<URL> readUrls(String string) throws MalformedURLException {
			List<URL> urls = new ArrayList<>();
			for (String url : string.split(" ")) {
				if (url != null) urls.add(new URL(url));
			}
			return urls;
		}
	}
	
	protected File dotMcFolder;
	private List<URL> libraries;
	private String javaw;
	private String main;
	private String args;
	
	/**
	 * Constructor that automatically installs an instance
	 * @param name Name of the folder to install to
	 * @param javahome Javahome to run with
	 * @param libraries Libraries to launch the game with
	 * @param main Main class of the game
	 * @param args Arguments for the game (%MCDATADIR%)
	 * @param mods List of mods to install
	 * @throws IOException Throws whenever an IO process couldn't be completed
	 */
	public MinecraftInstance(String name, File javahome, List<URL> libraries, String main, String args, List<URL> mods) throws IOException {
		dotMcFolder = new File(System.getProperty("user.home"), ".minecraft_" + name);
		this.main = main;
		this.args = args.replaceAll("%MCDATADIR%", dotMcFolder.getAbsolutePath().replaceAll("\\\\", "\\/"));
		this.libraries = libraries;
		if (System.getProperty("os.name").toLowerCase().contains("win")) this.javaw = new File(javahome, "bin/java.exe").getAbsolutePath();
		else this.javaw = new File(javahome, "bin/java").getAbsolutePath();
		if (!dotMcFolder.exists()) {
			if (TASLauncher.offlineMode) {
				System.err.println("Cannot install Instance because there is not internet connection");
				return;
			}
			System.out.println("Installing Instance..");
			installInstance();
		}
		new File(dotMcFolder, "assets").mkdirs();
		new File(dotMcFolder, "libs").mkdirs();
		new File(dotMcFolder, "mods").mkdirs();
		new File(dotMcFolder, "mods").listFiles((dir, filename) -> new File(dir, filename).delete());
		for (int i = 0; i < mods.size(); i++) {
			String[] mod = mods.get(i).getFile().split("\\/");
			Files.copy(mods.get(i).openStream(), new File(dotMcFolder, "mods/" + mod[mod.length - 1]).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	/**
	 * Launched the Game
	 * @throws IOException Throws whenever the game cannot create a log file
	 */
	public void launch() throws IOException {
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

	/**
	 * Installs the Minecraft Instance.
	 * @throws IOException Throws whenever files couldn't be created
	 */
	public void installInstance() throws IOException {
		dotMcFolder.mkdirs();
		// Download Libs
		new File(dotMcFolder, "libs").mkdirs();
		for (int i = 0; i < libraries.size(); i++) {
			// Extract Natives
			if (libraries.get(i).getPath().contains("natives")) {
				File natives = new File(dotMcFolder, "libs/jar_" + i + ".natives");
				Files.copy(libraries.get(i).openStream(), natives.toPath());
				ZipUtils.unzipJar(new File(dotMcFolder, "libs/").getAbsolutePath(), natives.getAbsolutePath());
			} else {
				String[] lib = libraries.get(i).getFile().split("\\/");
				Files.copy(libraries.get(i).openStream(), new File(dotMcFolder, "libs/" + lib[lib.length - 1]).toPath());
			}
		}
		// Download Music Data
		try {
			Files.copy(new URL("http://mgnet.work/launcher/assets.zip").openStream(), new File(dotMcFolder, "assets.zip").toPath(), StandardCopyOption.REPLACE_EXISTING);
			ZipUtils.unzipJar(new File(dotMcFolder, "assets").getAbsolutePath(), new File(dotMcFolder, "assets.zip").getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done downloading sounds...");
	}
	
}
