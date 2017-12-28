package io.rainfall;

import jsr166e.ThreadLocalRandom;

/**
 * @author Aurelien Broszniowski
 */

public class PersonGenerator implements ObjectGenerator<Person> {

  @Override
  public Person generate(final Long seed) {
    return new Person(ThreadLocalRandom.current(), seed);
  }

  @Override
  public String getDescription() {
    return null;
  }
}
