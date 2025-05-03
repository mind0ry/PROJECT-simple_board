// src/main/java/com/simpleboard/config/GlobalControllerAdvice.java
package com.simpleboard.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalControllerAdvice {

    @ModelAttribute("currentNickname")
    public String currentNickname(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("NICKNAME".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
