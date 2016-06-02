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
import java.util.Map;
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

import org.jgrapht.DirectedGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import org.jgraph.*;

import com.github.javaparser.ParseException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;

import pdg.PDGCore;
import org.jgraph.graph.GraphModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class mainframe extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File selectedFile;
	
	DirectedGraph<String, DefaultEdge> hrefGraph;
    
	private PDGCore astprinter = new PDGCore();
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new PDGCore();
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
		frame.setMinimumSize(new Dimension(1000, 400));
		frame.setPreferredSize(new Dimension(1300, 800));
		frame.setTitle("Java PDG Generator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//FRAME CONTENT PANE
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		//CONTENT PANELS
		JPanel codepanel = new JPanel();
		codepanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		contentPane.add(codepanel, BorderLayout.WEST);
		
		createGraph();
		resetGraph();
		codepanel.setLayout(new BorderLayout(0, 0));
        
		JTextArea txtrCodeGoesHere = new JTextArea();
		txtrCodeGoesHere.setTabSize(2);
		txtrCodeGoesHere.setFont(new Font("Monospaced", Font.PLAIN, 11));
		txtrCodeGoesHere.setEditable(false);
		txtrCodeGoesHere.setBorder(codepanel.getBorder());
		JScrollPane scroll = new JScrollPane(txtrCodeGoesHere, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(400, 725));
		codepanel.add(scroll);
		txtrCodeGoesHere.setText("Code goes here");		
		
		JPanel graphPane = new JPanel();
		contentPane.add(graphPane, BorderLayout.CENTER);
		graphPane.setLayout(new BorderLayout(0, 0));
		
		JPanel optionsPane = new JPanel();
		graphPane.add(optionsPane, BorderLayout.NORTH);
		optionsPane.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonsPane = new JPanel();
		optionsPane.add(buttonsPane, BorderLayout.EAST);
		
		JButton button = new JButton("Call Graph");
		buttonsPane.add(button);
		
		JButton button_1 = new JButton("Choose File");
		buttonsPane.add(button_1);
		
		JPanel varPane = new JPanel();
		optionsPane.add(varPane, BorderLayout.WEST);
		
		JLabel label = new JLabel("Variable:");
		varPane.add(label);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		
		JComboBox<Object> comboBox = new JComboBox<Object>();
		varPane.add(comboBox);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		graphPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JGraph graph = new JGraph((GraphModel) null);
		graph.setGridVisible(true);
		graph.setGridEnabled(true);
		graph.setPreferredSize(new Dimension(931, 679));
		panel.add(graph);
		
		//FINALIZE THE FRAME
		frame.pack();
		frame.setVisible(true);
	}

	private void resetGraph() {
	}

	private void createGraph() {
		hrefGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		String program = "Program";
		hrefGraph.addVertex(program);
	}
	
	private JGraph getJgraph() {
	    ListenableGraph<String, DefaultEdge> g = new ListenableDirectedGraph<String, DefaultEdge>(hrefGraph);		
	    JGraph jgraph = new JGraph(new JGraphModelAdapter<String, DefaultEdge>(g));
	    
		// Let's see if we can lay it out
	    JGraphFacade jgf = new JGraphFacade(jgraph);
	    JGraphFastOrganicLayout layoutifier = new JGraphFastOrganicLayout();
	    layoutifier.run(jgf);
	    System.out.println("Layout complete");

	    final Map<?, ?> nestedMap = jgf.createNestedMap(true, true);
	    
	    return jgraph;
	}
}
