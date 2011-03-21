package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
		textArea = new JTextArea(4, 20);
		textArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(textArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(200, 70));
		add(scrollPane, BorderLayout.CENTER);

		// Redirect streams
		System.setOut(new PrintStream(new JTextAreaOutputStream(textArea)));
		System.setErr(new PrintStream(new JTextAreaOutputStream(textArea)));

	}

	public void setText(String text) {
		textArea.append(text);
	}

	public void clearTerminal() {
		textArea.setText("");
	}
}
