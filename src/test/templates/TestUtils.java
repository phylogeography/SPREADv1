package test.templates;

import java.net.URL;

import junit.framework.TestCase;

import app.SpreadApp;

public class TestUtils extends TestCase {

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
	

	public static void main(String args[]) {
	    org.junit.runner.JUnitCore.main(args[0]);
    }
}
