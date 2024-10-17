package com.example.springcloudconfigadmin.dto.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeFileResponse {

    @JsonProperty("content")
    private Content content;

}