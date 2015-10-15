package pdm;

import java.util.HashSet;
import java.util.Set;

public class Task {

	private String name;
	private int duration;
	private int earliestStart;
	private int earliestFinish;
	private int latestStart;
	private int latestFinish;
	private Set<Task> dependencies;
	private Set<Task> nextTasks;
	private boolean isCompleted;

	public Task(String name, int duration, Set<Task> dependencies) {
		this.name = name;
		this.duration = duration;
		this.earliestStart = 0;
		this.earliestFinish = 0;
		this.latestStart = 0;
		this.latestFinish = 0;
		this.dependencies = dependencies;
		this.nextTasks = new HashSet<Task>();
		this.isCompleted = false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Task " + getName() + "\n");
		builder.append("Duration: " + getDuration() + "\n");
		builder.append("Earliest start: " + getEarliestStart() + "\n");
		builder.append("Earliest finish: " + getEarliestFinish() + "\n");
		builder.append("Latest start: " + getLatestStart() + "\n");
		builder.append("Latest finish: " + getLatestFinish() + "\n");
		if (getDependencies().size() > 0) {
			builder.append("Dependencies: ");
			for (Task dep : getDependencies()) {
				builder.append(dep.getName() + " ");
			}
		} else
			builder.append("No dependencies.");

		builder.append("\n");
		return builder.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getEarliestStart() {
		return earliestStart;
	}

	public void setEarliestStart(int earliestStart) {
		this.earliestStart = earliestStart;
	}

	public int getEarliestFinish() {
		return earliestFinish;
	}

	public void setEarliestFinish(int earliestFinish) {
		this.earliestFinish = earliestFinish;
	}

	public int getLatestStart() {
		return latestStart;
	}

	public void setLatestStart(int latestStart) {
		this.latestStart = latestStart;
	}

	public int getLatestFinish() {
		return latestFinish;
	}

	public void setLatestFinish(int latestFinish) {
		this.latestFinish = latestFinish;
	}

	public Set<Task> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Set<Task> dependencies) {
		this.dependencies = dependencies;
	}

	public Set<Task> getNextTasks() {
		return nextTasks;
	}

	public void addNextTask(Task nextTask) {
		this.nextTasks.add(nextTask);
	}

	public void removeNextTask(Task nextTask) {
		this.nextTasks.remove(nextTask);
	}

	public void addDependency(Task dependency) {
		this.dependencies.add(dependency);
	}

	public void removeDependency(Task dependency) {
		this.dependencies.remove(dependency);
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
}