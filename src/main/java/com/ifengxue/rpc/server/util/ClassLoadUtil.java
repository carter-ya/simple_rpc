package com.ifengxue.rpc.server.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类加载工具
 * Created by LiuKeFeng on 2017-05-01.
 */
public class ClassLoadUtil {
    private static final FileSystemClassLoader FILE_SYSTEM_CLASS_LOADER = new FileSystemClassLoader(new URL[] {});

    /**
     * 从指定的jar目录下加载jar包
     * @param jarPaths
     * @return
     */
    public static synchronized List<Class<?>> loadClassFromJarPaths(String...jarPaths) {
        List<String> jarFiles = new ArrayList<>();
        for (String jarPath : jarPaths) {
            File file = new File(jarPath);
            Arrays.stream(file.listFiles(f -> f.getName().endsWith(".jar")))
                    .forEach(f -> jarFiles.add(f.getPath()));
        }
        jarFiles.forEach(FILE_SYSTEM_CLASS_LOADER::appendJarPath);

        List<Class<?>> loadedClassList = new ArrayList<>();
        for (String jarPath : jarFiles) {
            try (JarFile jarFile = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String filename = jarEntry.getName();
                    if (filename.startsWith("META-INF/") || !filename.endsWith(".class")) {
                        continue;
                    }
                    String classname = filename.replace('/', '.').replace(".class", "");
                    loadedClassList.add(FILE_SYSTEM_CLASS_LOADER.loadClass(classname));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                //do nothing
            }
        }
        return loadedClassList;
    }

    /**
     *  从指定的classpath下加载Class
     * @param classpath
     * @return
     */
    public static synchronized List<Class<?>> loadClassFromClasspath(String classpath) {
        FILE_SYSTEM_CLASS_LOADER.appendClasspath(classpath);
        List<String> classnameList = findAllClassnameFromClasspath(classpath, new ArrayList<>());
        List<Class<?>> loadedClassList = new ArrayList<>(classnameList.size());
        for (String classname : classnameList) {
            try {
                loadedClassList.add(FILE_SYSTEM_CLASS_LOADER.loadClass(classname));
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                //do nothing
            }
        }
        return loadedClassList;
    }

    /**
     * 从指定路径下寻找所有classname
     * @param classpath 类路径
     * @param classnameList 存放classname的集合
     * @return
     */
    private static List<String> findAllClassnameFromClasspath(String classpath, List<String> classnameList) {
        //适应本地文件系统路径分隔符
        String classesAndSeparator = "classes" + File.separator;
        for (File file : new File(classpath).listFiles()) {
            if (file.isFile()) {
                if (file.getName().endsWith(".class")) {
                    String classname = file.getPath().replace(".class", "");
                    int classesIndex = classname.indexOf(classesAndSeparator);
                    classname = classname.substring(classesIndex + classesAndSeparator.length()).replace(File.separatorChar, '.');
                    classnameList.add(classname);
                }
            } else {
                findAllClassnameFromClasspath(file.getPath(), classnameList);
            }
        }
        return classnameList;
    }

    /**
     * 从文件系统加载Class
     */
    private static class FileSystemClassLoader extends URLClassLoader {
        public FileSystemClassLoader(URL[] urls) {
            super(urls);
        }

        public void appendJarPath(String jarPath) {
            try {
                super.addURL(new File(jarPath).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }
        }

        public void appendClasspath(String classpath) {
            appendJarPath(classpath);
        }
    }
}
