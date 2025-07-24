package com.example.blockchain.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StaticExplorerRedirect {

    @GetMapping("/explorer")
    public String redirectExplorer() {
        return "forward:/explorer/index.html";
    }
    @GetMapping("/explorer/")
    public String redirectExplorerSlash() {
        return "forward:/explorer/index.html";
    }
    @GetMapping("/index.html")
    public String redirectIndex() {
        return "forward:/explorer";
    }

    @GetMapping("/block.html")
    public String redirectBlock() {
        return "forward:/explorer/block.html";
    }
}