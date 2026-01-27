package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ClusterInfo {
    @JsonProperty("kafkaVersion")
    private String kafkaVersion;

    @JsonProperty("brokers")
    private List<BrokerInfo> brokers;

    public ClusterInfo() {
    }

    public String getKafkaVersion() {
        return kafkaVersion;
    }

    public void setKafkaVersion(String kafkaVersion) {
        this.kafkaVersion = kafkaVersion;
    }

    public List<BrokerInfo> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<BrokerInfo> brokers) {
        this.brokers = brokers;
    }
}
