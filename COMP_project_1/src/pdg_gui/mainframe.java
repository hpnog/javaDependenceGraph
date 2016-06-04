package pdg_gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.ListenableDirectedGraph;

import org.jgraph.*;

import com.github.javaparser.ParseException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import graphStructures.GraphNode;
import graphStructures.RelationshipEdge;
import pdg.PDGCore;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;

public class mainframe extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File selectedFile;
	DirectedGraph<GraphNode, RelationshipEdge> hrefGraph;
	private PDGCore astprinter = new PDGCore();
	
	private JPanel contentPane;
	private JPanel panel;
	private JScrollPane graphScroll;
	

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
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
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
		scroll.setPreferredSize(new Dimension(300, 725));
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
		
		JButton btnExportTodot = new JButton("Export to .dot file");
		buttonsPane.add(btnExportTodot);
		
		JPanel varPane = new JPanel();
		optionsPane.add(varPane, BorderLayout.WEST);
		
		JLabel label = new JLabel("Variable:");
		varPane.add(label);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		
		JComboBox<Object> comboBox = new JComboBox<Object>();
		varPane.add(comboBox);
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		graphPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JGraph graph = getJgraph();
		
		graphScroll = new JScrollPane(graph, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		panel.add(graphScroll);
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					createGraph();
					GraphNode gn = new GraphNode(0, "Entry");
					hrefGraph.addVertex(gn);
					astprinter.addFile(new FileInputStream(selectedFile), hrefGraph, gn);			// É PRECISO PASSAR AQUI O GRAFO PARA O PREENCHER PROVAVELMENTE
				} catch (ParseException | IOException e1) {	e1.printStackTrace();}
		        				
				updateGraph();
			}
		});
		
		button_1.addActionListener(new ActionListener() {
			@SuppressWarnings("resource")
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
			        selectedFile = fileChooser.getSelectedFile();
			        
			        try {
						String content = new Scanner(selectedFile).useDelimiter("\\Z").next();
						txtrCodeGoesHere.setText(content);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		      	}
			}
		});
		
		btnExportTodot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = new File("attepmt.dot");
				FileOutputStream out;
				try {
					GraphNode.exporting = true;
					String filename = JOptionPane.showInputDialog(frame, "What name do you want to give the file (must write .dot)?");
					out = new FileOutputStream("dotOutputs/" + filename);
					DOTExporter<GraphNode, RelationshipEdge> exporter = new DOTExporter<GraphNode, RelationshipEdge>(
							new StringNameProvider<GraphNode>(), null,
							new StringEdgeNameProvider<RelationshipEdge>());
					exporter.export(new OutputStreamWriter(out), hrefGraph);
					out.close();
					JOptionPane.showMessageDialog(frame, "File saved in 'dotOutpus' folder as " + filename);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				GraphNode.exporting = false;
			}
		});
		
		//FINALIZE THE FRAME
		frame.pack();
		frame.setVisible(true);
	}

	private void resetGraph() {
	}

	private void createGraph() {
		hrefGraph = new DefaultDirectedGraph<GraphNode, RelationshipEdge>(RelationshipEdge.class);
	}
	
	private JGraph getJgraph() {
	    ListenableGraph<GraphNode, RelationshipEdge> g = new ListenableDirectedGraph<GraphNode, RelationshipEdge>(hrefGraph);	
	    	    
	    JGraph jgraph = new JGraph(new JGraphModelAdapter<GraphNode, RelationshipEdge>(g));
	    jgraph.setGridEnabled(true);
	    jgraph.setGridVisible(true);
	    jgraph.setGridSize(10.0);
	    jgraph.setDragEnabled(true);
	    jgraph.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    jgraph.setVolatileOffscreen(true);
	    	    
	    JGraphFacade facade = new JGraphFacade(jgraph);
	    
	    facade.setIgnoresUnconnectedCells(false);
	    JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
	    layout.setOrientation(SwingConstants.NORTH);
	    layout.setIntraCellSpacing(150.0);
	    layout.setLayoutFromSinks(false);
	    layout.run(facade);
	    Map<?, ?> nested = facade.createNestedMap(true, true);
	    if (nested != null)
	        jgraph.getGraphLayoutCache().edit(nested);

	    System.out.println("Layout complete");
	    	    
	    return jgraph;
	}
	
	private void updateGraph() {
		JGraph graph = getJgraph();
		graph.setAutoResizeGraph(true);
		panel.removeAll();
		
		graphScroll = new JScrollPane(graph, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(graphScroll);
		
		panel.revalidate();
		panel.repaint();
	}
}
