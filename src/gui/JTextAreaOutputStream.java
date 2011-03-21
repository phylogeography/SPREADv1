package gui;

import java.io.OutputStream;

import javax.swing.JTextArea;

public class JTextAreaOutputStream extends OutputStream {
	JTextArea textArea;

	public JTextAreaOutputStream(JTextArea t) {

		super();
		textArea = t;
	}

	public void write(int i) {
		char[] chars = new char[1];
		chars[0] = (char) i;
		String s = new String(chars);
		textArea.append(s);
	}

	public void write(char[] buf, int off, int len) {
		String s = new String(buf, off, len);
		textArea.append(s);
	}

}// END: JTextAreaOutputStream
