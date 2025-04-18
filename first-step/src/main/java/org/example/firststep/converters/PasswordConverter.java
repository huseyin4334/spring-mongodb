package org.example.firststep.converters;

import org.example.firststep.utils.MongoPasswordEncryptor;
import org.springframework.data.mongodb.core.convert.MongoConversionContext;
import org.springframework.data.mongodb.core.convert.MongoValueConverter;

public class PasswordConverter implements MongoValueConverter<String, String> {

    private final MongoPasswordEncryptor converter = new MongoPasswordEncryptor();

    @Override
    public String read(String value, MongoConversionContext context) {
        return  converter.decryptPassword(value);
    }

    @Override
    public String write(String value, MongoConversionContext context) {
        return converter.encryptPassword(value);
    }
}

/*
    MongoConversionContext is a context object that provides information about the current conversion operation.
    We can access the other properties of the object. Also, we can write and read the value of the object.
 */
