package org.example;

import javax.sound.midi.SoundbankResource;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public List<Integer> field1 = new ArrayList<>(List.of(1, 2, 3));
    public Integer[] field2 = new Integer[]{1,2,3,};
    public Long field3 = 1L;

    public static <T>void main(String[] args) {
        Method method1 = Arrays.stream(Main.class.getMethods()).filter(method -> method.getName().equals("returner1")).findFirst().get();
        Method method2 = Arrays.stream(Main.class.getMethods()).filter(method -> method.getName().equals("returner2")).findFirst().get();
        Field field1 = Arrays.stream(Main.class.getFields()).filter(field -> field.getName().equals("field1")).findFirst().get();
        Field field2 = Arrays.stream(Main.class.getFields()).filter(field -> field.getName().equals("field2")).findFirst().get();
        Field field3 = Arrays.stream(Main.class.getFields()).filter(field -> field.getName().equals("field3")).findFirst().get();
        System.out.println(method1);
        System.out.println(method2);
        System.out.println(field1);
        System.out.println(field2);
        System.out.println(field3);
        System.out.println();
        var aa = ((ParameterizedType) method1.getAnnotatedReturnType().getType());

        System.out.println(method1.getAnnotatedReturnType());
        System.out.println(method2.getAnnotatedReturnType());
        System.out.println(field1.getAnnotatedType());
        System.out.println(field2.getAnnotatedType());
        System.out.println(field3.getAnnotatedType());

        System.out.println();
        System.out.println(method1.getAnnotatedReturnType().getType());
        System.out.println(method2.getAnnotatedReturnType().getType());
        System.out.println(field1.getAnnotatedType().getType());
        System.out.println(field2.getAnnotatedType().getType());
        System.out.println(field3.getAnnotatedType().getType());
    }

    public static List<Integer> returner1() {
        return new ArrayList<>(List.of(1, 2, 3));
    }

    public static int[] returner2() {
        return new int[]{1,2,3};
    }
}
