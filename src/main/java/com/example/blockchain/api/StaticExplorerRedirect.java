package com.example.blockchain.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticExplorerRedirect {

    @GetMapping("/explorer")
    public String redirectExplorer() {
        return "forward:/explorer/index.html";
    }
}