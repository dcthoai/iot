package vn.ptit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class StaticResourceControllerAdvice {

    @Value("${resources.static.url}")
    private String staticResourceUrl;

    @ModelAttribute
    public void addStaticResourceUrl(Model model) {
        model.addAttribute("RESOURCES_URL", staticResourceUrl);
    }
}
