package org.example.graphQL;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Getter
public class SchemaLoader {
    private TypeDefinitionRegistry schemaFromFile;

    public SchemaLoader(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("schema.graphqls")))) {
            schemaFromFile = new SchemaParser().parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
