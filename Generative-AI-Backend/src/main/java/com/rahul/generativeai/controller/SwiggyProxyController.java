package com.rahul.generativeai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/swiggy")
@CrossOrigin("https://main.dt6i6q82xp8vm.amplifyapp.com/")
public class SwiggyProxyController {

    @Autowired
    private final RestTemplate restTemplate;

    public SwiggyProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CrossOrigin(origins = "https://main.dt6i6q82xp8vm.amplifyapp.com/")
    @RequestMapping("/swiggy-proxy/restaurants")
    public ResponseEntity<String> getRestaurants() {

        String url = String.format("https://www.swiggy.com/dapi/restaurants/list/v5?lat=17.3684658&lng=78.53159409999999&is-seo-homepage-enabled=true&page_type=DESKTOP_WEB_LISTING");
        System.out.println(url);
        return restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    }

    @CrossOrigin(origins = "https://main.dt6i6q82xp8vm.amplifyapp.com/")
    @RequestMapping("/swiggy-proxy/restaurantsMenu")
    public ResponseEntity<String> getRestaurantsMenu(@RequestParam String resId) {
        String url = String.format("https://www.swiggy.com/mapi/menu/pl?page-type=REGULAR_MENU&complete-menu=true&lat=17.37240&lng=78.43780&restaurantId="+resId+"");
        System.out.println(url);
        return restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    }
}
