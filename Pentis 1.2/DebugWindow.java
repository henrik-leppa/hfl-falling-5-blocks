import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DebugWindow extends JFrame{
	private JTextArea area;
	private ByteArrayOutputStream stream;
	private int lastSize;

	public DebugWindow(String title,Image icon,int closeOperation,boolean visible){
		stream = new ByteArrayOutputStream();

		area = new JTextArea(15, 80);
		area.setFont(new Font("Monospaced",Font.BOLD,14));
		area.setLineWrap(true);
		area.setEditable(false);
		area.setForeground(Color.GREEN);
		area.setBackground(Color.BLACK);
		area.setCaretColor(Color.GREEN);
		area.setSelectedTextColor(Color.BLACK);
		area.setSelectionColor(Color.GREEN);
		area.setDisabledTextColor(Color.RED);
		JScrollPane scrollPane = new JScrollPane(	area,
													JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);
		pack();

		setVisible(visible);
		setDefaultCloseOperation(closeOperation);
		setIconImage(icon);
		setTitle(title);
	}

	public void addComment(String comment){
		area.append(comment);
		area.setCaretPosition(area.getText().length());
	}

	public void updateText(String text){
		if(area.getText() != text){
			area.setText(text);
			area.setCaretPosition(area.getText().length());
		}
	}

	public void updateText(){
		String text = stream.toString();
		if(lastSize != stream.size()){
			area.setText(text);
			area.setCaretPosition(area.getText().length());
		}
		lastSize = stream.size();
	}

	public PrintStream getPrintStream(){
		return new PrintStream(stream);
	}

	public static void main(String[] args) {
		DebugWindow debug = new DebugWindow("Debug",null,JFrame.EXIT_ON_CLOSE,true);
		System.setOut(debug.getPrintStream());

		System.out.println(		"DebugWindow.class version 1.0,\n"
							+	"created by Henrik Leppä");
		debug.updateText();
	}
}