package pdm;

import java.util.Set;

public class Task {

	private String name;
	private int duration;
	private int earliestStart;
	private int earliestFinish;
	private int latestStart;
	private int latestFinish;
	private int totalFloat;
	private Set<Task> dependencies;
	private boolean isCompleted;

	public Task(String name, int duration, Set<Task> dependencies) {
		this.name = name;
		this.duration = duration;
		this.earliestStart = -1;
		this.earliestFinish = -1;
		this.latestStart = -1;
		this.latestFinish = -1;
		this.totalFloat = -1;
		this.dependencies = dependencies;
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
		builder.append("Total float: " + getTotalFloat() + "\n");
		if (getDependencies().size() > 0) {
			builder.append("Dependencies: ");
			for (Task dep : getDependencies()) {
				builder.append(dep.getName() + " ");
			}
		} else
			builder.append("No dependencies.");
		
		builder.append("\n");
		return builder.toString(); // TODO
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

	public int getTotalFloat() {
		return this.totalFloat;
	}

	public void setTotalFloat(int totalFloat) {
		this.totalFloat = totalFloat;
	}
}