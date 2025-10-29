package org.lemanoman.filerepository.controller;

import org.lemanoman.filerepository.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {
    private final StoreService storeService;

    public IndexController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("resume", storeService.getResumeStatus());
        return "index";
    }
}
