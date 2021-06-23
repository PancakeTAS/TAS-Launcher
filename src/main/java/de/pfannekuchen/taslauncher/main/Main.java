package de.pfannekuchen.taslauncher.main;

import java.net.URL;

public class Main extends ClassLoader {
	
	public static void main(String[] args) throws Exception {
		byte[] bytes = new URL("http://mgnet.work/launcher/TASLauncher.class").openStream().readAllBytes();
		new Main().defineClass("de.pfannekuchen.taslauncher.TASLauncher", bytes, 0, bytes.length).getMethod("start").invoke(null);
	}

}
