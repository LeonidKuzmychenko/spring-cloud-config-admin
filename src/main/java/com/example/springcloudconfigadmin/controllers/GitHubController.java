package com.example.springcloudconfigadmin.controllers;

import com.example.springcloudconfigadmin.dto.ConfigFileBase64Response;
import com.example.springcloudconfigadmin.dto.ConfigFileDto;
import com.example.springcloudconfigadmin.dto.ConfigFileNameResponse;
import com.example.springcloudconfigadmin.dto.change.ChangeFileResponse;
import com.example.springcloudconfigadmin.utils.Base64Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/github")
public class GitHubController {


    private final String accessToken;
    private final String owner;
    private final String repo;
    private final String branch;

    private final RestTemplate restTemplate = new RestTemplate();

    public GitHubController(@Value("ACCESS_TOKEN") String accessToken,
                            @Value("OWNER_NAME") String owner,
                            @Value("REPO_NAME") String repo,
                            @Value("BRANCH_NAME") String branch) {
        this.accessToken = accessToken;
        this.owner = owner;
        this.repo = repo;
        this.branch = branch;
    }

    @GetMapping("/configs/files")
    public ResponseEntity<List<ConfigFileNameResponse>> getConfigsFiles() {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/configs?ref=" + branch;

        HttpEntity<String> entity = new HttpEntity<>(headers());
        ResponseEntity<ConfigFileNameResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, ConfigFileNameResponse[].class);
        ConfigFileNameResponse[] body = response.getBody();
        Objects.requireNonNull(body);
        List<ConfigFileNameResponse> list = Arrays.asList(body);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/configs/file")
    public ResponseEntity<ConfigFileDto> getConfigsFile(@RequestParam("fileName") String fileName) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/configs/" + fileName + "?ref=" + branch;

        HttpEntity<String> entity = new HttpEntity<>(headers());
        ResponseEntity<ConfigFileBase64Response> response = restTemplate.exchange(url, HttpMethod.GET, entity, ConfigFileBase64Response.class);
        ConfigFileBase64Response body = response.getBody();
        Objects.requireNonNull(body);
        String content = Base64Utils.splitDecodeBase64(body.getContent()).replaceAll("\r\n", "\n");
        ConfigFileDto configFileDto = new ConfigFileDto(body.getName(), content, body.getSha());
        return new ResponseEntity<>(configFileDto, HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity<ConfigFileDto> editFile(@RequestParam String fileName, @RequestParam String sha, @RequestBody String content) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/configs/" + fileName;

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Updating file: " + fileName);
        body.put("content", Base64.getEncoder().encodeToString(content.getBytes()));
        body.put("branch", branch);
        body.put("sha", sha);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers());
        ResponseEntity<ChangeFileResponse> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ChangeFileResponse.class);
        ChangeFileResponse changeFileResponse = response.getBody();
        Objects.requireNonNull(changeFileResponse);
        ConfigFileDto configFileDto = new ConfigFileDto(fileName, content, changeFileResponse.getContent().getSha());
//        System.out.println(changeFileResponse);
//        changeFileResponse.setContent(content);
        return new ResponseEntity<>(configFileDto, HttpStatus.OK);
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }
}
