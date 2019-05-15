package nl.nickhartjes;

import com.github.javafaker.Faker;
import lombok.extern.java.Log;
import nl.nickhartjes.Models.Measurement;
import nl.nickhartjes.Persistance.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Log
public class Main {


  public static void main(String[] args) {

    Persistance persistance = new Persistance();
//    persistance.add(new MongoPersistance());
    persistance.add(new InfluxPersistance());
//    persistance.add(new MSSqlPersistance());

    int a = 1000000;
    int batchSize = 100;
    Double min = 10d;
    Double max = 10d;
    List<Measurement> measurements = new ArrayList<>();

    Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
    Date startDate = new Date();

    int count = 0;
    Double randomNum;
    for (int i = 0; i < a; i++) {
      randomNum = ThreadLocalRandom.current().nextDouble(min, max + 1);

      Calendar now = calendar;
//      java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());
      Measurement measurement = new Measurement();
      measurement.setTimestamp(now);
      measurement.setValue(randomNum);
      measurements.add(measurement);

      if((count % batchSize) == 0){
        System.out.println(count);
        persistance.save(measurements);
        measurements = new ArrayList<>();
      }


      calendar.add(Calendar.SECOND, 1);
      min = randomNum / 100 * 95;
      max = randomNum / 100 * 102;
      count++;
    }

    persistance.close();

    Date endDate = new Date();
    int numSeconds = (int) ((endDate.getTime() - startDate.getTime()) / 1000);
    System.out.println(min);
    System.out.println(max);
    System.out.println(calendar.getTime());
    System.out.println(count);

    System.out.println(numSeconds);
  }
}
