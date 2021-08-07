package com.huyiyu.agent;

import com.huyiyu.Replace;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;

public class PreMainReplaceClassAgent {

    private static final ConcurrentHashMap<String, MetadataReader> map = new ConcurrentHashMap<>();

    static {
        // 使用componentScan 辅助扫描
        ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(new DefaultListableBeanFactory(), false, new StandardEnvironment());
        // 自定义扫描规则并缓存扫描结果
        classPathBeanDefinitionScanner.addIncludeFilter((metadataReader, metadataReaderFactory1) -> {
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            String replace = annotationMetadata.getClassName().replace(".", "/");
            boolean replaceFlag = annotationMetadata.hasAnnotation(Replace.class.getName());
            if (replaceFlag) {
                map.put(replace, metadataReader);
            }
            return replaceFlag;
        });
        // 开始扫描固定包下的内容 包名可根据需求修改
        classPathBeanDefinitionScanner.findCandidateComponents("com.huyiyu.worktest");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(PreMainReplaceClassAgent::transform, true);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(PreMainReplaceClassAgent::transform, true);

    }

    private static byte[] transform(ClassLoader classLoader, String className, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) {

        if (className != null && map.containsKey(className)) {
            MetadataReader metadataReader = map.get(className);
            try (InputStream in = metadataReader.getResource().getInputStream()) {
                byte[] bytes1 = new byte[in.available()];
                int read = in.read(bytes1);
                return bytes1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
