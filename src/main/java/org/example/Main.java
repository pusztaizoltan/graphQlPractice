package org.example;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            var aa = Introspector.getBeanInfo(AuthorDTO.class);
            Arrays.stream(aa.getMethodDescriptors()).forEach(System.out::println);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
//        var value = new PropertyDescriptor("name", Person.class).getWriteMethod();

    }
}
