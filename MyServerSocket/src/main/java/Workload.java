public class Workload {
    private int CPUUtilization_Average;
    private int NetworkIn_Average;
    private int NetworkOut_Average;
    private double MemoryUtilization_Average;
    private double Final_Target;

    public Workload(int CPUUtilization_Average, int networkIn_Average, int networkOut_Average, double memoryUtilization_Average, double final_Target) {
        this.CPUUtilization_Average = CPUUtilization_Average;
        NetworkIn_Average = networkIn_Average;
        NetworkOut_Average = networkOut_Average;
        MemoryUtilization_Average = memoryUtilization_Average;
        Final_Target = final_Target;
    }

    public int getCPUUtilization_Average() {
        return CPUUtilization_Average;
    }

    public void setCPUUtilization_Average(int CPUUtilization_Average) {
        this.CPUUtilization_Average = CPUUtilization_Average;
    }

    public int getNetworkIn_Average() {
        return NetworkIn_Average;
    }

    public void setNetworkIn_Average(int networkIn_Average) {
        NetworkIn_Average = networkIn_Average;
    }

    public int getNetworkOut_Average() {
        return NetworkOut_Average;
    }

    public void setNetworkOut_Average(int networkOut_Average) {
        NetworkOut_Average = networkOut_Average;
    }

    public double getMemoryUtilization_Average() {
        return MemoryUtilization_Average;
    }

    public void setMemoryUtilization_Average(double memoryUtilization_Average) {
        MemoryUtilization_Average = memoryUtilization_Average;
    }

    public double getFinal_Target() {
        return Final_Target;
    }

    public void setFinal_Target(double final_Target) {
        Final_Target = final_Target;
    }
}
