package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrokerInfo {
    @JsonProperty("id")
    private int id;

    @JsonProperty("rack")
    private String rack;

    public BrokerInfo() {
    }

    public BrokerInfo(int id, String rack) {
        this.id = id;
        this.rack = rack;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }
}
