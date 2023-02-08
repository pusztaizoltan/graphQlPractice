package org.example.graphql;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;

public class SchemaLoader {
    @Getter
    private TypeDefinitionRegistry schemaFromFile;

    public SchemaLoader() {
        try (BufferedReader reader = new BufferedReader(new FileReader("schema.graphqls"))) {
            schemaFromFile = new SchemaParser().parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
