package test.app;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.finder.JFileChooserFinder.findFileChooser;

import java.io.File;

import javax.swing.JButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.image.ScreenshotTaker;
import org.junit.Test;

public class ContinuousTutorialTest extends SPREADBase {
	
	@Test
	public void testContinuousTutorial() throws Exception {
		ScreenshotTaker screenshotTaker = new ScreenshotTaker();

		JTabbedPaneFixture f = spreadFrame.tabbedPane();
		f.requireVisible();
		String[] titles = f.tabTitles();
		assertArrayEquals(titles,"[Discrete Tree, Discrete Bayes Factors, Continuous Tree, Time Slicer, Terminal]");
		f = f.selectTab("Continuous Tree");
		assertThat(f).isNotNull();

		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
//				return button.getLabel().equals("Open");
				return button.getText().equals("Open");
			}
		}).click();
		
		JFileChooserFixture fileChooser = findFileChooser().using(robot());
		fileChooser.setCurrentDirectory(new File("src/data/tutorial/phylogeography_continuous"));
		fileChooser.selectFile(new File("RacRABV.tree")).approve();
		
		spreadFrame.comboBox("latitudeComboBox").selectItem("location.location1");
		spreadFrame.comboBox("longitudeComboBox").selectItem("location.location2");
		
		spreadFrame.panel("Output.spinWidget").click();
		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
//				return button.getLabel().equals("Plot");
				return button.getText().equals("Plot");
			}
		}).click();

		new File("SPREADcontinuous.png").delete();
		screenshotTaker.saveComponentAsPng(spreadFrame.target, "SPREADcontinuous.png");
	}

}
