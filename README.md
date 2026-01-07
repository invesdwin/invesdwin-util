# invesdwin-util

This project consists of common utilities and helpers that make a developers life easier. Most of these utilities are used for performance critical financial strategy backtesting. Especially the FDate, Decimal and AHistoricalCache implementations drive this the most. But there is even more useful stuff for other use cases.

## Maven

Releases and snapshots are deployed to this maven repository:
```
https://invesdwin.de/repo/invesdwin-oss-remote/
```

Dependency declaration:
```xml
<dependency>
	<groupId>de.invesdwin</groupId>
	<artifactId>invesdwin-util</artifactId>
	<version>1.0.4</version><!---project.version.invesdwin-util-parent-->
</dependency>
```

## Types

All of these types are immutable in nature, which makes code for calculations safer.

#### `Decimal` 
This type wraps a `double` value and handles rounding properly when needed internally to increase precision of calculations for mathematical use cases where performance is crititcal while a good precision is required. It eases working with `double` by providing a fluent API with `ScaledDecimals` (e.g. `ByteSize` or `Percent`, more complex ones like `Money`, `Price` or `Amount` are used in other frameworks built on this) to make transformations between units easier and aggregate functions (e.g. sum, avg, ...) to ease working with lots of `Decimals`. It makes `double` more like `BigDecimal`, without being as slow as `BigDecimal`, nor being as imprecise as `double` normally is. And the API makes mathematical code much easier to write and understand.
#### `FDate`
This stands for "Fast Date". It is essentially a long value and is suitable as a key in maps. Each time operation is handled by the fastest time library available, which is [Joda-Time](http://www.joda.org/joda-time) mostly. Also comes with a `FDateBuilder` that makes creation of specific dates easier.
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
Provides a way to create a pipes and filters work chain with steps to parallelize on. Chaining of tasks can make complex workloads far easier to implement. For example loading a financial data cache from a rest service and transforming that into a binary local cache format in a parallel fashion becomes easy with this design.
#### `@ParallelSuite`
Use this annotation instead of `@Suite` from JUnit to parallelize test suites/classes/methods. The annotation provides parameters to configure parallelization needs and control inheritance.

## Beans
#### `APropertyChangeSupported`
This is a base class that handles a lazily instantiated `PropertyChangeSupport` in a threadsafe manner. It becomes more useful when it is combined with an aspect that handles the setters properly without you needing to implement the calls to `firePropertyChange(...)` manually. See the [invesdwin-aspects](https://github.com/subes/invesdwin-aspects) project for such an aspect.
#### `AValueObject`
This type should be used as the base class for all beans. It provides reflective `toString()`, `hashCode()`, `equals()` and `compareTo()` per default using [commons-lang](https://commons.apache.org/proper/commons-lang) (with you being able to override if needed). Also it allows to copy equally named properties from other objects via a `mergeFrom(...)` method that utilizes the [commons-beanutils](http://commons.apache.org/proper/commons-beanutils) framework. And it provides a deep `clone()` method per default that is implemented via the [FST](https://github.com/RuedigerMoeller/fast-serialization) framework. Though it still offers a `shallowClone()` method for when this is needed. Also classes extending from this one will get [bean path constants classes](https://github.com/subes/invesdwin-norva#constants) generated for it and will get [QueryDSL](http://www.querydsl.com) query classes generated as long as the specific annotation processors are activated for this.
#### `DirtyTracker`
`AValueObjects` also have something called a `DirtyTracker`, which is a class that allows you to check if the values of a bean tree have been changed or not. It utilizes the `PropertyChangeSupport` to keep track of changes in the objects tree. Though please be sure to always implement `firePropertyChange(...)` properly in your setters or use an [aspect](https://github.com/subes/invesdwin-aspects#propertychangesupportedaspect) for this. Also make sure to only change values by calling the setters or else the `DirtyTracker` will not notice changes in the fields themselves. The `DirtyTracker` is very handy for UI development where the UI framework does not handle dirty state properly or not at all.
#### `Pair`, `Triple`, `Quadruple`
These can be useful as combined keys for caches (e.g. ALoadingCache) or when multiple return values are required for a method and you don't want to write another value object for this.

## Byte Buffers
#### `IByteBuffer`
Wrappers for various other byte buffer implementations. Adds some convenience to the Java `ByteBuffer`, [Agrona DirectBuffer](https://github.com/real-logic/agrona), [Netty ByteBuf](https://netty.io/wiki/using-as-a-generic-library.html#buffer-api) (optional dependency), and [Chronicle Bytes](https://github.com/OpenHFT/Chronicle-Bytes) (optional dependency). The class `ByteBuffers` provides `allocate` and `wrap` methods for the fastest implementations. Slices are reused per default for zero-allocation (use `newSlice` if it should not be reused).
#### `IMemoryBuffer`
When addressing memory larger than 31 bits with `int`, this interface allows you to address 63 bits using `long`. It is also possible to move the base pointer of an IByteBuffer around so that different segments can be addressed within an 63 bit address space as a 31 bit segment. This allows one to use `ISerde` from an `IMemoryBuffer` using `memoryBuffer.asByteBufferFrom(offset)`. This is especially helpful for memory mapped files and off heap memory larger than 2gb.
#### `ISerde` 
Serde stands for Serializer/Deserializer. They provide simplified and fast conversion from/to bytes for value objects. Preferably use the `fromBuffer`/`toBuffer` methods with sliced buffers instead of `fromBytes`/`toBytes` so that zero-copy/zero-allocation pipelines can be built. There are implementations available for most simple types. [Simple Binary Encoding](https://github.com/real-logic/simple-binary-encoding) can be used to generate code for complex types that can be wrapped by `ISerde` classes. An alternative for the flyweight pattern might be [Chronicle-Values](https://github.com/OpenHFT/Chronicle-Values) which does not require a separate contract declaration.

## Others
#### `Assertions`
The popular [AssertJ](http://joel-costigliola.github.io/assertj) fluent API extended by `FDate` and `Decimal`. Though sometimes it might be better to use if-throw statements instead of this API, since it might be a performance bottleneck in some cases. Where it is not, it is a very good ease in doing defensive coding. Best approach is to use it as a default and replace it by manual code where the profiler tells that it is too slow (should not be too many cases anyway).
#### `AComparator`
Ever wondered if your comparator will result in ascending or descending order? This class will make the desired order easier to get by making that an explicit decision during sort calls. You also only have to give `ACriteriaComparator` the property to compare and it will handle casting, null checks and other things for you.
#### `Strings`, `Reflections`, `Objects` ...
Each one being a one-stop class to find the utility method you are searching for by providing a [static facade](https://github.com/subes/invesdwin-norva#static-facade) to the most useful frameworks and providing its own set of operations which are missing from the ones that already exist.
#### `ExpressionParser`
This is a major rewrite of the popular and fast [parsii](https://github.com/scireum/parsii) expression library. There are a lot of performance optimizations included (using final and immutable where possible, faster tokenizer and removing unneeded features). Also support was added for time series based expressions with `[x]` operator on functions and variables for looking up previous values and for operators like `crosses above` and `crosses below`. Functions can be referenced as variables and vice versa where possible. Thus the parentheses operator `()` becomes optional. Boolean expressions are processed efficiently by skipping unnecessary expression evaluations. You can also use `double and boolean` as results for evaluations as well as `none, int and time` based historical indexing for evaluation by calling the appropriate expression method. The time series based functions and variables can be added by overriding the `getFunction(name)`, `getVariable(name)` and `getPreviousKeyFunction()` methods of the parser class. Though without these, the classical math and boolean expressions still work properly. It is possible to extend the expressions by technical analysis features using this functionality. The expressions are case insensitive with variables and functions automatically being converted to lowercase before being parsed. Read more about the language design and performance results [here](https://www.researchgate.net/publication/359659846_A_Simple_Expression_Language_for_Automating_Strategy_Development_Processes). This new expression language can be called "InvEL" for "Invesdwin Expression Language". [Here](https://www.youtube.com/watch?v=Ilw8J_bfgwA&list=FLebnPcJPaUWYjEuJj6z7tSw) is a recorded presentation of a practical application of the expression language (please excuse the bad microphone quality). Slides are available as a [long](https://github.com/invesdwin/invesdwin-util/blob/master/invesdwin-util-parent/invesdwin-util/doc/DBA_Presentation_long.pdf) and [short](https://github.com/invesdwin/invesdwin-util/blob/master/invesdwin-util-parent/invesdwin-util/doc/DBA_Presentation_short.pdf) version.
#### `PseudoRandomGenerators`
This provides pools, threadlocals, adapters and wrappers for Random implementations. Consider using [CryptoRandomGenerators or StrongRandomGenerators](https://github.com/invesdwin/invesdwin-context-security/tree/master#crypto-module) where security is a concern. We use [XoShiro256+](https://prng.di.unimi.it/#speed) as a fast algorithm with high quality.
Here some benchmarks (2022, Core i9-9900K with SSD, Java 17):
```
Reuse instance:
JdkThreadLocalRandom							Records:	6,167,082.80/ms	=>	56,19 times faster (does not pass randomness tests)
*PseudoRandomGenerator (XoShiro256+)					Records:	1,493,363.24/ms	=>	12,85 times faster
Xoroshiro								Records:	1,276,492.19/ms	=>	10,84 times faster
ThreadLocalRandom							Records:	1,185,453.99/ms	=>	9,99 times faster
L32X64MixRandom								Records:	  167,951.51/ms	=>	55,76% faster
PseudoRandomGeneratorObjectPool(OneToOne)				Records:	  161,984.73/ms	=>	50,23% faster
Xoshiro256PlusPlus							Records:	  149,690.42/ms	=>	38,83% faster
SplittableRandom							Records:	  147,002.13/ms	=>	36,33% faster
Xoroshiro128PlusPlus							Records:	  145,790.15/ms	=>	35,21% faster
L64X128StarStarRandom							Records:	  140,477.61/ms	=>	30,28% faster
L64X128MixRandom							Records:	  139,236.71/ms	=>	29,13% faster
L128X128MixRandom							Records:	  135,185.45/ms	=>	25,37% faster
L64X1024MixRandom							Records:	  130,005.04/ms	=>	20,57% faster
L64X256MixRandom							Records:	  124,849.60/ms	=>	15,79% faster
jdkDefaultRandomGenerator(L32X64MixRandom)				Records:	  107,826.18/ms	=>	Baseline	
PseudoRandomGeneratorObjectPool(OneToMany)				Records:	  103,167.75/ms	=>	4,32% slower
L128X1024MixRandom							Records:	  92,593.38/ms	=>	14,13% slower
L128X256MixRandom							Records:	  78,287.52/ms	=>	27,39% slower
PseudoRandomGeneratorObjectPool(ManyToMany)				Records:	  69,571.56/ms	=>	35,48% slower
java.util.Random							Records:	  68,135.00/ms	=>	36,81% slower
PseudoRandomGeneratorObjectPool(BufferingIterator)			Records:	  58,700.37/ms	=>	45,56% slower
PseudoRandomGeneratorObjectPool(SynchronizedBufferingIterator)		Records:	  25,245.95/ms	=>	76,59% slower
SecureRandom								Records:	   2,439.59/ms	=>	97,74% slower

Don't reuse instance:
ThreadLocalRandom							Records:	229,960.77/ms	=>	5,51 times faster (does not pass randomness tests)
PseudoRandomGeneratorObjectPool(OneToOne)				Records:	161,984.73/ms	=>	3,58 times faster
JdkThreadLocalRandom							Records:	139,492.58/ms	=>	2,95 times faster
PseudoRandomGeneratorObjectPool(OneToMany)				Records:	103,167.75/ms	=>	1,92 times faster
PseudoRandomGeneratorObjectPool(ManyToMany)				Records:	 69,571.56/ms	=>	96,92% faster
PseudoRandomGeneratorObjectPool(BufferingIterator)			Records:	 58,700.37/ms	=>	66,15% faster
L32X64MixRandom								Records:	 46,001.90/ms	=>	30,21% faster
Xoroshiro128PlusPlus							Records:	 45,708.95/ms	=>	29,38% faster
*PseudoRandomGenerator (XoShiro256+)					Records:	 43,115.07/ms	=>	22,04% faster
L64X128MixRandom							Records:	 42,780.08/ms	=>	21,09% faster
L64X128StarStarRandom							Records:	 40,969.41/ms	=>	15,96% faster
L128X128MixRandom							Records:	 40,543.98/ms	=>	14,76% faster
SplittableRandom							Records:	 40,400.40/ms	=>	14,35% faster
Xoshiro256PlusPlus							Records:	 38,563.43/ms	=>	9,15% faster
Xoroshiro								Records:	 38,285.23/ms	=>	8,37% faster
jdkDefaultRandomGenerator(L32X64MixRandom)				Records:	 35,329.86/ms	=>	Baseline	
L64X256MixRandom							Records:	 34,403.33/ms	=>	2,62% slower
L128X256MixRandom							Records:	 30,494.92/ms	=>	13,69% slower
PseudoRandomGeneratorObjectPool(SynchronizedBufferingIterator)		Records:	 25,245.95/ms	=>	28,54% slower
java.util.Random							Records:	 23,913.61/ms	=>	32,31% slower
L64X1024MixRandom							Records:	 21,618.42/ms	=>	38,81% slower
L128X1024MixRandom							Records:	 19,785.29/ms	=>	44,00% slower
```

## Support

If you need further assistance or have some ideas for improvements and don't want to create an issue here on github, feel free to start a discussion in our [invesdwin-platform](https://groups.google.com/forum/#!forum/invesdwin-platform) mailing list.
