package id.my.hendisantika.postgis.controller;

import id.my.hendisantika.postgis.dto.BoundaryData;
import id.my.hendisantika.postgis.entity.WilayahLevel12;
import id.my.hendisantika.postgis.service.WilayahService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 10.45
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/wilayah")
@RequiredArgsConstructor
public class WilayahController {

    private final WilayahService wilayahService;

    @GetMapping("/provinsi")
    public String getProvinsiList(Model model) {
        model.addAttribute("wilayahList", wilayahService.getAllProvinsi());
        return "fragments/wilayah-list :: wilayah-list";
    }

    @GetMapping("/kabupaten/{provinsiKode}")
    public String getKabupatenList(@PathVariable String provinsiKode, Model model) {
        model.addAttribute("wilayahList", wilayahService.getKabupatenByProvinsi(provinsiKode));
        return "fragments/wilayah-list :: wilayah-list";
    }

    @GetMapping("/kecamatan/{kabupatenKode}")
    public String getKecamatanList(@PathVariable String kabupatenKode, Model model) {
        model.addAttribute("wilayahList", wilayahService.getKecamatanByKabupaten(kabupatenKode));
        return "fragments/wilayah-list :: wilayah-list";
    }

    @GetMapping("/desa/{kecamatanKode}")
    public String getDesaList(@PathVariable String kecamatanKode, Model model) {
        model.addAttribute("wilayahList", wilayahService.getDesaByKecamatan(kecamatanKode));
        return "fragments/wilayah-list :: wilayah-list";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("wilayahList", wilayahService.search(keyword));
        return "fragments/wilayah-list :: wilayah-list";
    }

    @GetMapping("/detail/{kode}")
    public String getDetail(@PathVariable String kode, Model model) {
        int kodeLength = kode.length();
        if (kodeLength >= 8) {
            // Kecamatan or Desa from wilayah_level_3_4
            wilayahService.getLevel34ByKode(kode).ifPresent(wilayah -> {
                model.addAttribute("wilayah34", wilayah);
            });
        } else {
            // Provinsi or Kabupaten from wilayah_level_1_2
            wilayahService.getByKode(kode).ifPresent(wilayah ->
                    model.addAttribute("wilayah", wilayah));
        }
        return "fragments/wilayah-detail :: wilayah-detail";
    }

    @GetMapping("/api/boundaries/{kode}")
    @ResponseBody
    public ResponseEntity<WilayahLevel12> getBoundaries(@PathVariable String kode) {
        return wilayahService.getByKode(kode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllWithBoundaries() {
        List<Map<String, Object>> result = wilayahService.getAllProvinsi().stream()
                .map(w -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("kode", w.getKode());
                    map.put("nama", w.getNama());
                    map.put("ibukota", w.getIbukota());
                    map.put("lat", w.getLat());
                    map.put("lng", w.getLng());
                    map.put("luas", w.getLuas());
                    map.put("penduduk", w.getPenduduk());
                    map.put("level", w.getLevel());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/kabupaten-select/{provinsiKode}")
    public String getKabupatenSelect(@PathVariable String provinsiKode, Model model) {
        model.addAttribute("kabupatenList", wilayahService.getKabupatenByProvinsi(provinsiKode));
        return "fragments/kabupaten-select :: kabupaten-select";
    }

    @GetMapping("/kecamatan-select/{kabupatenKode}")
    public String getKecamatanSelect(@PathVariable String kabupatenKode, Model model) {
        model.addAttribute("kecamatanList", wilayahService.getKecamatanByKabupaten(kabupatenKode));
        return "fragments/kecamatan-select :: kecamatan-select";
    }

    @GetMapping("/desa-select/{kecamatanKode}")
    public String getDesaSelect(@PathVariable String kecamatanKode, Model model) {
        model.addAttribute("desaList", wilayahService.getDesaByKecamatan(kecamatanKode));
        return "fragments/desa-select :: desa-select";
    }

    @GetMapping("/api/boundary/{kode}")
    @ResponseBody
    public ResponseEntity<BoundaryData> getBoundaryWithGeometry(@PathVariable String kode) {
        return wilayahService.getBoundaryData(kode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
