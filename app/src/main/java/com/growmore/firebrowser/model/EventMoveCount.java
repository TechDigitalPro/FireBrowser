package com.growmore.firebrowser.model;

public class EventMoveCount {
    private int current;
    private int total;

    public EventMoveCount(int current, int total){
        this.current = current;
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
