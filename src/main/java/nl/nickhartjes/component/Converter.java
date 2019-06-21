package nl.nickhartjes.component;

public class Converter {

    public static long nanosecondsToSeconds(long nanoseconds) {
        return nanoseconds / 1000000000;
    }

    public static long nanosecondsToMiliseconds(long nanoseconds) {
        return nanoseconds / 1000000;
    }
}
