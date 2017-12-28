package io.rainfall.redis;

import io.lettuce.core.api.sync.RedisStringCommands;
import io.rainfall.ObjectGenerator;
import io.rainfall.Operation;
import io.rainfall.SequenceGenerator;
import io.rainfall.redis.operations.SetOperation;

/**
 * @author Aurelien Broszniowski
 */

public class RedisOperations {
  public static <K, V> Operation set(final ObjectGenerator<K> keyGenerator, final ObjectGenerator<V> valueGenerator,
                                     final SequenceGenerator sequenceGenerator, final RedisStringCommands<K, V> commands) {
    return new SetOperation<K, V>(keyGenerator, valueGenerator, sequenceGenerator, commands);
  }

}
