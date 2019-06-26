package nl.nickhartjes;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.component.Orchestrator;

import java.io.File;

@Slf4j
public class DataGeneration {

    public static void main(String... args) {

        boolean started = false;
        try {
            while (started == false) {
                File tmpDir = new File("go.txt");
                started = tmpDir.exists();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            log.debug(e.toString());
            System.exit(1);
        }
        new Orchestrator();
    }

}
