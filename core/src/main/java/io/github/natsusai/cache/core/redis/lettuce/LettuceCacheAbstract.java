package io.github.natsusai.cache.core.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lettuce Cache Abstract
 *
 * @param <K>
 * @author liufuhong
 * @since 2020-03-10 15:56
 */

@Getter
public abstract class LettuceCacheAbstract<K, V> {

  protected final RedisCommands<K, V>           commands;
  protected       RedisClient                   redisClient;
  protected       StatefulRedisConnection<K, V> connection;

  /**
   * 缓存前缀
   * <p>
   * 通常为应用名称
   * </p>
   */
  protected final String PREFIX;
  protected final String CONNECTOR = ":";

  public LettuceCacheAbstract(String prefix, RedisClient redisClient) {
    this.PREFIX = prefix == null ? "" : prefix;
    this.redisClient = redisClient;
    this.connection = this.redisClient.connect(new KryoCodec<>());
    this.commands = this.connection.sync();
  }

  public LettuceCacheAbstract(String prefix, RedisClient redisClient, RedisCodec<K, V> redisCodec) {
    this.PREFIX = prefix == null ? "" : prefix;
    this.redisClient = redisClient;
    this.connection = this.redisClient.connect(redisCodec);
    this.commands = this.connection.sync();
  }

  public LettuceCacheAbstract(String prefix, String host, int port) {
    this.PREFIX = prefix == null ? "" : prefix;
    this.redisClient = RedisClient.create(RedisURI.builder()
                           .withHost(host)
                           .withPort(port)
                           .build());
    this.connection = this.redisClient.connect(new KryoCodec<>());
    this.commands = this.connection.sync();
  }

  public LettuceCacheAbstract(String prefix, String host, int port, String password) {
    this.PREFIX = prefix == null ? "" : prefix;
    this.redisClient = RedisClient.create(RedisURI.builder()
                                         .withHost(host)
                                         .withPort(port)
                                         .withPassword(password)
                                         .build());
    this.connection = this.redisClient.connect(new KryoCodec<>());
    this.commands = this.connection.sync();
  }

  public LettuceCacheAbstract(String prefix, String host, int port, String password, int database) {
    this.PREFIX = prefix == null ? "" : prefix;
    this.redisClient = RedisClient.create(RedisURI.builder()
                                         .withHost(host)
                                         .withPort(port)
                                         .withPassword(password)
                                         .withDatabase(database)
                                         .build());
    this.connection = this.redisClient.connect(new KryoCodec<>());
    this.commands = this.connection.sync();
  }

  public LettuceCacheAbstract(String prefix, String host, int port, String password, int database, RedisCodec<K, V> redisCodec) {
    this.PREFIX = prefix == null ? "" : prefix;
    this.redisClient = RedisClient.create(RedisURI.builder()
                                         .withHost(host)
                                         .withPort(port)
                                         .withPassword(password)
                                         .withDatabase(database)
                                         .build());
    this.connection = this.redisClient.connect(redisCodec);
    this.commands = this.connection.sync();
  }

  /**
   * 生成缓存key
   * @param key 缓存标识/id
   * @param namespace 命名空间
   * @return 缓存key
   */
  protected String buildCacheKey(String key, String namespace) {
    return Stream.of(PREFIX.toUpperCase(), namespace, key)
        .filter(StringUtils::isNotBlank).collect(Collectors.joining(CONNECTOR));
  }

  /**
   * 生成命名空间所有缓存的表达式（模糊查询）
   * @param namespace 命名空间
   * @return 表达式字符串
   */
  protected String buildNamespacePatternKey(String namespace) {
    return Stream.of(PREFIX.toUpperCase(), namespace, "*")
        .filter(StringUtils::isNotBlank).collect(Collectors.joining(CONNECTOR));
  }
}
