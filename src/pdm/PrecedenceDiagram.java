package pdm;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrecedenceDiagram {

	private Set<Task> tasks;
	private List<Set<Task>> criticalPaths;
	private Boolean dirtyEarlyStart;
	private Boolean dirtyEarlyFinish;
	private Boolean dirtyLateStart;
	private Boolean dirtyLateFinish;
	private Boolean dirtyTotalFloat;

	public PrecedenceDiagram(Set<Task> tasks) {
		this.dirtyEarlyStart = true;
		this.dirtyEarlyFinish = true;
		this.dirtyLateStart = true;
		this.dirtyLateFinish = true;
		this.dirtyTotalFloat = true;
		this.tasks = tasks;
		this.generateCriticalPaths();
	}

	public PrecedenceDiagram(String filename) {
		this.dirtyEarlyStart = true;
		this.dirtyEarlyFinish = true;
		this.dirtyLateStart = true;
		this.dirtyLateFinish = true;
		this.dirtyTotalFloat = true;
		this.tasks = new HashSet<>();
		this.parseTasks(filename);
		this.generateCriticalPaths();
	}

	public void addTask(Task task) {
		this.tasks.add(task);
		this.dirtyEarlyStart = true;
		this.dirtyEarlyFinish = true;
		this.dirtyLateStart = true;
		this.dirtyLateFinish = true;
		this.dirtyTotalFloat = true;
	}

	public void removeTask(Task task) {
		this.tasks.remove(task);
		this.dirtyEarlyStart = true;
		this.dirtyEarlyFinish = true;
		this.dirtyLateStart = true;
		this.dirtyLateFinish = true;
		this.dirtyTotalFloat = true;
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
		for (char ch : dependencyString.toCharArray()) {
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

	/**
	 * This is called first. Assigns the greatest earliest finish of this task's
	 * dependencies. If this task has no dependencies (i.e. is a "first" task in
	 * the PDM), the earliest start is assigned to 0.
	 */
	public void generateEarliestStartTimes() {
		for (Task task : getTasks()) {
			int earliestStart = 0;
			for (Task dependency : task.getDependencies()) {
				if (dependency.getDuration() > earliestStart)
					earliestStart = dependency.getEarliestFinish();
			}

			task.setEarliestStart(earliestStart);
		}
		this.dirtyEarlyStart = false;
	}

	/**
	 * This is called second. Assigns the earliest finish of this task to be the
	 * sum of its earliest start and its duration.
	 */
	public void generateEarliestFinishTimes() {
		if (this.dirtyEarlyStart) generateEarliestFinishTimes();
		for (Task task : getTasks())
			task.setEarliestFinish(task.getEarliestStart() + task.getDuration());
		this.dirtyEarlyFinish = false;
	}

	public void generateLatestStartTimes() {
		if (this.dirtyLateFinish) this.generateLatestFinishTimes();
		for (Task task : getTasks()) {
			task.setLatestStart(task.getLatestFinish() - task.getDuration());
			if (task.getLatestStart() < 0)
				task.setLatestStart(0);
		}
		this.dirtyLateStart = false;
	}
	
	public void generateLatestFinishTimes() {
		if (this.dirtyEarlyFinish) generateEarliestFinishTimes();
		for (Task task : getTasks()) {
			if (task.getNextTasks().size() > 0) {
				int currentLatestFinish = -1; // starting point, guaranteed to
												// be replaced
				for (Task nextTask : task.getNextTasks()) {
					if (currentLatestFinish == -1) // is the first task in the
													// set of next tasks
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
		this.dirtyLateFinish = false;
	}

	public void generateTimes() {
		this.generateEarliestStartTimes();
		this.generateEarliestFinishTimes();
		this.generateLatestFinishTimes();
		this.generateLatestStartTimes();
	}

	public void generateTotalFloat() {
		if (this.dirtyEarlyStart || this.dirtyLateStart) this.generateTimes();
		for(Task task : getTasks())
			task.setTotalFloat(task.getLatestStart() - task.getEarliestStart());
		this.dirtyTotalFloat = false;
	}

	private List<Task> recursiveDFS(Task t) {
		Set<Task> dependencies = t.getDependencies();
		return null; // TODO
	}

	public void generateCriticalPaths() {
		if (this.dirtyTotalFloat) generateTotalFloat();
		for (Task t : this.tasks) {
			if (t.getDependencies().isEmpty() && t.getTotalFloat() == 0) {
				// TODO
			}
		}
	}

	public List<Set<Task>> getCriticalPaths() {
		return this.criticalPaths;
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
