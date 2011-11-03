import javax.swing.JFrame;


public class ExampleGraph extends JFrame {
	public ExampleGraph() {
		super("Example Graph");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Graph graph = new Graph(1000, 600);
		
		double[][] points = {{-2, -4}, {0, 5}, {4, 3}, {5, 3}, {7, -1}};
		graph.setPoints(points);
		
		double[][] equations = {{1, 4, -10, 0}, {-4, 1, 0, 10}};
		graph.setEquations(equations);
		
		getContentPane().add(graph);
		
		setVisible(true);
	}
	public static void main(String args[]) {
		new ExampleGraph();
	}
}
