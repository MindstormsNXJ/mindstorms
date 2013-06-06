package de.fh.zwickau.mindstorms.server;

import java.awt.Color;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class SystemOutConsole extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PrintStream out = new MyAreaStream(this);

	public SystemOutConsole(PrintStream stream) {
		super();
		setEditable(false);
		if (stream == System.err) {
			System.setErr(out);
		} else {
			System.setOut(out);
		}
		setBackground(Color.GRAY);
		setForeground(Color.WHITE);
	}

	class MyAreaStream extends PrintStream {
		MyAreaStream(JTextArea area) {
			super(new MyAreaOutStream(area));
		}
	}

	class MyAreaOutStream extends OutputStream {
		private JTextArea area = null;

		MyAreaOutStream(JTextArea out) {
			this.area = out;
		}

		@Override
		public void write(final int b) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					area.append(String.valueOf((char) b));
					area.setCaretPosition(area.getText().length());
				}
			});
		}
	}
}