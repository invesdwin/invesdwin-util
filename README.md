# invesdwin-util

This project consists of common utilities and helpers that make a developers life easier. Most of these utilities are used for performance critical financial strategy backtesting. Especially the FDate, Decimal and AHistoricalCache implementations drive this the most. But there is even more useful stuff for other use cases.

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

#### `Decimal` 
This type wraps a `Double` value and handles rounding properly when needed internally to increase precision of calculations for mathematical use cases where performance is crititcal while a good precision is required. It eases working with `Double` by providing a fluent API with `ScaledDecimals` (e.g. `ByteSize` or `Percent`, more complex ones like `Money`, `Price` or `Amount` are used in other frameworks built on this) to make transformations between units easier and aggregate functions (e.g. sum, avg, ...) to ease working with lots of `Decimals`. It makes `Double` more like `BigDecimal`, without being as slow as `BigDecimal`, nor being as imprecise as `Double` normally is. And the API makes mathematical code much easier to write and understand.
#### `FDate`
This stands for "Fast Date". It is essentially a long value with a cached hashcode for making it faster as a key in maps. Each time operation is handled by the fastest time library available, which is [Joda-Time](http://www.joda.org/joda-time) in this case. Also comes with a `FDateBuilder` that makes creation of specific dates easier.
#### `Instant`
Makes time-tracking in batch processing easier, prints itself out as a Duration with the precision of System.nanoTime().
#### `Duration`
Useful for time duration calculations and prints them out as an extended version of [ISO-8601 Duration](https://en.wikipedia.org/wiki/ISO_8601#Durations)

## Caches
#### `ALoadingCache`
A simple map like class that loads missing values via your custom load method. It finds the fastest map implementation depending on the configuration methods `isHighConcurrency()` and `getMaximumSize()` to get the optimum out of each calculation you might want to cache while providing a common way to access those results. This class can be quite useful in factories that want to cache instances. For example providing cached instances of historical caches that were instantiated by a specific parameter set.
#### `AHistoricalCache`
This wraps multiple ALoadingCaches to provide time sensitive lookups of keys and values by providing convenience queries for next and previous ones, even bulk loading. It caches a bit of the history to be a very fast accessor for financial datapoints during backtesting. Derived data from calculations like financial indicators and signals are a good use case for this cache.
#### `AGapHistoricalCache`
This is a historical cache that works with sources like a database, data file or rest service containing data points by time. It can even handle gaps efficiently (while providing random access) like weekends in daily data or sparse data like ticks. Financial data sources like bars or ticks are a good use case for this cache.

## Concurrency

#### `Executors`
Similar to `java.util.concurrent.Executors`, just with the convention to name each thread pool for easier debugging of thread dumps or in `jvisualvm` and to extend the pool by a few load handling helper methods. Also registering a JVM shutdown hook for each pool and ensuring the default uncaught exception handler is used.
#### `Futures`
This makes working with lots of tasks in thread pools easier by providing methods for bulk-handling those futures.
#### `ICloseableIterable` and `ICloseableIterator`
Provides a way to create a pipes and filters work chain with steps to parallelize on. Chaining of tasks can make complex workloads far easier to implement. For example loading and financial data cache from a rest service and transforming that into a binary local cache format in a parallel fashion becomes easy with this design.

## Beans
#### `APropertyChangeSupported`
This is a base class that handles a lazily instantiated `PropertyChangeSupport` in a threadsafe manner. It becomes more useful when it is combined with an aspect that handles the setters properly without you needing to implement the calls to `firePropertyChange(...)` manually. See the [invesdwin-aspects](https://github.com/subes/invesdwin-aspects) project for such an aspect.
#### `AValueObject`
This type should be used as the base class for all beans. It provides reflective `toString()`, `hashCode()`, `equals()` and `compareTo()` per default using [commons-lang](https://commons.apache.org/proper/commons-lang) (with you being able to override if needed). Also it allows to copy equally named properties from other objects via a `mergeFrom(...)` method that utilizes the [commons-beanutils](http://commons.apache.org/proper/commons-beanutils) framework. And it provides a deep `clone()` method per default that is implemented via the [FST](https://github.com/RuedigerMoeller/fast-serialization) framework. Though it still offers a `shallowClone()` method for when this is needed. Also classes extending from this one will get [bean path constants classes](https://github.com/subes/invesdwin-norva#constants) generated for it and will get [QueryDSL](http://www.querydsl.com) query classes generated as long as the specific annotation processors are activated for this.
#### `DirtyTracker`
`AValueObjects` also have something called a `DirtyTracker`, which is a class that allows you to check if the values of a bean tree have been changed or not. It utilizes the `PropertyChangeSupport` to keep track of changes in the objects tree. Though please be sure to always implement `firePropertyChange(...)` properly in your setters or use an aspect for this. Also make sure to only change values by calling the setters or else the `DirtyTracker` will not notice changes in the fields themselves. The `DirtyTracker` is very handy for UI development where the UI framework does not handle dirty state properly or not at all.
#### `Pair`, `Triple`, `Quadruple`
These can be useful as combined keys for caches (e.g. ALoadingCache) or when multiple return values are required for a method and you don't want to write another value object for this.

## Others
#### `Assertions`
The popular [AssertJ](http://joel-costigliola.github.io/assertj) fluent API extended by `FDate` and `Decimal`. Though sometimes it might be better to use if-throw statements instead of this API, since it might be a performance bottleneck in some cases. Where it is not, it is a very good ease in doing defensive coding. Best approach is to use it as a default and replace it by manual code where the profiler tells that it is too slow (should not be too many cases anyway).
#### `ADelegateComparator`
Ever wondered if your comparator will result in ascending or descending order? This class will make the desired order easier to get by making that an explicit decision during sort calls. You also only have to give it the property to compare and it will handle casting, null checks and other things for you.
#### `Strings`, `Reflections`, `Objects`, ...
Each one being a one-stop class to find the utility method you are searching for by providing a [static facade](https://github.com/subes/invesdwin-norva#static-facade) to the most useful frameworks and providing its own set of operations which are missing from the ones that already exist.
