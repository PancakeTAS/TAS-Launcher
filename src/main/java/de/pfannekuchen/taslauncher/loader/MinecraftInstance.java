package de.pfannekuchen.taslauncher.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import de.pfannekuchen.taslauncher.util.ZipUtils;

/**
 * Simple Minecraft Instance
 * @author Pancake
 */
public class MinecraftInstance {

	protected File dotMcFolder;
	private List<URL> libraries;
	private String javaw;
	private String main;
	private String args;
	
	public MinecraftInstance(String name, File javahome, List<URL> libraries, String main, String args) throws IOException {
		dotMcFolder = new File(System.getProperty("user.home"), ".minecraft_" + name);
		this.main = main;
		this.args = args;
		this.libraries = libraries;
		if (System.getProperty("os.name").toLowerCase().contains("win")) this.javaw = new File(javahome, "bin/javaw.exe").getAbsolutePath();
		else this.javaw = new File(javahome, "bin/javaw").getAbsolutePath();
		if (!dotMcFolder.exists()) installInstance();
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
		builder.command(Arrays.asList(javaw, "-cp", "libs/*",  "-Djava.library.path=libs/", main, args));
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
	}
	
}
