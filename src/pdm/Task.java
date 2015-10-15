package pdm;

import java.util.List;

public class Task {
	
	private String name;
	private int duration;
	private int earliestStart;
	private int earliestFinish;
	private int latestStart;
	private int latestFinish;
	private List<Task> dependencies;
	private boolean isCompleted;
	
	public Task(String name, List<Task> dependencies) {
		this.setName(name);
		this.dependencies = dependencies;
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
	
	public List<Task> getDependencies() {
		return dependencies;
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