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
			List<List<Task>> criticals = pdm.getCriticalPaths();
			System.out.print("Critical Path Tasks: ");
			for (List<Task> criticalPath : criticals) {
				for (Task t : criticalPath)
					System.out.print(t.getName() + " ");
				System.out.println();
			}
		} catch (Exception e) {
			return;
		}
	}
}
