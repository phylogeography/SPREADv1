package test.templates;

import java.net.URL;

import app.SpreadApp;

public class TestUtils {

	public static String getResourcePath(String resource) throws Exception {
		URL url = SpreadApp.class.getResource(resource);
		if (url == null) {
			url = SpreadApp.class.getResource("/src/" + resource);
		}
		if (url == null) {
			throw new Exception ("Resource  " + resource + " not found");
		}
		String path = url.getPath();
		return path;
	}
	
}
