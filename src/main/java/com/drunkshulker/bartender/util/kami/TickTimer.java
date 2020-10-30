package com.drunkshulker.bartender.util.kami;

public class TickTimer extends TimerUtils {
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    public TickTimer(TimeUnit timeUnit){
        this.timeUnit=timeUnit;
    }

    public boolean tick(long delay) {
        if (getCurrentTime() - time > delay * timeUnit.multiplier) {
            
            return true;
        } else {
            return false;
        }
    }
}