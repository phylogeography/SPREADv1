package gui;

import java.awt.BorderLayout;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("serial")
public class TerminalTab extends JPanel {

	private JTextArea textArea;

	public TerminalTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		// Setup text area
		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(textArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);

		// Redirect streams
		System.setOut(new PrintStream(new JTextAreaOutputStream(textArea)));
		System.setErr(new PrintStream(new JTextAreaOutputStream(textArea)));

//		//TODO: check how to append and use JEditorPane for text, hyperlinks handling etc
//		JEditorPane textAreaTest = new JEditorPane();
//		textAreaTest.setContentType("text/html");
//		textAreaTest.setText("kutas");
		
	}

	public void setText(String text) {
		textArea.append(text);
	}

	public void clearTerminal() {
		textArea.setText("");
	}

}// END: class
