# fast-reflection
Yet another fast alternative for Java reflection(WIP), making dynamic calls as efficient as direct calls.

## Usage
See the tests for now:
* [tests for `FastMethod`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastMethodTest.java)
* [tests for `FastConstructor`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastConstructorTest.java)
* [tests for `FastClass`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastClassTest.java)

## Performance Test
Run `gradlew jmh` to get the performance test result.

* JMH version: 1.33
* VM version: JDK 11.0.9, OpenJDK 64-Bit Server VM, 11.0.9+11-LTS
* VM options: -Xms2g -Xms2g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC
* Blackhole mode: full + dont-inline hint (default, use -Djmh.blackhole.autoDetect=true to auto-detect)
* Warmup: 3 iterations, 1 s each
* Measurement: 5 iterations, 1 s each
* Timeout: 10 min per iteration
* Threads: 1 thread, will synchronize iterations
* Benchmark mode: Average time, time/op

```java
Benchmark                                                       Mode  Cnt   Score   Error  Units
FastMethodPerfTest.constructor_direct_StringCtorCharArray       avgt   15  12.222 ± 0.542  ns/op
FastMethodPerfTest.constructor_fastreflect_StringCtorCharArray  avgt   15  11.529 ± 0.615  ns/op
FastMethodPerfTest.constructor_reflect_StringCtorCharArray      avgt   15  19.444 ± 1.088  ns/op
FastMethodPerfTest.method_direct_StringStartsWith               avgt   15   2.752 ± 0.281  ns/op
FastMethodPerfTest.method_fastreflect_StringStartsWith          avgt   15   3.634 ± 0.394  ns/op
FastMethodPerfTest.method_reflect_StringStartsWith              avgt   15  12.384 ± 1.310  ns/op
```

**Note:**
* fast-reflection runs almost as fast as direct call
* much faster than normal reflection
