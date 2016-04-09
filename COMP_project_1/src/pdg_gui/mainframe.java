package pdg_gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import java.awt.Dimension;
import java.awt.Component;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.github.javaparser.ParseException;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

import pdg.Tester;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import java.awt.Font;

public class mainframe extends JFrame {
	private JPanel contentPane;
	private static Tester tester;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				tester = new Tester();
				try {
					new mainframe();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public mainframe() {
		final JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(1300, 800));
		frame.setResizable(false);
		frame.setTitle("Java PDG Generator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//FRAME CONTENT PANE
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//CONTENT PANELS
		JPanel codepanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) codepanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);
		codepanel.setBounds(12, 18, 321, 742);
		codepanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		contentPane.add(codepanel);
		
		JPanel graphpanel = new JPanel();
		graphpanel.setBounds(343, 48, 941, 712);
		graphpanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(graphpanel);
		
		
		//TEXT AREAS
		JTextArea txtrGraphGoesHere = new JTextArea();
		graphpanel.add(txtrGraphGoesHere);
		txtrGraphGoesHere.setText("Graph goes here");
		
		
		JTextArea txtrCodeGoesHere = new JTextArea();
		txtrCodeGoesHere.setTabSize(2);
		txtrCodeGoesHere.setFont(new Font("Monospaced", Font.PLAIN, 11));
		txtrCodeGoesHere.setEditable(false);
		txtrCodeGoesHere.setBorder(codepanel.getBorder());
		JScrollPane scroll = new JScrollPane(txtrCodeGoesHere, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(307, 725));
		codepanel.add(scroll);
		txtrCodeGoesHere.setText("Code goes here");
		
		//SELECT VARIABLE TO TRACK STUFF
		JLabel lblVariable = new JLabel("Variable:");
		lblVariable.setBounds(343, 18, 56, 16);
		contentPane.add(lblVariable);
		
		JComboBox selvar = new JComboBox();
		selvar.setBounds(391, 18, 113, 22);
		contentPane.add(selvar);
		
		//BUTTONS
		JButton callGraph = new JButton("Call Graph");
		callGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		callGraph.setBounds(1021, 14, 119, 25);
		contentPane.add(callGraph);

		JButton chfile = new JButton("Choose File");
		chfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
			        File selectedFile = fileChooser.getSelectedFile();
			        try {
						Tester.addFile(new FileInputStream(selectedFile));
					} catch (ParseException | IOException e1) {	e1.printStackTrace();}
			        
			        try {
						FileInputStream toCode = new FileInputStream(selectedFile);
						String content = new Scanner(selectedFile).useDelimiter("\\Z").next();
						txtrCodeGoesHere.setText(content);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		      	}
			}
		});
		chfile.setBounds(1150, 12, 134, 25);
		contentPane.add(chfile);
		
		
		//FINALIZE THE FRAME
		frame.pack();
		frame.setVisible(true);
	}
}
