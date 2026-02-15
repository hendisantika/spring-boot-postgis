package id.my.hendisantika.postgis.controller;

import id.my.hendisantika.postgis.service.WilayahService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 10.40
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final WilayahService wilayahService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Beranda");
        model.addAttribute("provinsiList", wilayahService.getAllProvinsi());
        return "index";
    }

    @GetMapping("/map")
    public String map(Model model) {
        model.addAttribute("title", "Peta Interaktif");
        return "map";
    }
}
