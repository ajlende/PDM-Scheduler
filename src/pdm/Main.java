package pdm;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
    	PrecedenceDiagram pdm = new PrecedenceDiagram();
        Task A = new Task("A", 2, new HashSet<>());
        Task B = new Task("B", 3, new HashSet<>());
        Task C = new Task("C", 2, new HashSet<>());
        Task D = new Task("D", 3, new HashSet<>());
        Task E = new Task("E", 2, new HashSet<>());
        Task F = new Task("F", 1, new HashSet<>());
        Task G = new Task("G", 4, new HashSet<>());
        Task H = new Task("H", 5, new HashSet<>());
        Task I = new Task("I", 3, new HashSet<>());
        Task J = new Task("J", 3, new HashSet<>());
        Task K = new Task("K", 2, new HashSet<>());
        Task L = new Task("L", 2, new HashSet<>());

        D.addDependency(A);
        E.addDependency(B);
        E.addDependency(C);
        F.addDependency(A);
        F.addDependency(B);
        G.addDependency(A);
        H.addDependency(C);
        I.addDependency(D);
        I.addDependency(F);
        J.addDependency(E);
        J.addDependency(G);
        K.addDependency(I);
        L.addDependency(K);

        pdm.addTask(A);
        pdm.addTask(B);
        pdm.addTask(C);
        pdm.addTask(D);
        pdm.addTask(E);
        pdm.addTask(F);
        pdm.addTask(G);
        pdm.addTask(H);
        pdm.addTask(I);
        pdm.addTask(J);
        pdm.addTask(K);
        pdm.addTask(L);

        Set<Task> critical = pdm.getCriticalPaths();
        System.out.print("Critical Path Tasks: ");
        for (Task t : critical) System.out.print(t.getName() + " ");
        System.out.println();

        System.out.println(pdm.toString());
    }
}
