package pdm;

import java.util.List;

/**
 * @author Andrew Bowler, Alex Lende
 */
public class Main {

	public static void main(String[] args) {
		try {
			// REPLACE PARAMETER WITH YOUR OWN TEST FILE
			PrecedenceDiagram pdm = new PrecedenceDiagram("test_files/hw3.txt");
			pdm.generateTimes();
			System.out.println(pdm.toString());
			List<Task> critical = pdm.getCriticalPath();
			System.out.print("Critical Path Tasks: ");
			for (Task t : critical)
				System.out.print(t.getName() + " ");
		} catch (Exception e) {
			return;
		}
	}
}
