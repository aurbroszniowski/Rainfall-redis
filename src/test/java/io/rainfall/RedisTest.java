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

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.rainfall.configuration.ConcurrencyConfig;
import io.rainfall.configuration.ReportingConfig;
import io.rainfall.generator.LongGenerator;
import io.rainfall.redis.RedisOperations;
import io.rainfall.redis.statistics.RedisResult;
import org.junit.Test;


import static io.rainfall.configuration.ReportingConfig.html;
import static io.rainfall.execution.Executions.during;
import static io.rainfall.generator.SequencesGenerator.sequentially;
import static io.rainfall.unit.TimeDivision.minutes;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Aurelien Broszniowski
 */

public class RedisTest {


  @Test
  public void simpleLoad() throws SyntaxException {
    RedisClient client = RedisClient.create(RedisURI.create("redis://localhost"));
    StatefulRedisConnection<Long, Person> connection = client.connect(new SerializedPersonCodec());
    RedisStringCommands<Long, Person> commands  = connection.sync();

    ObjectGenerator<Long> keyGenerator = new LongGenerator();
    ObjectGenerator<Person> valueGenerator = new PersonGenerator();

    ConcurrencyConfig concurrency = ConcurrencyConfig.concurrencyConfig().threads(4).timeout(50, MINUTES);

    ScenarioRun run = Runner.setUp(
        Scenario.scenario("Warm up phase").exec(
            RedisOperations.set(keyGenerator, valueGenerator, sequentially(), commands)
        ))
        .warmup(during(5, minutes))
        .executed(during(5, minutes))
        .config(concurrency, ReportingConfig.report(RedisResult.class).log(html()));

    run.start();

    connection.close();
    client.shutdown();
  }

  @Test
  public void testConnection() {
    RedisClient redisClient = RedisClient.create(RedisURI.create("redis://localhost"));
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisStringCommands<String, String> commands  = connection.sync();
    String key = "key";
    commands.set(key, "val");


    System.out.println("Connected to Redis: " + commands.get(key));

    connection.close();
    redisClient.shutdown();
  }
}
