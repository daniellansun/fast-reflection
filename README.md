# fast-reflection
Yet another fast alternative for Java reflection(WIP), making dynamic calls as efficient as direct calls.

## Usage
See the tests for now:
* [tests for `FastMethod`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastMethodTest.java)
* [tests for `FastConstructor`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastConstructorTest.java)
* [tests for `FastClass`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastClassTest.java)

## Performance Test
### Run `gradlew jmh` to get the performance test result

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
Benchmark                                                                      Mode  Cnt   Score   Error  Units
FastMethodPerfTest.constructor_constant_handle_StringCtorCharArray             avgt   15  12.329 ± 0.181  ns/op
FastMethodPerfTest.constructor_constant_lambdametafactory_StringCtorCharArray  avgt   15  12.304 ± 0.134  ns/op
FastMethodPerfTest.constructor_direct_StringCtorCharArray                      avgt   15  12.390 ± 0.211  ns/op
FastMethodPerfTest.constructor_fastreflect_StringCtorCharArray                 avgt   15  14.696 ± 0.263  ns/op
FastMethodPerfTest.constructor_instance_handle_StringCtorCharArray             avgt   15  17.857 ± 0.432  ns/op
FastMethodPerfTest.constructor_instance_lambdametafactory_StringCtorCharArray  avgt   15  13.671 ± 0.188  ns/op
FastMethodPerfTest.constructor_reflect_StringCtorCharArray                     avgt   15  18.692 ± 0.238  ns/op
FastMethodPerfTest.constructor_reflect_accessible_StringCtorCharArray          avgt   15  16.860 ± 1.800  ns/op
FastMethodPerfTest.method_constant_handle_StringStartsWith                     avgt   15   3.461 ± 0.027  ns/op
FastMethodPerfTest.method_constant_lambdametafactory_StringStartsWith          avgt   15   3.745 ± 0.280  ns/op
FastMethodPerfTest.method_direct_StringStartsWith                              avgt   15   3.307 ± 0.162  ns/op
FastMethodPerfTest.method_fastreflect_StringStartsWith                         avgt   15   3.460 ± 0.043  ns/op
FastMethodPerfTest.method_instance_handle_StringStartsWith                     avgt   15  11.531 ± 0.329  ns/op
FastMethodPerfTest.method_instance_lambdametafactory_StringStartsWith          avgt   15   3.967 ± 0.092  ns/op
FastMethodPerfTest.method_reflect_StringStartsWith                             avgt   15  11.701 ± 0.481  ns/op
FastMethodPerfTest.method_reflect_accessible_StringStartsWith                  avgt   15  10.722 ± 0.695  ns/op
```

### Summary
* fast-reflection runs almost as fast as direct call
* fast-reflection runs almost as fast as constant method handle
* fast-reflection runs much faster than normal reflection
* instance method handle runs almost as slow as normal reflection

## FAQ
* **Q:** How fast can fast-reflection run?
* **A:** fast-reflection runs almost as fast as direct calls, and much faster than normal reflection(see the above performance test result).

* **Q:** Method handles are available in JDK since Java 7, and they don't require any additional dependency. I'm not sure why custom libraries could be preferred over method handles.
* **A:** Only constant method handles can gain the best performance, but reflection runs on the fly, so it's impossible for us to hard-code all constant method handles we may use. The fast-reflection will generate constants method handles for dynamic calls to gain the best performance, this is the reason why we need the custom library. BTW, instance method handles are much flexible but can not help us gain better performance because they run almost as slow as normal reflection.

* **Q:** What dependencies does fast-reflection require?
* **A:** Just the ASM.
