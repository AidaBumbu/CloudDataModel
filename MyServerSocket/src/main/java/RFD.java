import java.util.List;

public class RFD {
    private int RFWid;
    private int lastBatchID;
    private List<Workload> samplesRequested;

    public RFD(int RFWid, int lastBatchID, List<Workload> samplesRequested) {
        this.RFWid = RFWid;
        this.lastBatchID = lastBatchID;
        this.samplesRequested = samplesRequested;
    }

    public int getRFWid() {
        return RFWid;
    }

    public void setRFWid(int RFWid) {
        this.RFWid = RFWid;
    }

    public int getLastBatchID() {
        return lastBatchID;
    }

    public void setLastBatchID(int lastBatchID) {
        this.lastBatchID = lastBatchID;
    }

    public List<Workload> getSamplesRequested() {
        return samplesRequested;
    }

    public void setSamplesRequested(List<Workload> samplesRequested) {
        this.samplesRequested = samplesRequested;
    }
}
