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
* VM options: -Xms2g -Xmx2g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC
* Blackhole mode: full + dont-inline hint (default, use -Djmh.blackhole.autoDetect=true to auto-detect)
* Warmup: 3 iterations, 1 s each
* Measurement: 5 iterations, 1 s each
* Timeout: 10 min per iteration
* Threads: 1 thread, will synchronize iterations
* Benchmark mode: Average time, time/op

```java
Benchmark                                                              Mode  Cnt   Score   Error  Units
FastMethodPerfTest.constructor_direct_StringCtorCharArray              avgt   15  13.959 ± 0.194  ns/op
FastMethodPerfTest.constructor_fastreflect_StringCtorCharArray         avgt   15  13.766 ± 0.294  ns/op
FastMethodPerfTest.constructor_reflect_StringCtorCharArray             avgt   15  22.429 ± 0.599  ns/op
FastMethodPerfTest.constructor_reflect_accessible_StringCtorCharArray  avgt   15  18.724 ± 2.333  ns/op
FastMethodPerfTest.method_direct_StringStartsWith                      avgt   15   3.326 ± 0.262  ns/op
FastMethodPerfTest.method_fastreflect_StringStartsWith                 avgt   15   4.323 ± 0.300  ns/op
FastMethodPerfTest.method_reflect_StringStartsWith                     avgt   15  14.117 ± 0.626  ns/op
FastMethodPerfTest.method_reflect_accessible_StringStartsWith          avgt   15  11.371 ± 0.681  ns/op
```

**Note:**
* fast-reflection runs almost as fast as direct call
* much faster than normal reflection

## FAQ
* **Q:** Method handles are available in JDK since Java 7, and they don't require any additional dependency. I'm not sure why custom libraries could be preferred over method handles.
* **A:** Only constant method handles can gain the best performance, but reflection runs on the fly, so it's impossible for us to hard-code all constant method handles we may use. The fast-reflection will generate constants method handles for dynamic calls to gain the best performance, this is the reason why we need the custom library.

