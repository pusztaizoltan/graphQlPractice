package org.example;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    List<Integer> list = new ArrayList<>(List.of(1, 2, 3)) {
    };
    Long num = 1L;

    public static void main(String[] args) {
        Method method = Arrays.stream(Main.class.getMethods()).filter(method1 -> method1.getName().equals("returner")).findFirst().get();
        System.out.println(method);
        System.out.println(method.getGenericReturnType().getClass());
        var gg = method.getGenericReturnType();
        var aa = method.getAnnotatedReturnType();
        var bba = ((ParameterizedType) gg);
        System.out.println(bba.getOwnerType());
        System.out.println(bba.getRawType());
        System.out.println(bba);
//        System.out.println(method.getAnnotatedReturnType());
        System.out.println("-------------");
//        Arrays.stream(Main.class..getDeclaredClasses()).forEach(System.out::println);
        //        ArrayList<Integer> aa = new ArrayList<>(List.of(1,2,3)){};
//        Class<?>  cc = aa.getClass();
//        System.out.println(cc.getSimpleName());
//        System.out.println(cc.componentType());
//        System.out.println(cc.arrayType());
//        System.out.println(cc.descriptorString());
//        System.out.println(cc.getCanonicalName());
//        System.out.println(cc.getClass());
//        System.out.println("- getClasses:");
//        Arrays.stream(cc.getClasses()).forEach(System.out::println);
//        System.out.println("----------");
//        System.out.println(cc.getEnclosingMethod());
//        System.out.println("- getGenericInterfaces:");
//        Arrays.stream(cc.getGenericInterfaces()).forEach(System.out::println);
//        System.out.println("----------");
//        System.out.println(cc.getGenericSuperclass());
//        System.out.println(cc.getGenericSuperclass().getTypeName());
//        System.out.println(((ParameterizedType) cc.getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public static List<Integer> returner() {
        return new ArrayList<>(List.of(1, 2, 3)) {
        };
    }
}
