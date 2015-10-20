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
	private int totalFloat;
	private Set<Task> precedingTasks;
	private Set<Task> followingTasks;
	private boolean isCompleted;

	public Task(String name, int duration, Set<Task> precedingTasks) {
		this.name = name;
		this.duration = duration;
		this.earliestStart = -1;
		this.earliestFinish = -1;
		this.latestStart = Integer.MAX_VALUE;
		this.latestFinish = Integer.MAX_VALUE;
		this.totalFloat = -1;
		this.precedingTasks = precedingTasks;
		this.followingTasks = new HashSet<>();
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
		builder.append("Dependencies: ");
		if (this.getPrecedingTasks().size() > 0) {
			for (Task t : this.getPrecedingTasks()) {
				builder.append(t.getName() + " ");
			}
		} else {
			builder.append("No dependencies");
		}
		builder.append("\n");
		builder.append("Following: ");
		if (this.getFollowingTasks().size() > 0) {
			for (Task t : this.getFollowingTasks()) {
				builder.append(t.getName() + " ");
			}
		} else {
			builder.append("No following");
		}
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

	public Set<Task> getPrecedingTasks() {
		return precedingTasks;
	}

	public Set<Task> getFollowingTasks() {
		return followingTasks;
	}

	public void addFollowingTask(Task followingTask) {
		this.followingTasks.add(followingTask);
	}

	public void removeFollowingTask(Task followingTask) {
		this.followingTasks.remove(followingTask);
	}

	public void addDependency(Task dependency) {
		this.precedingTasks.add(dependency);
	}

	public void removeDependency(Task dependency) {
		this.precedingTasks.remove(dependency);
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