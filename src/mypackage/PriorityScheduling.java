package mypackage;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PriorityScheduling {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter number of processes: ");
        int n = scanner.nextInt();
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("Enter process name, arrival time, burst time, and priority: ");
            String name = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            int priority = scanner.nextInt();
            processes.add(new Process(name, arrivalTime, burstTime, priority));
        }

        // Preemptive Priority Scheduling
        List<GanttChartEntry> ganttChart = new ArrayList<>();
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt((Process p) -> p.priority).thenComparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        List<Process> completedProcesses = new ArrayList<>(); // List to store completed processes

        while (!processes.isEmpty() || !pq.isEmpty()) {
            for (Iterator<Process> it = processes.iterator(); it.hasNext(); ) {
                Process p = it.next();
                if (p.arrivalTime <= currentTime) {
                    pq.add(p);
                    it.remove();
                }
            }

            if (pq.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = pq.poll();
            ganttChart.add(new GanttChartEntry(currentProcess.name, currentTime, currentTime + 1));
            currentProcess.burstTime--;

            if (currentProcess.burstTime > 0) {
                pq.add(currentProcess);
            } else {
                completedProcesses.add(currentProcess); // Add completed process to the list
            }

            currentTime++;
        }

        // Visualize Gantt Chart
        new GanttChart(ganttChart, completedProcesses);
    }
}

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int priority;
    int waitingTime;
    int turnAroundTime;

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.waitingTime = 0;
        this.turnAroundTime = 0;
    }
}

class GanttChartEntry {
    String processName;
    int startTime;
    int endTime;

    public GanttChartEntry(String processName, int startTime, int endTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

class GanttChart extends JFrame {
    private List<GanttChartEntry> ganttChart;
    private List<Process> completedProcesses;

    public GanttChart(List<GanttChartEntry> ganttChart, List<Process> completedProcesses) {
        this.ganttChart = ganttChart;
        this.completedProcesses = completedProcesses;
        setTitle("Gantt Chart");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int x = 50;
        int y = 50;
        int height = 50;

        for (GanttChartEntry entry : ganttChart) {
            int width = (entry.endTime - entry.startTime) * 30; // Triple the original scale for better visualization
            g.drawRect(x, y, width, height);
            g.drawString(entry.processName, x + (width / 2), y + (height / 2));
            g.drawString(entry.startTime + "", x, y + height + 15);
            x += width;
        }

        g.drawString(ganttChart.get(ganttChart.size() - 1).endTime + "", x, y + height + 15); // end time of the last process

        // Display the table
        int tableY = y + height + 40;
        g.drawString("Process", 50, tableY);
        g.drawString("Arrival", 150, tableY);
        g.drawString("Burst", 250, tableY);
        g.drawString("Waiting", 350, tableY);
        g.drawString("Turnaround", 450, tableY);
        g.drawString("Priority", 550, tableY);

        int rowY = tableY + 20;
        for (Process p : completedProcesses) {
            g.drawString(p.name, 50, rowY);
            g.drawString(Integer.toString(p.arrivalTime), 150, rowY);
            g.drawString(Integer.toString(p.burstTime), 250, rowY);
            g.drawString(Integer.toString(p.waitingTime), 350, rowY);
            g.drawString(Integer.toString(p.turnAroundTime), 450, rowY);
            g.drawString(Integer.toString(p.priority), 550, rowY);
            rowY += 20;
        }
    }
}
