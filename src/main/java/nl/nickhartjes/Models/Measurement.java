package nl.nickhartjes.Models;

import lombok.Data;

import java.util.Calendar;

@Data
public class Measurement {

  private Calendar timestamp;

  private Double value;
}
