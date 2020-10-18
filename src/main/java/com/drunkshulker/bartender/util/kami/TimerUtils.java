package com.drunkshulker.bartender.util.kami;

public class TimerUtils {
        long time = getCurrentTime();
        //protected set

        protected long getCurrentTime() {
        return System.currentTimeMillis();
        }

        public void reset() {
            long offset =0;
        time = getCurrentTime() + offset;
        }

        public enum TimeUnit {
            MILLISECONDS(1L),
            TICKS(50L),
            SECONDS(1000L),
            MINUTES(60000L);

            public long multiplier = 0L;

            TimeUnit(long m) {
                this.multiplier = m;
            }
        }


        // Implementations


    // public static class StopTimer extends TimerUtils {
    //      private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    //       StopTimer(TimeUnit timeUnit){
                //         this.timeUnit=timeUnit;
    //      }
    //      long stop() {
    //          return (getCurrentTime() - time) / timeUnit.multiplier;
    //       }
    //   }
}