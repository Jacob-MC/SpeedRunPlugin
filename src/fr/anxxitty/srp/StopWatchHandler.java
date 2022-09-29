package fr.anxxitty.srp;

import org.apache.commons.lang.time.StopWatch;

public class StopWatchHandler {

    public static void startTimer(StopWatch stopWatch) {
        stopWatch.start();
    }

    public static void stopTimer(StopWatch stopWatch) {
        stopWatch.stop();
    }

    public static void resetTimer(StopWatch stopWatch) { stopWatch.reset(); }

    public static String checkTime(StopWatch stopWatch) {
        long milliseconds = (stopWatch.getTime() % 1000);
        int two_digit_ms = (int) (milliseconds/10);
        int totalSecs = (int) (stopWatch.getTime()/1000.0);
        int hours = totalSecs/3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        String time;
        if (hours > 0) {
                time = String.format("%01d:%02d:%02d.%02d", hours, minutes, seconds, two_digit_ms);
        } else {
                time = String.format("%01d:%02d.%02d", minutes, seconds, two_digit_ms);
        }
        return time;
    }

}
