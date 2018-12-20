package com.jacle.kafka.signalHandler;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * 设置信号量来针对不同的kill，处理不同的操作
 * <p>
 * kill  指定处理内容
 */
public class AppSignalHandler implements SignalHandler {

    public static final String TERM_15 = "TERM";
    public static final String QUIT_3 = "QUIT";


    public void registerSignal(String signalName) {
        Signal signal = new Signal(signalName);
        Signal.handle(signal, this);
    }


    @Override
    public void handle(Signal signal) {
        if (signal.getName().equals(TERM_15)) {
            TestThread.shutdownFlag=true;
            System.out.println("TermFlag=true");
            // System.exit(0);
        } else if (signal.getName().equals(QUIT_3)) {
            System.out.println("QuitFlag=true");
        }
    }
}
