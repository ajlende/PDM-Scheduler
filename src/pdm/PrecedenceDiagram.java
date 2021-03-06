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

/**
 * A PDM schedule with a set of tasks
 * 
 * @author Andrew Bowler, Alex Lende
 */
public class PrecedenceDiagram {

	/**
	 * A PDM has a set of Tasks, and a set of all Tasks that contribute to a
	 * possible critical path.
	 */
	private Set<Task> tasks;
	private Set<Task> criticalPaths;

	/**
	 * Checks for method calls. If these are false, that means the related
	 * methods have already been called and the PDM's state has already been
	 * updated, and no action is taken.
	 */
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

	/**
	 * Adds a task if it doesn't already exist in the PDM, otherwise throws an
	 * exception.
	 */
	public void addTask(Task task) {
		for (Task t : getTasks()) {
			if (t.getName().equals(task.getName()))
				throw new IllegalArgumentException();
		}
		tasks.add(task);
		this.dirtyEarlyTimes = true;
		this.dirtyLateTimes = true;
		this.dirtyTotalFloat = true;
		this.dirtyCriticalPath = true;
	}

	/**
	 * Removes the specified task from the PDM
	 */
	public void removeTask(Task task) {
		this.tasks.remove(task);
		for (Task t : this.tasks) {
			for (Task following : t.getFollowingTasks()) {
				if (following.equals(task))
					t.removeFollowingTask(following);
			}
			for (Task dep : t.getPrecedingTasks()) {
				if (dep.equals(task))
					t.removeDependency(dep);
			}
		}
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
	 * Returns a list of in-order critical paths from the set of Tasks that are
	 * part of at least one critical path, through DFS traversal.
	 */
	public List<List<Task>> getCriticalPaths() {
		if (dirtyCriticalPath)
			generateCriticalPaths();

		List<List<Task>> sortedCriticalPaths = new ArrayList<>();
		Stack<Task> taskStack;
		int idx = 0;
		for (Task t : this.criticalPaths) {
			if (t.getPrecedingTasks().isEmpty()) {
				taskStack = new Stack<>();
				sortedCriticalPaths.add(new ArrayList<>());
				sortedCriticalPaths.get(idx).add(t);
				taskStack.push(t);
				while (!taskStack.isEmpty()) {
					Task tmp = taskStack.pop();
					if (tmp.getFollowingTasks().isEmpty()) {
						break;
					} else {
						for (Task u : tmp.getFollowingTasks()) {
							if (this.criticalPaths.contains(u)) {
								List<Task> currentPath = sortedCriticalPaths.get(idx);
								if (u.getPrecedingTasks().contains(currentPath.get(currentPath.size() - 1)))
									sortedCriticalPaths.get(idx).add(u);
								else {
									List<Task> newPath = new ArrayList<>(currentPath);
									newPath.remove(newPath.size() - 1);
									newPath.add(u);
									sortedCriticalPaths.add(++idx, newPath);
								}
							}
							taskStack.push(u);
						}
					}
				}
				idx++;
			}
		}
		return sortedCriticalPaths;
	}

	/**
	 * Returns the set of all tasks in the PDM
	 */
	public Set<Task> getTasks() {
		return tasks;
	}

	/**
	 * Prints out each task of the PDM and all of its relevant information (see
	 * Task.toString() implementation).
	 */
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
	 * Parses the file to generate a PDM Diagram
	 *
	 * @param filepath
	 */
	private void parseTasks(String filepath) {
		Scanner scanner = null;
		try {
			File file = new File(filepath);
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
					throw e;
				} catch (NoSuchElementException e) {
					System.out.println("Error reading file. Stop trying to break it.");
					throw e;
				}
				Task task = new Task(taskName, Integer.parseInt(duration), dependencies);
				for (Task dependency : dependencies) {
					Task dep = findTask(dependency.getName());
					if (dep != null)
						dep.addFollowingTask(task);
					else {
						System.out.println("Error linking dependencies to following tasks.");
						throw new IllegalStateException();
					}
				}

				try {
					addTask(task);
				} catch (IllegalArgumentException e) {
					System.out.println("This task already exists in the "
							+ "PDM and cannot be modified. Stop trying to break it.");
					throw e;
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Bogus file. Stop trying to break it.");
		} catch (NumberFormatException e) {
			System.out.println("Input a number for the duration. Stop trying to break it.");
			throw e;
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
