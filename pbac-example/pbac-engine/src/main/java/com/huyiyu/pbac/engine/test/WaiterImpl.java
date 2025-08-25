package com.huyiyu.pbac.engine.test;

public class WaiterImpl implements Waiter {
    @Override
    public void service() {
        System.out.println("may i take your order");
    }
}
