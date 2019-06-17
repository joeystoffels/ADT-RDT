package nl.nickhartjes.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Calendar;

@Data
@AllArgsConstructor
public class Measurement {

  private Calendar timestamp;
  private Double value;

}
