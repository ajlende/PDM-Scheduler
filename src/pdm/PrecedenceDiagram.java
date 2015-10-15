package pdm;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class PrecedenceDiagram {

	private Set<Task> tasks;


	public PrecedenceDiagram(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public PrecedenceDiagram(String filename) {
		tasks = new HashSet<>();
		this.parseTasks(filename);
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

	public Set<Task> parseDependencies(String dependencyString) {
		Set<Task> dependencies = new HashSet<>();
		for (char ch: dependencyString.toCharArray()) {
			dependencies.add(this.findTask(Character.toString(ch)));
		}
		return dependencies;
	}

	public void parseTasks(String csvFile) {
		BufferedReader br = null;
		String line;
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] taskFields = line.split(cvsSplitBy);
				Set<Task> dependencies = this.parseDependencies(taskFields[2]);
				Task task = new Task(taskFields[0], Integer.parseInt(taskFields[1]), dependencies);
				this.addTask(task);

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

	public void generateEarliestStartTimes() {
		for (Task task : getTasks()) {
			int earliestStart = 0;
			for (Task dependency : task.getDependencies()) {
				if (dependency.getDuration() > earliestStart)
					earliestStart = dependency.getEarliestFinish();
			}

			task.setEarliestStart(earliestStart);
		}
	}

	public void generateEarliestFinishTimes() {
		for (Task task : getTasks())
			task.setEarliestFinish(task.getEarliestStart() + task.getDuration());
	}

	public void generateLatestStartTimes() {
		for (Task task : getTasks()) {
			task.setLatestStart(task.getLatestFinish() - task.getDuration());
			if (task.getLatestStart() < 0)
				task.setLatestStart(0);
		}
	}

	public void generateLatestFinishTimes() {
		for (Task task : getTasks()) {
			if (task.getNextTasks().size() > 0) {
				int currentLatestFinish = -1; // starting point, guaranteed to be replaced
				for (Task nextTask : task.getNextTasks()) {
					if (currentLatestFinish == -1) // is the first task in the set of next tasks
						currentLatestFinish = nextTask.getLatestStart();
					else {
						if (nextTask.getLatestStart() < currentLatestFinish)
							currentLatestFinish = nextTask.getLatestStart();
					}
				}
				task.setLatestFinish(currentLatestFinish);
			} else // doesn't have any next tasks, is a final task
				task.setLatestFinish(task.getEarliestFinish());
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
		StringBuilder builder = new StringBuilder();
		for (Task task : this.getTasks()) {
			builder.append("---------------------\n");
			builder.append(task.toString() + "\n");
			builder.append("---------------------\n");
		}
		return builder.toString();
	}
}
