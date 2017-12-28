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

package io.rainfall.redis.operations;

import io.lettuce.core.api.sync.RedisStringCommands;
import io.rainfall.AssertionEvaluator;
import io.rainfall.Configuration;
import io.rainfall.ObjectGenerator;
import io.rainfall.Operation;
import io.rainfall.SequenceGenerator;
import io.rainfall.TestException;
import io.rainfall.redis.statistics.RedisResult;
import io.rainfall.statistics.StatisticsHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Aurelien Broszniowski
 */

public class SetOperation<K, V> implements Operation {
  private final ObjectGenerator<K> keyGenerator;
  private final ObjectGenerator<V> valueGenerator;
  private final SequenceGenerator sequenceGenerator;
  private final RedisStringCommands<K, V> commands;
  private final String name;

  public SetOperation(final ObjectGenerator<K> keyGenerator, final ObjectGenerator<V> valueGenerator,
                      final SequenceGenerator sequenceGenerator, final RedisStringCommands<K,V> commands) {
    this.keyGenerator = keyGenerator;
    this.valueGenerator = valueGenerator;
    this.sequenceGenerator = sequenceGenerator;
    this.commands = commands;
    this.name = this.valueGenerator.generate(1L).getClass().getName();
  }

  @Override
  public void exec(final StatisticsHolder statisticsHolder, final Map<Class<? extends Configuration>, Configuration> configurations,
                   final List<AssertionEvaluator> assertions) throws TestException {
    long seed = sequenceGenerator.next();
    K key = keyGenerator.generate(seed);
    V value = valueGenerator.generate(seed);
    long start = statisticsHolder.getTimeInNs();
    String ret = commands.set(key, value);
    long end = statisticsHolder.getTimeInNs();
    if ("OK".equals(ret)) {
      statisticsHolder.record(this.name, (end - start), RedisResult.SET);
    } else {
      statisticsHolder.record(this.name, (end - start), RedisResult.FAIL);
    }
  }

  @Override
  public List<String> getDescription() {
    return Arrays.asList("Set operation", "Value type = " + this.name);
  }
}
