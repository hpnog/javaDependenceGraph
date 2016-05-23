package pdg_gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.github.javaparser.ParseException;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import pdg.ASTPrinter;

public class mainframe extends JFrame {
	
	private mxGraph graph = new mxGraph();
	private mxGraphComponent graphComponent;
	
	private File selectedFile;
	
	private JPanel contentPane;
	private static ASTPrinter astprinter;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				astprinter = new ASTPrinter();
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
		
		startGraph(graphpanel);
		
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
				graph.getModel().beginUpdate();
				
				try {
					astprinter.addFile(new FileInputStream(selectedFile), graph);			// � PRECISO PASSAR AQUI O GRAFO PARA O PREENCHER PROVAVELMENTE
				} catch (ParseException | IOException e1) {	e1.printStackTrace();}
		        
				// CALL THE GRAPH
				graph.insertVertex(graph.getDefaultParent(), null, "TESTE2", 30, 80, 100, 50);
				
				// � PRECISO AQUI PREENCHER O GRAFO OU ESCOLHER COMO O MOSTRAR
				
				graph.getModel().endUpdate();
				graphpanel.repaint();
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
			        selectedFile = fileChooser.getSelectedFile();
			        
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

	private void startGraph(JPanel graphpanel) {
		graphComponent = new mxGraphComponent(graph);
		
		graphComponent.setPreferredSize(new Dimension(930, 700));
		graphpanel.removeAll();
					
		graphpanel.add(graphComponent);

		graph.getModel().beginUpdate();
		
		Object parent = graph.getDefaultParent();
		
		graph.insertVertex(parent, null, "TESTE", 20, 50, 50, 20);
		
		graph.getModel().endUpdate();
		
		graphpanel.repaint();
	}
}
