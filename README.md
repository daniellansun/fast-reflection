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
FastMethodPerfTest.constructor_constant_handle_StringCtorCharArray     avgt   15  10.830 ± 0.057  ns/op
FastMethodPerfTest.constructor_direct_StringCtorCharArray              avgt   15  10.803 ± 0.037  ns/op
FastMethodPerfTest.constructor_fastreflect_StringCtorCharArray         avgt   15  12.915 ± 0.022  ns/op
FastMethodPerfTest.constructor_instance_handle_StringCtorCharArray     avgt   15  15.555 ± 0.057  ns/op
FastMethodPerfTest.constructor_reflect_StringCtorCharArray             avgt   15  16.494 ± 0.050  ns/op
FastMethodPerfTest.constructor_reflect_accessible_StringCtorCharArray  avgt   15  14.677 ± 1.627  ns/op
FastMethodPerfTest.method_constant_handle_StringStartsWith             avgt   15   3.069 ± 0.212  ns/op
FastMethodPerfTest.method_direct_StringStartsWith                      avgt   15   2.716 ± 0.037  ns/op
FastMethodPerfTest.method_fastreflect_StringStartsWith                 avgt   15   3.204 ± 0.212  ns/op
FastMethodPerfTest.method_instance_handle_StringStartsWith             avgt   15   9.785 ± 0.123  ns/op
FastMethodPerfTest.method_reflect_StringStartsWith                     avgt   15   9.923 ± 0.450  ns/op
FastMethodPerfTest.method_reflect_accessible_StringStartsWith          avgt   15   8.921 ± 0.430  ns/op
```

**Summary**
* fast-reflection runs almost as fast as direct call
* fast-reflection runs much faster than normal reflection
* constant method handle runs almost as fast as direct call
* constant method handle runs much faster than normal reflection
* instance method handle runs almost as slow as normal reflection

## FAQ
* **Q:** Method handles are available in JDK since Java 7, and they don't require any additional dependency. I'm not sure why custom libraries could be preferred over method handles.
* **A:** Only constant method handles can gain the best performance, but reflection runs on the fly, so it's impossible for us to hard-code all constant method handles we may use. The fast-reflection will generate constants method handles for dynamic calls to gain the best performance, this is the reason why we need the custom library. BTW, instance method handles are much flexible but can not help us gain better performance because they run almost as slow as normal reflection.

