Introduction
============

This repo is created as repro for [hazelcast/hazelcast#6339](https://github.com/hazelcast/hazelcast/issues/6339) / [spring-projects/spring-boot#7418](https://github.com/spring-projects/spring-boot/issues/7418). It is a very basic Spring Boot ``CommandLineRunner`` that only prints "Hello World". After that, the process should end, but it fails to do so because the daemon threads of Hazelcast keep it up and running. An explicit ``Hazelcast.shutdownAll()`` is required to make the process end after completing the commandline runner. This is annoying for a command line runner, but also for a service process. If the service fails to start, you would like it to end, but that requires adding a try/catch around ``SpringApplication.run(YourApplication.class, args)`` with a Hazelcast shutdown in the catch. Doing that inside a ``finally`` instead of a ``catch`` would cause Hazelcast to shutdown while the appliation continues to run, a pitfall for beginners.

How to reproduce
================

Run ``gradlew bootRun``. The process will continue running till you interrupt it.
These are the last output lines:

```
016-11-18 08:05:01.404  INFO 7668 --- [           main] com.hazelcast.core.LifecycleService      : [10.0.75.1]:5701 [dev] [3.5.5] Address[10.0.75.1]:5701 is STARTED
2016-11-18 08:05:01.604  INFO 7668 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
Hello World!
2016-11-18 08:05:01.619  INFO 7668 --- [           main] n.f.r.hazelcast.shutdown.TheApplication  : Started TheApplication in 5.005 seconds (JVM running for 5.37)
> Building 80% > :bootRun
```

If you uncomment line 29 in ``TheApplication.java`` and run it again, it nicely stops after completing the runner.
These are the last output lines:

```
2016-11-18 08:07:04.493  INFO 6000 --- [           main] com.hazelcast.core.LifecycleService      : [10.0.75.1]:5701 [dev] [3.5.5] Address[10.0.75.1]:5701 is STARTED
2016-11-18 08:07:04.697  INFO 6000 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
Hello World!
2016-11-18 08:07:04.709  INFO 6000 --- [           main] n.f.r.hazelcast.shutdown.TheApplication  : Started TheApplication in 5.237 seconds (JVM running for 5.517)
2016-11-18 08:07:04.710  INFO 6000 --- [           main] com.hazelcast.core.LifecycleService      : [10.0.75.1]:5701 [dev] [3.5.5] Address[10.0.75.1]:5701 is SHUTTING_DOWN
2016-11-18 08:07:04.711  INFO 6000 --- [           main] com.hazelcast.instance.Node              : [10.0.75.1]:5701 [dev] [3.5.5] Shutting down multicast service...
2016-11-18 08:07:04.712  INFO 6000 --- [           main] com.hazelcast.instance.Node              : [10.0.75.1]:5701 [dev] [3.5.5] Shutting down connection manager...
2016-11-18 08:07:04.713  INFO 6000 --- [           main] com.hazelcast.instance.Node              : [10.0.75.1]:5701 [dev] [3.5.5] Shutting down node engine...
2016-11-18 08:07:04.716  INFO 6000 --- [           main] com.hazelcast.instance.NodeExtension     : [10.0.75.1]:5701 [dev] [3.5.5] Destroying node NodeExtension.
2016-11-18 08:07:04.716  INFO 6000 --- [           main] com.hazelcast.instance.Node              : [10.0.75.1]:5701 [dev] [3.5.5] Hazelcast Shutdown is completed in 5 ms.
2016-11-18 08:07:04.717  INFO 6000 --- [           main] com.hazelcast.core.LifecycleService      : [10.0.75.1]:5701 [dev] [3.5.5] Address[10.0.75.1]:5701 is SHUTDOWN
2016-11-18 08:07:04.718  INFO 6000 --- [       Thread-2] s.c.a.AnnotationConfigApplicationContext : Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@16f7c8c1: startup date [Fri Nov 18 08:06:59 CET 2016]; root of context hierarchy
2016-11-18 08:07:04.720  INFO 6000 --- [       Thread-2] o.s.j.e.a.AnnotationMBeanExporter        : Unregistering JMX-exposed beans on shutdown
2016-11-18 08:07:04.721  INFO 6000 --- [       Thread-2] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2016-11-18 08:07:04.721  INFO 6000 --- [       Thread-2] org.hibernate.tool.hbm2ddl.SchemaExport  : HHH000227: Running hbm2ddl schema export
2016-11-18 08:07:04.722  INFO 6000 --- [       Thread-2] org.hibernate.tool.hbm2ddl.SchemaExport  : HHH000230: Schema export complete

BUILD SUCCESSFUL

Total time: 14.534 secs
```
