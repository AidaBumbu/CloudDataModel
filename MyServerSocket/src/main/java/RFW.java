public class RFW {
    enum Type { DVD, NDBench; }
    enum Metric {CPU, NetworkIn, NetworkOut, Memory;}
    private static int ID = 0;
    private Type benchmark;
    private Metric workloadMetric;
    private int batchUnit;
    private int batchID;
    private int batchSize;

    public RFW(Type benchmark, Metric workloadMetric, int batchUnit, int batchID, int batchSize) {
        ID = ID + 1;
        this.benchmark = benchmark;
        this.workloadMetric = workloadMetric;
        this.batchUnit = batchUnit;
        this.batchID = batchID;
        this.batchSize = batchSize;
    }

    public int getID() {
        return ID;
    }

    //public void setID(int ID) {
    //    this.ID = ID;
    //}

    public Type getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(Type benchmark) {
        this.benchmark = benchmark;
    }

    public Metric getWorkloadMetric() {
        return workloadMetric;
    }

    public void setWorkloadMetric(Metric workloadMetric) {
        this.workloadMetric = workloadMetric;
    }

    public int getBatchUnit() {
        return batchUnit;
    }

    public void setBatchUnit(int batchUnit) {
        this.batchUnit = batchUnit;
    }

    public int getBatchID() {
        return batchID;
    }

    public void setBatchID(int batchID) {
        this.batchID = batchID;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }




}
