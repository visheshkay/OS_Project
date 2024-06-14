package mypackage;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PriorityScheduling {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter number of processes: ");
        int n = scanner.nextInt();
        List<Process> processes = new ArrayList<>();
        List<Process> processes1 = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("Enter process name, arrival time, burst time, and priority: ");
            String name = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            int priority = scanner.nextInt();
            processes.add(new Process(name, arrivalTime, burstTime, priority));
            processes1.add(new Process(name, arrivalTime, burstTime, priority));
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
            currentProcess.waitingTime += currentTime - currentProcess.arrivalTime; // Calculate waiting time
            currentProcess.turnAroundTime = currentProcess.waitingTime + currentProcess.burstTime; // Calculate turnaround time
            ganttChart.add(new GanttChartEntry(currentProcess.name, currentTime, currentTime + 1));
            currentProcess.burstTime--;

            if (currentProcess.burstTime > 0) {
                pq.add(currentProcess);
            } else {
            	currentProcess.completionTime=currentTime+1;
                completedProcesses.add(currentProcess); // Add completed process to the list
            }

            currentTime++;
        }

        // Update burst times and waiting times in completedProcesses
        for (Process p:completedProcesses) {
        	for(Process inputProcess:processes1) {
        		if (p.name.equals(inputProcess.name)) {
            // Update burst time to match the input burst time
            p.burstTime = inputProcess.burstTime;
            p.turnAroundTime=p.completionTime-inputProcess.arrivalTime;

            // Calculate waiting time as turnaround time - burst time
            p.waitingTime = p.turnAroundTime - inputProcess.burstTime;
        		}
        	}
        }
//        	System.out.println(completedProcesses.size()+" "+processes1.size());


        // Calculate average waiting time and average turnaround time
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        for (Process p : completedProcesses) {
            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.turnAroundTime;
        }
        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();

        // Visualize Gantt Chart and display average times
        new GanttChart(ganttChart, completedProcesses, avgWaitingTime, avgTurnaroundTime, currentTime);
    }
}

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int priority;
    int waitingTime;
    int completionTime;
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
    private double avgWaitingTime;
    private double avgTurnaroundTime;
    private int totalTime;

    public GanttChart(List<GanttChartEntry> ganttChart, List<Process> completedProcesses, double avgWaitingTime, double avgTurnaroundTime, int totalTime) {
        this.ganttChart = ganttChart;
        this.completedProcesses = completedProcesses;
        this.avgWaitingTime = avgWaitingTime;
        this.avgTurnaroundTime = avgTurnaroundTime;
        this.totalTime = totalTime;
        setTitle("Gantt Chart with Average Times");
        setSize(1000, 600); // Adjusted window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int x = 50;
        int y = 50;
        int height = 50;
        int scale = 30; // Scale for time units

        for (GanttChartEntry entry : ganttChart) {
            int width = (entry.endTime - entry.startTime) * scale;
            g.drawRect(x, y, width, height);
            g.drawString(entry.processName, x + (width / 2), y + (height / 2));
            g.drawString(entry.startTime + "", x, y + height + 15);
            x += width;
        }

        g.drawString(ganttChart.get(ganttChart.size() - 1).endTime + "", x, y + height + 15);

        // Display the table
        int tableY = y + height + 40;
        g.drawString("Process", 50, tableY);
        g.drawString("Arrival", 150, tableY);
        g.drawString("Burst", 250, tableY);
        g.drawString("Waiting", 350, tableY);
        g.drawString("Turnaround", 450, tableY);
        g.drawString("Completion Time", 550, tableY);
        g.drawString("Priority", 700, tableY);

        int rowY = tableY + 20;
        for (Process p : completedProcesses) {
            g.drawString(p.name, 50, rowY);
            g.drawString(Integer.toString(p.arrivalTime), 150, rowY);
            g.drawString(Integer.toString(p.burstTime), 250, rowY);
            g.drawString(Integer.toString(p.waitingTime), 350, rowY);
            g.drawString(Integer.toString(p.turnAroundTime), 450, rowY);
            g.drawString(Integer.toString(p.completionTime), 550, rowY);
            g.drawString(Integer.toString(p.priority), 700, rowY);
            rowY += 20;
        }

        // Display average times
        g.drawString("Average Waiting Time: " + avgWaitingTime, 50, rowY + 20);
        g.drawString("Average Turnaround Time: " + avgTurnaroundTime, 50, rowY + 40);

        // Display bars for each process
        int barHeight = 20; // Adjust bar height
        int barY = rowY + 60;
        int totalBarWidth = completedProcesses.stream().mapToInt(p -> p.completionTime * scale).sum(); // Calculate total bar width
        int startX = 350; // Calculate starting x-coordinate for centering
        g.drawString("Completion Time",450,rowY+50);
        for (Process p : completedProcesses) {
            int barWidth = p.completionTime * scale; // Adjust bar width based on turnaround time
            g.setColor(Color.BLUE);
            g.fillRect(startX, barY, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawString(p.name, 320, barY + barHeight + 15); // Add completion time label// Move to the next bar position
            barY += 40;
        }
    }
}
