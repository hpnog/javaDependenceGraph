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
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class mainframe extends JFrame {
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
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
		frame.setTitle("Java PDG Generator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//FRAME CONTENT PANE
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//CONTENT PANELS
		JPanel codepanel = new JPanel();
		codepanel.setBounds(12, 32, 248, 593);
		codepanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(codepanel);
		
		JPanel graphpanel = new JPanel();
		graphpanel.setBounds(272, 87, 818, 577);
		graphpanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(graphpanel);
		
		//TEXT AREAS
		JTextArea txtrGraphGoesHere = new JTextArea();
		graphpanel.add(txtrGraphGoesHere);
		txtrGraphGoesHere.setText("Graph goes here");
		
		
		JTextArea txtrCodeGoesHere = new JTextArea();
		codepanel.add(txtrCodeGoesHere);
		txtrCodeGoesHere.setText("Code goes here");
		
		//SELECT VARIABLE TO TRACK STUFF
		JLabel lblVariable = new JLabel("Variable:");
		lblVariable.setBounds(614, 18, 56, 16);
		contentPane.add(lblVariable);
		
		JComboBox selvar = new JComboBox();
		selvar.setBounds(670, 15, 113, 22);
		contentPane.add(selvar);
		
		//BUTTONS
		JButton callGraph = new JButton("Call Graph");
		callGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		callGraph.setBounds(971, 12, 119, 25);
		contentPane.add(callGraph);

		JButton chfile = new JButton("Choose File");
		chfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		chfile.setBounds(1102, 12, 134, 25);
		contentPane.add(chfile);
		
		
		//FINALIZE THE FRAME
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
}
