package com.example.springcloudconfigadmin.utils;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class Base64Utils {

    public static String splitDecodeBase64(String content){
        return Stream.of(content)
                .flatMap(it -> Stream.of(it.split("\n")))
                .map(base64 -> new String(Base64.getDecoder().decode(base64)))
                .collect(Collectors.joining(""));
    }

}
