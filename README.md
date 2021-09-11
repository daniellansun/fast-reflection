# fast-reflection
Yet another extremely fast alternative for Java reflection, making dynamic calls as efficient as direct calls.

## Usage
Use fast-reflection as enhanced Java reflection, see the tests for now:
* [`FastMethod`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastMethodTest.java)
* [`FastConstructor`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastConstructorTest.java)
* [`FastField`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastFieldTest.java)
* [`FastClass`](https://github.com/danielsun1106/fast-reflection/blob/main/src/test/java/me/sunlan/fastreflection/FastClassTest.java)

## Benchmark Test
### Run `gradlew jmh` to get the benchmark test report

* JMH version: 1.33
* VM version: JDK 11.0.9, OpenJDK 64-Bit Server VM, 11.0.9+11-LTS
* VM options: -Xms2g -Xmx2g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC
* Blackhole mode: full + dont-inline hint (default, use -Djmh.blackhole.autoDetect=true to auto-detect)
* Warmup: 3 iterations, 1 s each
* Measurement: 5 iterations, 1 s each
* Timeout: 10 min per iteration
* Threads: 1 thread, will synchronize iterations
* Benchmark mode: Average time, time/op

### Summary
* fast-reflection runs almost as fast as direct call
* fast-reflection runs almost as fast as constant method handle
* fast-reflection runs almost as fast as lambda meta factory
* fast-reflection runs much faster than normal reflection
* instance method handle runs almost as slow as normal reflection

### Benchmark Test Report
```java
Benchmark                                                                      Mode  Cnt   Score   Error  Units
FastMethodPerfTest.constructor_constant_handle_StringCtorCharArray             avgt   15  12.140 ± 0.064  ns/op
FastMethodPerfTest.constructor_constant_lambdametafactory_StringCtorCharArray  avgt   15  12.137 ± 0.052  ns/op
FastMethodPerfTest.constructor_direct_StringCtorCharArray                      avgt   15  12.066 ± 0.031  ns/op
FastMethodPerfTest.constructor_fastreflect_StringCtorCharArray                 avgt   15  14.169 ± 0.053  ns/op
FastMethodPerfTest.constructor_instance_handle_StringCtorCharArray             avgt   15  16.098 ± 0.145  ns/op
FastMethodPerfTest.constructor_instance_lambdametafactory_StringCtorCharArray  avgt   15  12.263 ± 0.428  ns/op
FastMethodPerfTest.constructor_reflect_StringCtorCharArray                     avgt   15  17.296 ± 0.029  ns/op
FastMethodPerfTest.constructor_reflect_accessible_StringCtorCharArray          avgt   15  16.646 ± 0.104  ns/op
FastMethodPerfTest.field_constant_handle_IntegerSize                           avgt   15   2.574 ± 0.165  ns/op
FastMethodPerfTest.field_direct_IntegerSize                                    avgt   15   2.692 ± 0.037  ns/op
FastMethodPerfTest.field_fastreflect_IntegerSize                               avgt   15   3.365 ± 0.034  ns/op
FastMethodPerfTest.field_instance_handle_IntegerSize                           avgt   15   6.477 ± 0.141  ns/op
FastMethodPerfTest.field_reflect_IntegerSize                                   avgt   15   5.801 ± 0.378  ns/op
FastMethodPerfTest.field_reflect_accessible_IntegerSize                        avgt   15   5.026 ± 0.006  ns/op
FastMethodPerfTest.method_constant_handle_StringStartsWith                     avgt   15   2.584 ± 0.173  ns/op
FastMethodPerfTest.method_constant_lambdametafactory_StringStartsWith          avgt   15   2.975 ± 0.117  ns/op
FastMethodPerfTest.method_direct_StringStartsWith                              avgt   15   2.873 ± 0.150  ns/op
FastMethodPerfTest.method_fastreflect_StringStartsWith                         avgt   15   3.206 ± 0.212  ns/op
FastMethodPerfTest.method_instance_handle_StringStartsWith                     avgt   15   8.924 ± 0.312  ns/op
FastMethodPerfTest.method_instance_lambdametafactory_StringStartsWith          avgt   15   3.539 ± 0.235  ns/op
FastMethodPerfTest.method_reflect_StringStartsWith                             avgt   15  10.681 ± 0.626  ns/op
FastMethodPerfTest.method_reflect_accessible_StringStartsWith                  avgt   15   9.322 ± 0.341  ns/op
```

## FAQ
### How fast can fast-reflection run?
fast-reflection runs almost as fast as direct calls, and much faster than normal reflection(see the above benchmark test report).

### Method handles are available since Java 7, why to use fast-reflection?
Only constant method handles can gain the best performance, but reflection runs on the fly, so it's impossible for us to pre-define all constant method handles we may use. The fast-reflection will generate constants method handles for dynamic calls to gain the best performance, this is the reason why we need the custom library. BTW, instance method handles are much flexible but can not help us gain better performance because they run almost as slow as normal reflection.

### How to use fast-reflection?
fast-reflection provides similar API of Java reflection for dynamic invocation cases, so use fast-reflection as enhanced Java reflection.

### What dependencies does fast-reflection require?
Just the ASM.
