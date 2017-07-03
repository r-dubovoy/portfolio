package main;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Interface {

	private JFrame frame;
	private JTextField txtDnsIp;
	private JTextField txtGooglecom;
	private JTextField txtA;
	private static JTextArea textArea;
	static JTextArea txtrShit = new JTextArea();

	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 589);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		txtA = new JTextField();
		txtA.setBounds(6, 6, 99, 20);
		txtA.setText("A");
		frame.getContentPane().add(txtA);
		txtA.setColumns(10);

		txtGooglecom = new JTextField();
		txtGooglecom.setBounds(160, 6, 99, 20);
		txtGooglecom.setText("google.com");
		frame.getContentPane().add(txtGooglecom);
		txtGooglecom.setColumns(10);

		txtDnsIp = new JTextField();
		txtDnsIp.setBounds(325, 6, 99, 20);
		txtDnsIp.setText("DNS ip");
		frame.getContentPane().add(txtDnsIp);
		txtDnsIp.setColumns(15);
		
	
		txtrShit.setText("");
		txtrShit.setBounds(20, 71, 388, 468);
		frame.getContentPane().add(txtrShit);

		
		JButton sendButton = new JButton("send request ");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					sendRequest(arg0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		sendButton.setBackground(Color.LIGHT_GRAY);
		sendButton.setBounds(139, 37, 140, 23);
		frame.getContentPane().add(sendButton);

	}

	protected void sendRequest(ActionEvent arg0) throws IOException {
		String[] args = {"", "", ""};
		args[0] = txtGooglecom.getText();
		args[1] = txtDnsIp.getText();
		args[2] = txtA.getText();
		Client.main(args);
	}
	
	public static void setOutPut(String txt){
		
		txtrShit.append(txt + "\n");
	}

	/*
	 * @Override public void write(String s) { textArea.append(s +
	 * System.lineSeparator()); }
	 */
}
