import javax.swing.JFrame;


public class ExampleGraph extends JFrame {
	public ExampleGraph() {
		super("Example Graph");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Graph graph = new Graph(1000, 600);
		
		//{{x1, y1}, {x2, y2}, ...}
		double[][] points = {{-2, -4}, {0, 5}, {4, 3}, {5, 3}, {7, -1}};
		graph.setPoints(points);
		
		//{{m, b, start_x_interval, end_x_interval}, ...}
		double[][] equations = {{1, 4, -10, 0}, {-4, 1, 0, 10}};
		graph.setEquations(equations);
		
		getContentPane().add(graph);
		
		setVisible(true);
	}
	public static void main(String args[]) {
		new ExampleGraph();
	}
}
