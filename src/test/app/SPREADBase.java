package test.app;



import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.edt.GuiActionRunner.execute;
import static org.fest.swing.finder.JFileChooserFinder.findFileChooser;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.io.File;

import javax.swing.JFrame;

import org.fest.swing.annotation.RunsInEDT;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;

import app.SpreadApp;

/**
 * Basic gui test methods for SPREAD  
 * 
 */
public class SPREADBase extends FestSwingJUnitTestCase {

	protected FrameFixture spreadFrame;
	protected SpreadApp spreadApp;

	protected void onSetUp() {
		spreadFrame = new FrameFixture(robot(), createNewEditor());
		spreadFrame.show();
		spreadFrame.resizeTo(new Dimension(1224, 786));
		spreadApp = SpreadApp.gui;
	}

	@RunsInEDT
	private static JFrame createNewEditor() {
		return execute(new GuiQuery<JFrame>() {
			protected JFrame executeInEDT() throws Throwable {
				SpreadApp gui = new SpreadApp();
				JFrame frame = gui.launchFrame();
				return frame;
			}
		});
	}


	void warning(String str) {
		System.err.println("\n\n=====================================================\n");
		System.err.println(str);
		System.err.println("\n=====================================================\n\n");
	}
	
	// for handling file open events on Mac
	FileDialog fileDlg = null;
	String _dir;
	File _file;

	boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}
	
	void importAlignment(String dir, File ... files) {
		if (!isMac()) {
			spreadFrame.menuItemWithPath("File", "Import Alignment").click();
			JFileChooserFixture fileChooser = findFileChooser().using(robot());
			fileChooser.setCurrentDirectory(new File(dir));
			fileChooser.selectFiles(files).approve();
		} else {
			this._dir = dir;
			for (File file : files) {
				_file = new File(dir + "/" + file.getName());
				execute(new GuiTask() {
			        protected void executeInEDT() {
			        	try {
//			        		spreadApp.doc.importNexus(_file);
			        	} catch (Exception e) {
							e.printStackTrace();
						}
			        }
			    });
			}
		}			
	}

	
	void assertArrayEquals(Object [] o, String array) {
		String str = array.substring(1, array.length() - 1);
		String [] strs = str.split(", ");
		for (int i = 0; i < o.length && i < strs.length; i++) {
			assertThat(strs[i]).as("expected array value " + strs[i] + " instead of " + o[i].toString()).isEqualTo(o[i].toString());
		}
		assertThat(o.length).as("arrays do not match: different lengths").isEqualTo(strs.length);
	}
	
}