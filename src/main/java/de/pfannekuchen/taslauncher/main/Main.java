package de.pfannekuchen.taslauncher.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import de.pfannekuchen.taslauncher.TASLauncher;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		// Move Streams
		System.setOut(new PrintStream(new File(System.getProperty("user.home"), "launcher_out.log")));
		System.setErr(new PrintStream(new File(System.getProperty("user.home"), "launcher_err.log")));
		TASLauncher.start(); // Start JavaFX
		System.out.close();
		System.err.close();
	}

}
