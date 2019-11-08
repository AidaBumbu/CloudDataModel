public class RFW {
    enum Type { dvdtest, dvdtrain, ndbenchtest, ndbenchtrain }
    enum Metric {cpu, networkin, networkout, memory}
    private static int ID = 0;
    private Type benchmark;
    private Metric metric;
    private int batchUnit;
    private int batchID;
    private int batchSize;

    public RFW(String benchmark, String metric, int batchUnit, int batchID, int batchSize) {
        ID = ID + 1;
        this.benchmark = Type.valueOf(benchmark.toLowerCase());
        this.metric = Metric.valueOf(metric.toLowerCase());
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

    public void setBenchmark(String benchmark) { this.benchmark = Type.valueOf(benchmark.toLowerCase()); }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = Metric.valueOf(metric.toLowerCase());
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
