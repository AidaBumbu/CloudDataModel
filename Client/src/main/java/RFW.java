public class RFW {
    enum Type { dvdtest, dvdtrain, ndbenchtest, ndbenchtrain }
    enum Metric {cpu, networkin, networkout, memory}
    private int ID;
    private String benchmark;
    private String metric;
    private int batchUnit;
    private int batchID;
    private int batchSize;

    public RFW(String benchmark, String metric, int batchUnit, int batchID, int batchSize) {
        ID = ID + 1;
        this.benchmark = benchmark;
        this.metric = metric;
        this.batchUnit = batchUnit;
        this.batchID = batchID;
        this.batchSize = batchSize;
    }

    public int getID() {
        return ID;
    }

    public Type getBenchmark() {return Type.valueOf(benchmark.toLowerCase());}

    //public String getBenchmark() {return benchmark;}

    public void setBenchmark(String benchmark) { this.benchmark = benchmark; }

    public Metric getMetric() {return Metric.valueOf(metric.toLowerCase());}

    //public String getMetric() {return metric};

    public void setMetric(String metric) {
        this.metric = metric;
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
