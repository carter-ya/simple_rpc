package com.ifengxue.rpc.server.util;

import org.junit.Test;

import java.util.List;

/**
 * Created by LiuKeFeng on 2017-05-01.
 */
public class ClassLoadUtilTest {
    @Test
    public void testLoadClassFromJarPaths() {
        String[] jarPaths = {"D:/work/repository/dom4j/dom4j/1.6.1", "D:/work/repository/commons-lang/commons-lang/2.6"};
        List<Class<?>> classList = ClassLoadUtil.loadClassFromJarPaths(jarPaths);
        classList.forEach(System.out::println);
    }

    @Test
    public void testLoadClassFromClasspath() {
        String classpath = "D:/java_workspace/eclipse workspace/rpc/target/classes";
        List<Class<?>> classList = ClassLoadUtil.loadClassFromClasspath(classpath);
        classList.forEach(System.out::println);
    }
}
