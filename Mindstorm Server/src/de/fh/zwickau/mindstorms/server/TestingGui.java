package de.fh.zwickau.mindstorms.server;

import java.awt.Color;
import java.io.OutputStream;
import java.io.PrintStream;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class TestingGui extends JTextArea {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private PrintStream out = new MyAreaStream(this);
	private JTextField commandLineFeld;
	
	public TestingGui(PrintStream stream) {
		super();
		setEditable(false);
		if (stream == System.err) {
			System.setErr(out);
		} else {
			System.setOut(out);
		}
		frame = new JFrame();
		frame.setSize(1000, 700);
		frame.setVisible(true);
		frame.getContentPane().setLayout(new BorderLayout());
		
		JPanel pane = new JPanel();
		{
			frame.getContentPane().add(pane, BorderLayout.CENTER);
			pane.setLayout(new BorderLayout(0, 0));
						
			JPanel consolePane = new JPanel();
			consolePane.setBorder(new LineBorder(new Color(0, 0, 0)));
			{
				pane.add(consolePane, BorderLayout.EAST);
				consolePane.setLayout(new BorderLayout(0, 0));
			}
		
			setRows(20);
			JScrollPane consoleScrollPane = new JScrollPane(this);
			consolePane.add(consoleScrollPane);
			consoleScrollPane.setViewportBorder(null);
			append("                                                                                                                                ");
		}
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
	
	public static void main(String[] args) {
		new TestingGui(System.out);
	}
}
