package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * ForwardAgent - generated from the UML diagram by diagram2app.
 */
@Embeddable
public class ForwardAgent {

    @Column
    private String destination;
    @Column
    private String forwardingType;

    protected ForwardAgent() {
    }

    public ForwardAgent(String destination, String forwardingType) {
        this.destination = destination;
        this.forwardingType = forwardingType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getForwardingType() {
        return forwardingType;
    }

    public void setForwardingType(String forwardingType) {
        this.forwardingType = forwardingType;
    }

    public String generateLink() {
        // TODO(diagram2app): the behaviour of ForwardAgent.generateLink() is not specified in the diagram.
        throw new UnsupportedOperationException("ForwardAgent.generateLink() is not implemented: behaviour not specified");
    }

    public void forward() {
        // TODO(diagram2app): the behaviour of ForwardAgent.forward() is not specified in the diagram.
        throw new UnsupportedOperationException("ForwardAgent.forward() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "ForwardAgent{" + "destination=" + getDestination() + ", " + "forwardingType=" + getForwardingType() + "}";
    }
}
