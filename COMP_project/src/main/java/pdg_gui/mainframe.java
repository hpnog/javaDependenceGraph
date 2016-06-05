package pdg_gui;

import com.github.javaparser.ParseException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import graphStructures.GraphNode;
import graphStructures.RelationshipEdge;
import org.jgraph.JGraph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.ListenableDirectedGraph;
import pdg.PDGCore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;

/**
 * The Class mainframe - Main GUI.
 */
public class mainframe extends JFrame {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The selected file. */
    private File selectedFile;
    
    /** The href graph. */
    @SuppressWarnings("rawtypes")
	private DirectedGraph<GraphNode, RelationshipEdge> hrefGraph;
    
    /** The ast printer. */
    private PDGCore astPrinter = new PDGCore();

    /** The panel. */
    private JPanel panel;
    
    /** The graph scroll. */
    private JScrollPane graphScroll;
    
    /** The console text. */
    private JTextArea consoleText;

    /**
     * Instantiates a new mainframe and initializes all the containers as well as their needed listeners.
     */
    private mainframe() {
        final JFrame frame = new JFrame();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(1000, 400));
        frame.setPreferredSize(new Dimension(1300, 800));
        frame.setTitle("Java PDG Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //FRAME CONTENT PANE
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        frame.setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        //CONTENT PANELS
        JPanel codePanel = new JPanel();
        codePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

        contentPane.add(codePanel, BorderLayout.WEST);

        createGraph();
        resetGraph();
        codePanel.setLayout(new BorderLayout(0, 0));

        JTextArea txtCodeGoesHere = new JTextArea();
        txtCodeGoesHere.setTabSize(2);
        txtCodeGoesHere.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtCodeGoesHere.setEditable(false);
        txtCodeGoesHere.setBorder(codePanel.getBorder());

        JScrollPane scroll = new JScrollPane(txtCodeGoesHere, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(300, 725));
        codePanel.add(scroll);
        txtCodeGoesHere.setText("Code goes here");

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

        panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        graphPane.add(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JGraph graph = getJGraph();

        graphScroll = new JScrollPane(graph, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        panel.add(graphScroll);

        JPanel console = new JPanel();
        contentPane.add(console, BorderLayout.SOUTH);
        console.setLayout(new BorderLayout(0, 0));

        consoleText = new JTextArea();
        consoleText.setTabSize(2);
        consoleText.setEditable(false);
        consoleText.setBorder(codePanel.getBorder());

        JScrollPane consoleScroll = new JScrollPane(consoleText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        consoleScroll.setPreferredSize(new Dimension(19, 200));
        console.add(consoleScroll);

        button.addActionListener(e -> {
            consoleText.setText("----------------------------------------------------\n");
            runAnalysisAndMakeGraph();
        });

        button_1.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();

                try {
                    @SuppressWarnings("resource")
					String content = new Scanner(selectedFile).useDelimiter("\\Z").next();
                    txtCodeGoesHere.setText(content);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnExportTodot.addActionListener(e -> {
            FileOutputStream out;
            try {
                checkIfFolderExists();
                GraphNode.exporting = true;
                String filename = JOptionPane.showInputDialog(frame, "What name do you want to give the file (must write .dot)?");
                if (filename == null) {
                    GraphNode.exporting = false;
                    return;
                }
                out = new FileOutputStream("dotOutputs/" + filename);
                @SuppressWarnings("rawtypes")
				DOTExporter<GraphNode, RelationshipEdge> exporter = new DOTExporter<>(
                        new StringNameProvider<>(), null,
                        new StringEdgeNameProvider<>());
                exporter.export(new OutputStreamWriter(out), hrefGraph);
                out.close();
                consoleText.setText(consoleText.getText() + "Exoprted Graph to *.dot file\n");
                JOptionPane.showMessageDialog(frame, "File saved in 'dotOutpus' folder as " + filename);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            GraphNode.exporting = false;
        });

        //FINALIZE THE FRAME
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new PDGCore();
            try {
                new mainframe();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Check if folder exists.
     *
     * @return true, if successful
     */
    private boolean checkIfFolderExists() {
        File theDir = new File("dotOutputs");
        return !theDir.exists() && theDir.mkdir();
    }

    /**
     * Reset graph.
     */
    private void resetGraph() {
    }

    /**
     * Creates the graph.
     */
    private void createGraph() {
        hrefGraph = new DefaultDirectedGraph<>(RelationshipEdge.class);
    }

    /**
     * Gets the j graph.
     *
     * @return the j graph
     */
    private JGraph getJGraph() {
        @SuppressWarnings("rawtypes")
		ListenableGraph<GraphNode, RelationshipEdge> g = new ListenableDirectedGraph<>(hrefGraph);

        JGraph jgraph = new JGraph(new JGraphModelAdapter<>(g));
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

    /**
     * Update graph.
     */
    private void updateGraph() {
        JGraph graph = getJGraph();
        graph.setAutoResizeGraph(true);
        panel.removeAll();

        graphScroll = new JScrollPane(graph, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(graphScroll);

        panel.revalidate();
        panel.repaint();
    }

    /**
     * Run analysis and make graph.
     */
    private void runAnalysisAndMakeGraph() {
        try {
            createGraph();
            GraphNode gn = new GraphNode(0, "Entry");
            hrefGraph.addVertex(gn);
            if (astPrinter.addFile(new FileInputStream(selectedFile), hrefGraph, gn, consoleText))
                updateGraph();
        } catch (ParseException | IOException e1) {
            e1.printStackTrace();
        }
    }
}