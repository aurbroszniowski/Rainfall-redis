package io.rainfall;

import jsr166e.ThreadLocalRandom;

import java.io.Serializable;

/**
 * @author Aurelien Broszniowski
 */

public class Person implements Serializable {

  private String name;
  private String firstname;

  public Person(final ThreadLocalRandom random, final long seed) {
    this.name = "Name" + random.nextLong(seed);
    this.firstname = "FirstName" + random.nextLong(seed);
  }

  public String getName() {
    return name;
  }

  public String getFirstname() {
    return firstname;
  }
}
