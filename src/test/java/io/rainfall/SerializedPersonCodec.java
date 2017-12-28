package io.rainfall;

import io.lettuce.core.codec.RedisCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Aurelien Broszniowski
 */

public class SerializedPersonCodec implements RedisCodec<Long, Person> {
  private Charset charset = Charset.forName("UTF-8");

  @Override
  public Long decodeKey(ByteBuffer bytes) {
    return Long.getLong(charset.decode(bytes).toString());
  }

  @Override
  public Person decodeValue(ByteBuffer bytes) {
    try {
      byte[] array = new byte[bytes.remaining()];
      bytes.get(array);
      ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(array));
      return (Person)is.readObject();
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public ByteBuffer encodeKey(Long key) {
    return charset.encode("" + key);
  }

  @Override
  public ByteBuffer encodeValue(Person value) {
    try {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      ObjectOutputStream os = new ObjectOutputStream(bytes);
      os.writeObject(value);
      return ByteBuffer.wrap(bytes.toByteArray());
    } catch (IOException e) {
      return null;
    }
  }
}
