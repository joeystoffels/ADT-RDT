package nl.nickhartjes.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Measurement {

  private Timestamp timestamp;
  private Double value;

}
