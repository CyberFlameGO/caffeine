/*
 * Copyright 2014 Ben Manes. All Rights Reserved.
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
package com.github.benmanes.caffeine.cache.stats;

import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * A thread-safe {@link StatsCounter} implementation for use by {@link Cache} implementors.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class ConcurrentStatsCounter implements StatsCounter {
  private final LongAdder hitCount;
  private final LongAdder missCount;
  private final LongAdder loadSuccessCount;
  private final LongAdder loadFailureCount;
  private final LongAdder totalLoadTime;
  private final LongAdder evictionCount;

  /**
   * Constructs an instance with all counts initialized to zero.
   */
  public ConcurrentStatsCounter() {
    hitCount = new LongAdder();
    missCount = new LongAdder();
    loadSuccessCount = new LongAdder();
    loadFailureCount = new LongAdder();
    totalLoadTime = new LongAdder();
    evictionCount = new LongAdder();
  }

  @Override
  public void recordHits(@Nonnegative int count) {
    hitCount.add(count);
  }

  @Override
  public void recordMisses(@Nonnegative int count) {
    missCount.add(count);
  }

  @Override
  public void recordLoadSuccess(@Nonnegative long loadTime) {
    loadSuccessCount.increment();
    totalLoadTime.add(loadTime);
  }

  @Override
  public void recordLoadFailure(@Nonnegative long loadTime) {
    loadFailureCount.increment();
    totalLoadTime.add(loadTime);
  }

  @Override
  public void recordEviction() {
    evictionCount.increment();
  }

  @Override
  public CacheStats snapshot() {
    return new CacheStats(
        hitCount.sum(),
        missCount.sum(),
        loadSuccessCount.sum(),
        loadFailureCount.sum(),
        totalLoadTime.sum(),
        evictionCount.sum());
  }

  /** Increments all counters by the values in {@code other}. */
  public void incrementBy(@Nonnull StatsCounter other) {
    CacheStats otherStats = other.snapshot();
    hitCount.add(otherStats.hitCount());
    missCount.add(otherStats.missCount());
    loadSuccessCount.add(otherStats.loadSuccessCount());
    loadFailureCount.add(otherStats.loadFailureCount());
    totalLoadTime.add(otherStats.totalLoadTime());
    evictionCount.add(otherStats.evictionCount());
  }
}