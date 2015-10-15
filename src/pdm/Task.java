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

	public Task(String name, String duration, List<Task> dependencies) {
		this.setName(name);
		this.setDuration(Integer.parseInt(duration));
		this.dependencies = dependencies;
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
			for (int i = 0; i < getDependencies().size(); i++)
				builder.append(getDependencies().get(i).getName() + " ");
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