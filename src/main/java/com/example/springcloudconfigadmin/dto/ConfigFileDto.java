package com.example.springcloudconfigadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigFileDto {

    private String name;
    private String content;
    private String sha;
}
