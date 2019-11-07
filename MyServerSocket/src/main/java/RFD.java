import java.util.List;

public class RFD {
    private int RFWid;
    private int lastBatchID;
    private List<List<Double>> samplesRequested;

    public RFD(int RFWid, int lastBatchID, List<List<Double>> samplesRequested) {
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

    public List<List<Double>> getSamplesRequested() {
        return samplesRequested;
    }

    public void setSamplesRequested(List<List<Double>> samplesRequested) {
        this.samplesRequested = samplesRequested;
    }
}
