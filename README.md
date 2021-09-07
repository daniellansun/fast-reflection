# fast-reflection
Yet another fast alternative for Java reflection, making dynamic calls as efficient as direct calls.

## Usage
See the tests:
* [tests for `FastMethod`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastMethodTest.java)
* [tests for `FastConstructor`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastConstructorTest.java)
* [tests for `FastClass`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastClassTest.java)

## Performance Test
Run `gradlew jmh` to get the performance test result.

* JMH version: 1.33
* VM version: JDK 11.0.6, OpenJDK 64-Bit Server VM, 11.0.6+10-LTS
* VM options: -Xms2g -Xms2g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC
* Blackhole mode: full + dont-inline hint (default, use -Djmh.blackhole.autoDetect=true to auto-detect)
* Warmup: 3 iterations, 1 s each
* Measurement: 5 iterations, 1 s each
* Timeout: 10 min per iteration
* Threads: 1 thread, will synchronize iterations
* Benchmark mode: Average time, time/op

```java
Benchmark                                                       Mode  Cnt   Score    Error  Units
FastMethodPerfTest.constructor_direct_StringCtorCharArray       avgt   15   5.854 ±  0.064  ns/op
FastMethodPerfTest.constructor_fastreflect_StringCtorCharArray  avgt   15   5.259 ±  0.228  ns/op
FastMethodPerfTest.constructor_reflect_StringCtorCharArray      avgt   15  14.208 ±  0.586  ns/op
FastMethodPerfTest.method_direct_StringStartsWith               avgt   15   0.493 ±  0.078  ns/op
FastMethodPerfTest.method_fastreflect_StringStartsWith          avgt   15   0.443 ±  0.001  ns/op
FastMethodPerfTest.method_reflect_StringStartsWith              avgt   15   8.525 ±  1.171  ns/op
```
(**Note:** fast-reflection runs almost as fast as direct call and much faster than normal reflection)
