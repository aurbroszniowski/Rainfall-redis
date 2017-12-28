/*
 * Copyright 2014 Aurélien Broszniowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
