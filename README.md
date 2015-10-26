# PDM-Scheduler
Generates a PDM schedule for a project plan and computes the critical path.

### Running It

***This program must be run in Java 8.***

Open up the project in Eclipse and run `Main.java`. Change the file parameter in the logic in `main()` (it's obvious where) to whatever `.txt` file you want to test with.

### Files

* `Main.java`
 * Creates a PDM from the given file, schedules and links all of the tasks together, and computes the critical path.
* `PrecedenceDiagram.java`
 * This is the PDM. It contains a schedule of tasks, and contains logic for generating early/late times, and critical paths.
* `Task.java`
 * A POJO that describes a PDM task.
* `test_files/`
 * Contains all test files of this program

More detailed documentation is in the source files as Javadoc.
