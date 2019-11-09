import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class RFWmixIn {
    RFWmixIn(@JsonProperty("benchmark") String benchmark,
             @JsonProperty("metric") String metric,
             @JsonProperty("batchUnit") int batchUnit,
             @JsonProperty("batchID") int batchID,
             @JsonProperty("batchSize") int batchSize,
             @JsonProperty("id") int id) {

    }
}
