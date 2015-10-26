package pdm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class PrecedenceDiagram {

	private Set<Task> tasks;
	private Set<Task> criticalPaths;
	private boolean dirtyEarlyTimes;
	private boolean dirtyLateTimes;
	private boolean dirtyTotalFloat;
	private boolean dirtyCriticalPath;

	public PrecedenceDiagram() {
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
		this.tasks = new HashSet<>();
		this.criticalPaths = new HashSet<>();
	}

	public PrecedenceDiagram(Set<Task> tasks) {
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
		this.tasks = tasks;
		this.criticalPaths = new HashSet<>();
	}

	public PrecedenceDiagram(String filename) {
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
		this.tasks = new HashSet<>();
		this.criticalPaths = new HashSet<>();
		this.parseTasks(filename);
	}

	public void addTask(Task task) {
		this.tasks.add(task);
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
	}

	public void removeTask(Task task) {
		// TODO Remove links in other tasks
		this.tasks.remove(task);
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
	}

	/**
	 * Looks up the task with a given name
	 *
	 * @param name
	 * @return task with the given name
	 */
	public Task findTask(String name) {
		for (Task task : getTasks()) {
			if (task.getName().equals(name))
				return task;
		}

		return null; // not found
	}

	/**
	 * Runs all methods to get the early, late, and total float times
	 */
	public void generateTimes() {
		if (this.dirtyEarlyTimes)
			this.generateEarlyTimes();
		if (this.dirtyLateTimes)
			this.generateLateTimes();
		if (this.dirtyTotalFloat)
			this.generateTotalFloat();
	}

	/**
	 * Adds all tasks with a total float of zero to the criticalPaths field
	 */
	public void generateCriticalPaths() {
		if (this.dirtyTotalFloat)
			generateTotalFloat();
		criticalPaths.addAll(this.tasks.stream().filter(t -> t.getTotalFloat() == 0).collect(Collectors.toList()));
		this.dirtyCriticalPath = false;
	}

	/**
	 * Returns the in-order critical path of the PDM.
	 */
	public List<Task> getCriticalPath() {
		if (dirtyCriticalPath)
			generateCriticalPaths();
		Set<Task> criticalPathSet = this.criticalPaths;

		// get the first task in the critical path (no dependencies)
		List<Task> sortedCriticalPaths = new ArrayList<>();
		for (Task task : criticalPathSet) {
			if (task.getPrecedingTasks().isEmpty()) {
				sortedCriticalPaths.add(task);
				break;
			}
		}
		// Link up the rest of the critical path such that we have a sorted
		// critical path
		for (int i = 0; i < criticalPathSet.size() - 1; i++) {
			for (Task task : criticalPathSet) {
				if (sortedCriticalPaths.get(i).getFollowingTasks().contains(task)) {
					sortedCriticalPaths.add(task);
				}
			}
		}
		return sortedCriticalPaths;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Task task : this.getTasks()) {
			builder.append(">>>>>>>>>>>>>>>>>>>>>\n");
			builder.append(task.toString() + "\n");
			builder.append("<<<<<<<<<<<<<<<<<<<<<\n");
		}
		return builder.toString();
	}

	/**
	 * Parses the csv file to generate a PDM Diagram
	 *
	 * @param csvFile
	 */
	private void parseTasks(String csvFile) {
		Scanner scanner = null;
		try {
			File file = new File(csvFile);
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Scanner lineReader = new Scanner(line);
				String taskName = lineReader.next();
				String duration = lineReader.next();
				String dependencyString = null;
				if (lineReader.hasNext())
					dependencyString = lineReader.next();
				lineReader.close();
				Set<Task> dependencies = null;
				try {
					dependencies = parseDependencies(dependencyString);
				} catch (IllegalArgumentException e) {
					System.out.println("Dependency task not previously specified. Stop trying to break it.");
					return;
				} catch (NoSuchElementException e) {
					System.out.println("Error reading file. Stop trying to break it.");
				}
				Task task = new Task(taskName, Integer.parseInt(duration), dependencies);
				for (Task dependency : dependencies) {
					Task dep = findTask(dependency.getName());
					if (dep != null)
						dep.addFollowingTask(task);
					else {
						System.out.println("Error linking dependencies to following tasks.");
						return;
					}
				}
				addTask(task);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Bogus file. Stop trying to break it.");
		} catch (NumberFormatException e) {
			System.out.println("Input a number for the duration. Stop trying to break it.");
		}
	}

	/**
	 * Helper method for parseTasks
	 *
	 * @param dependencyString
	 * @return dependencies
	 */
	private Set<Task> parseDependencies(String dependencyString) throws IllegalArgumentException {
		if (dependencyString == null) // no dependencies were specified
			return new HashSet<>();
		Set<Task> dependencies = new HashSet<>();
		String[] dependencyArray = dependencyString.split(",");
		for (String dep : dependencyArray) {
			Task dependency = findTask(dep);
			if (dependency == null)
				throw new IllegalArgumentException();
			else
				dependencies.add(dependency);
		}
		return dependencies;
	}

	/**
	 * DFS traversal from each of the tasks with no dependencies
	 */
	private void generateEarlyTimes() {
		Stack<Task> taskStack;
		for (Task t : this.getTasks()) {
			if (t.getPrecedingTasks().isEmpty()) {
				taskStack = new Stack<>();
				t.setEarliestStart(0);
				t.setEarliestFinish(t.getDuration());
				taskStack.push(t);
				while (!taskStack.isEmpty()) {
					Task tmp = taskStack.pop();
					for (Task u : tmp.getFollowingTasks()) {
						if (tmp.getEarliestFinish() > u.getEarliestStart()) {
							u.setEarliestStart(tmp.getEarliestFinish());
							u.setEarliestFinish(u.getEarliestStart() + u.getDuration());
						}
						taskStack.push(u);
					}
				}
			}
		}
		this.dirtyEarlyTimes = false;
	}

	/**
	 * DFS traversal from each of the tasks with no dependencies
	 */
	private void generateLateTimes() {
		if (this.dirtyEarlyTimes)
			this.generateEarlyTimes();
		Stack<Task> taskStack;
		int maxTime = 0;
		for (Task t : this.getTasks()) {
			if (t.getFollowingTasks().isEmpty() && t.getEarliestFinish() > maxTime) {
				maxTime = t.getEarliestFinish();
			}
		}
		for (Task t : this.getTasks()) {
			if (t.getFollowingTasks().isEmpty()) {
				taskStack = new Stack<>();
				t.setLatestFinish(maxTime);
				t.setLatestStart(t.getLatestFinish() - t.getDuration());
				taskStack.push(t);
				while (!taskStack.isEmpty()) {
					Task tmp = taskStack.pop();
					for (Task u : tmp.getPrecedingTasks()) {
						if (tmp.getLatestStart() < u.getLatestFinish()) {
							u.setLatestFinish(tmp.getLatestStart());
							u.setLatestStart(u.getLatestFinish() - u.getDuration());
						}
						taskStack.push(u);
					}
				}
			}
		}
		this.dirtyLateTimes = false;
	}

	/**
	 * Generates the total float for each task
	 */
	private void generateTotalFloat() {
		if (this.dirtyEarlyTimes || this.dirtyLateTimes)
			this.generateTimes();
		for (Task task : getTasks())
			task.setTotalFloat(task.getLatestStart() - task.getEarliestStart());
		this.dirtyTotalFloat = false;
	}
}
