# invesdwin-util

This project consists of common utilities and helpers that make a developers life easier. Most of these utilities are used for performance critical financial strategy backtesting. Especially the FDate, Decimal and AHistoricalCache implementations drive this the most.

## Maven

Releases and snapshots are deployed to this maven repository:
```
http://invesdwin.de:8081/artifactory/invesdwin-oss
```

Dependency declaration:
```xml
<dependency>
	<groupId>de.invesdwin</groupId>
	<artifactId>invesdwin-util</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Types

All of these types are immutable in nature, which makes code for calculations safer.

### `Decimal` 
this type wraps a Double value and handles rounding properly when needed internally to increase precision of calculations for mathematical use cases where performance is crititcal while a good precision is required. It eases working with Double by providing a fluent API with ScaledDecimals (e.g. ByteSize or Percent, more complex ones like Money, Price or Amount are used in other frameworks built on this) to make transformations between units easier and aggregate functions (e.g. sum, avg, ...) to ease working with lots of Decimals. It makes Double more like BigDecimal, without being as slow as BigDecimal, nor being as imprecise as Double normally is. And the API makes mathematical code much easier to write and understand.
### `FDate`
this stands for "Fast Date". It is essentially a long value with a cached hashcode for making it faster as a key in maps. Each time operation is handled by the fastest time library available, which is http://www.joda.org/joda-time/ in this case. Also comes with a FDateBuilder that makes creation of specific dates easier.
### `Instant`
makes time-tracking in batch processing easier, prints itself out as a Duration with the precision of System.nanoTime().
### `Duration`
useful for time duration calculations and prints them out as an extended version of https://en.wikipedia.org/wiki/ISO_8601#Durations

## Caches
### `ALoadingCache`
a simple map like class that loads missing values via your custom load method. It finds the fastest map implementation depending on the configuration methods `isHighConcurrency()` and `getMaximumSize()` to get the optimum out of each calculation you might want to cache while providing a common way to access those results.
### `AHistoricalCache`
this wraps multiple ALoadingCaches to provide time sensitive lookups of keys and values by providing convenience queries for next and previous ones, even bulk loading. It caches a bit of the history to be a very fast accessor for financial datapoints during backtesting.
### `AGapHistoricalCache`
this is a historical cache that works with sources like a database, data file or rest service containing data points by time. It can even handle gaps efficiently (while providing random access) like weekends in daily data or sparse data like ticks. 

## Concurrency

### `Executors`
similar to java.util.concurrent.Executors, just with the convention to name each thread pool for easier debugging of thread dumps and to extend the pool by a few load handling helper methods. 
### `Futures`
this makes working with lots of tasks in thread pools easier by providing methods for bulk-handling those futures.
### `ICloseableIterable` and `ICloseableIterator`
provides a way to create a pipes and filters work chain with steps to parallelize on. Chaining of tasks can make complex workloads far easier to implement. For example loading and financial data cache from a rest service and transforming that into a binary local cache format in a parallel fashion becomes easy with this design.

## Others
### `Assertions`
the popular fluent API http://joel-costigliola.github.io/assertj/ extended by FDate and Decimal. Though sometimes it might be better to use if-throw statements instead of this API, since it might be a performance bottleneck in some cases. Where it is not, it is a very good ease in doing defensive coding. Best approach is to use it as a default and replace it by manual code where the profiler tells that it is too slow (should not be too many cases anyway).
### `ADelegateComparator`
ever wondered if your comparator will result in ascending or descending order? This class will make the desired order easier to get by making that an explicit decision during sort calls.
### `Strings`, `Reflections`, `Objects`, ...
being a one-stop class to find the utility method you are searching for by providing a static facade to the most useful frameworks and providing its own set of operations which are missing from the ones that already exist.
