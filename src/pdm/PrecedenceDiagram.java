package pdm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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
	private boolean dirtyFollowing;

	public PrecedenceDiagram() {
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
		this.dirtyFollowing = true;
		this.tasks = new HashSet<>();
		this.criticalPaths = new HashSet<>();
	}

	public PrecedenceDiagram(Set<Task> tasks) {
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
		this.dirtyFollowing = true;
		this.tasks = tasks;
		this.criticalPaths = new HashSet<>();
	}

	public PrecedenceDiagram(String filename) {
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
		this.dirtyFollowing = true;
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
		this.dirtyFollowing = true;
	}

	public void removeTask(Task task) {
		// TODO Remove links in other tasks
		this.tasks.remove(task);
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
		this.dirtyFollowing = true;
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
	 * Helper method for parseTasks
	 *
	 * @param dependencyString
	 * @return dependencies
	 */
	private Set<Task> parseDependencies(String dependencyString) throws IllegalArgumentException {
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
	 * Parses the csv file to generate a PDM Diagram
	 *
	 * @param csvFile
	 */
	public void parseTasks(String csvFile) {
		Scanner scanner = null;
		try {
			File file = new File(csvFile);
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Scanner lineReader = new Scanner(line);
				String taskName = lineReader.next();
				String duration = lineReader.next();
				lineReader.close();
				String dependencyString = scanner.next();
				Set<Task> dependencies = null;
				try {
					dependencies = parseDependencies(dependencyString);
				} catch (IllegalArgumentException e) {
					System.out.println("Dependency task not previously specified. Stop trying to break it.");
					return;
				}
				Task task = new Task(taskName, Integer.parseInt(duration), dependencies);
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
	 * BFS traversal from each node with preceding tasks to fill in the
	 * following Tasks
	 */
	private void generateFollowingTasks() {
		Queue<Task> taskQueue;
		for (Task t : this.getTasks()) {
			if (!t.getPrecedingTasks().isEmpty()) {
				taskQueue = new LinkedList<>();
				taskQueue.add(t);
				while(!taskQueue.isEmpty()) {
					Task tmp = taskQueue.remove();
					for(Task u : tmp.getPrecedingTasks()) {
						taskQueue.add(u);
						u.addFollowingTask(tmp);
					}
				}
			}
		}
		this.dirtyFollowing = false;
	}

	/**
	 * DFS traversal from each of the tasks with no dependencies
	 */
	public void generateEarlyTimes() {
		if (this.dirtyFollowing) this.generateFollowingTasks();
		Stack<Task> taskStack;
		for (Task t : this.getTasks()) {
			if (t.getPrecedingTasks().isEmpty()) {
				taskStack = new Stack<>();
				t.setEarliestStart(0);
				t.setEarliestFinish(t.getDuration());
				taskStack.push(t);
				while(!taskStack.isEmpty()) {
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
		if (this.dirtyEarlyTimes) this.generateEarlyTimes();
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
				while(!taskStack.isEmpty()) {
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
	public void generateTotalFloat() {
		if (this.dirtyEarlyTimes || this.dirtyLateTimes) this.generateTimes();
		for(Task task : getTasks())
			task.setTotalFloat(task.getLatestStart() - task.getEarliestStart());
		this.dirtyTotalFloat = false;
	}

	/**
	 * Runs all methods to get the early, late, and total float times
	 */
	public void generateTimes() {
		if (this.dirtyFollowing) this.generateFollowingTasks();
		if (this.dirtyEarlyTimes) this.generateEarlyTimes();
		if (this.dirtyLateTimes) this.generateLateTimes();
		if (this.dirtyTotalFloat) this.generateTotalFloat();
	}

	/**
	 * Adds all tasks with a total float of zero to the criticalPaths field
	 */
	public void generateCriticalPaths() {
		if (this.dirtyTotalFloat) generateTotalFloat();
		criticalPaths.addAll(this.tasks.stream().filter(t -> t.getTotalFloat() == 0).collect(Collectors.toList()));
		this.dirtyCriticalPath = false;
	}

	public Set<Task> getCriticalPaths() {
		if (dirtyCriticalPath) generateCriticalPaths();
		return this.criticalPaths;
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
}
