# spring-influxdb-orm

[![Build Status](https://travis-ci.org/y1j2x34/spring-influxdb-orm.svg?branch=master)](https://travis-ci.org/y1j2x34/spring-influxdb-orm)
[![codecov.io](https://codecov.io/github/y1j2x34/spring-influxdb-orm/coverage.svg?branch=master)](https://codecov.io/github/y1j2x34/spring-influxdb-orm)

## Usages

### Basic Usages

ApplicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:influxdb="http://www.vgerbot.com/schema/influxdb"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.vgerbot.com/schema/influxdb http://www.vgerbot.com/schema/influxdb/influxdb.xsd">

    <influxdb:mapper entity-base-package="com.yourcompany.influxdb.entity"
        dao-base-package="com.yourcompany.influxdb.dao">
        <influxdb:datasource database="test" host="127.0.0.1"
            port="8086" scheme="http" username="admin" password="admin"/>
    </influxdb:mapper>
</beans>
```

CensusMeasurement.java

```java
package com.yourcompany.influxdb.entity;

@InfluxDBMeasurement('census')
public class CensusMeasurement  implements Serializable {
    private static final long serialVersionUID = 8260424450884444916L;

    private Date time;

    @TagColumn("location")
    private String location;
    @TagColumn("scientist")
    private String scientist;

    @FieldColumn("butterflies")
    private Integer butterflies;
    @FieldColumn("honeybees")
    private Integer honeybees;

    // getters and setters
    // hashCode and equals

    public String toString(){
        return "[time: " + time.getTime() + ", location: " + location
                + ", scientist: " + scientist + ", butterflies: " + butterflies + ", honeybees: " + honeybees + "]";
    }
}
```

CensusDao.java

```java
package com.yourcompany.influxdb.dao;

public interface CensusDao extends InfluxDBDao<CensusMeasurement> {

    @InfluxDBSelect("select * from census where scientist = #{scientist}")
    public List<CensusMeasurement> selectByScientist(@InfluxDBParam("scientist") String scientist);

    @InfluxDBExecute("DROP SERIES FROM census where scientist=#{scientist} and location=#{location}")
    public void dropSeries( //
            @InfluxDBParam("scientist") String scientist, //
            @InfluxDBParam("location") Integer location //
    );
    @InfluxDBSelect("select scientist,location,butterflies from census where scientist = #{scientist}")
    public List<Map<String, Integer>> selectButterflies( //
            @InfluxDBParam("scientist") String scientist, //
            @InfluxDBParam("location") Integer location //
    );

    public void hello();
}
```

Main.java

```java
package com.yourcompany.influxdb;
public class Main {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml")) {
            context.refresh();
            CensusDao dao = context.getBean(CensusDao.class);
            List<CensusMeasurement> measurements = dao.selectByScientist("lanstroth");
            System.out.println(measurements);

            dao.dropSeries("lastroth", 12);

            List<Map<String, Integer>> result = dao.selectButterflies("lanstroth", 10);
            System.out.println(result);
        }
    }
}
```

## Advanced Usages

### Sharding

```java
// TODO
```

### Specify executor

```java
// CensusDao.java
public interface CensusDao{
    @SpecifiedExecutor(HelloWorldExecutor.class)
    public void execute(String scientist, Integer location);
}
// HelloWorldExecutor.java
public class HelloWorldExecutor extends Executor {
    public HelloWorldExecutor(InfluxDBRepository repository) {
        super(repository);
    }

    @Override
    public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
        System.out.println("hello world");
        return ResultContext.VOID;
    }
}

```

### Custom annotation

Select.java

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AnnotateExecutor(SelectExecutor.class)
public @interface Select{
    String value();
}
```

SelectExecutor.java

```java
public class SelectExecutor extends AnnotationExecutor<Select> {
    public SelectExecutor(InfluxDBRepository repository, Select selectAnnotation) {
        super(repository, selectAnnotation);
    }

    @Override
    public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
        String command = super.annotation.value();
        String parsedCommand = CommandUtils.parseCommand(command, parameters);
        return repository.query(parsedCommand);
    }
}
```

### Gzip support

https://github.com/influxdata/influxdb-java#gzips-support-version-25-required


## License

This project is released under MIT License