package test.app;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.finder.JFileChooserFinder.findFileChooser;

import java.io.File;

import javax.swing.JButton;


import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.image.ScreenshotTaker;
import org.junit.Test;

public class DiscreteTutorialTest extends SPREADBase {

	@Test
	public void testDiscreteTutorial() throws Exception {
		ScreenshotTaker screenshotTaker = new ScreenshotTaker();
		
		JTabbedPaneFixture f = spreadFrame.tabbedPane();
		f.requireVisible();
		String[] titles = f.tabTitles();
		assertArrayEquals(titles,"[Discrete Tree, Discrete Bayes Factors, Continuous Tree, Time Slicer, Terminal]");
		f = f.selectTab("Discrete Tree");
		assertThat(f).isNotNull();

		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
				return button.getLabel().equals("Open");
			}
		}).click();

		JFileChooserFixture fileChooser = findFileChooser().using(robot());
		fileChooser.setCurrentDirectory(new File("src/data/tutorial/phylogeography_discrete"));
		fileChooser.selectFile(new File("H5N1.tree")).approve();
		
		spreadFrame.comboBox("state").selectItem("location");
		
		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
				return button.getLabel().equals("Setup");
			}
		}).click();
		
		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
				return button.getLabel().equals("Load");
			}
		}).click();
		
		fileChooser = findFileChooser().using(robot());
		fileChooser.setCurrentDirectory(new File("src/data/tutorial/phylogeography_discrete"));
		fileChooser.selectFile(new File("H5N1locations.dat")).approve();

		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
				return button.getLabel().equals("Done");
			}
		}).click();

		spreadFrame.panel("Output.spinWidget").click();
		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
				return button.getLabel().equals("Plot");
			}
		}).click();
		

		try {
			JOptionPaneFixture optionPane = new JOptionPaneFixture(robot());
			optionPane.okButton().click(); 
		} catch (Exception e) {
			// ignore
		}
		
		 
		new File("src/data/tutorial/phylogeography_discrete/output.kml").delete();
		spreadFrame.button(new GenericTypeMatcher<JButton>(JButton.class, true) {
			@Override
			protected boolean isMatching(JButton button) {
				return button.getLabel().equals("Generate");
			}
		}).click();
		
		Thread.sleep(300);

		if (!(new File("src/data/tutorial/phylogeography_discrete/output.kml").exists())) {
			throw new Exception("Expected file output.kml to be generated");
		}

		new File("SPREADdiscrete.png").delete();
		screenshotTaker.saveComponentAsPng(spreadFrame.target, "SPREADdiscrete.png");
	} // testDiscreteTutorial

}
