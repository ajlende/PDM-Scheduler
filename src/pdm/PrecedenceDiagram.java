package pdm;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrecedenceDiagram {

	private Set<Task> tasks;

	public PrecedenceDiagram(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public PrecedenceDiagram(String filename) {
		tasks = new HashSet<>();
		this.readTasks(filename);
	}

	public void addTask(Task task) {
		this.tasks.add(task);
	}

	public void removeTask(Task task) {
		this.tasks.remove(task);
	}

	public Task findTask(String name) {
		for (Task task : getTasks()) {
			if (task.getName().equals(name))
				return task;
		}
		
		return null; // not found
	}

	public void readTasks(String csvFile) {
		BufferedReader br = null;
		String line;
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] taskFields = line.split(cvsSplitBy);
				List<Task> dependancies = null;
				Task task = new Task(taskFields[0], taskFields[1], dependancies);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Set<Task> getCriticalPaths() {
		return null;
	}
	
	public Set<Task> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
