package id.my.hendisantika.postgis.service;

import id.my.hendisantika.postgis.dto.BoundaryData;
import id.my.hendisantika.postgis.entity.SubDistrict;
import id.my.hendisantika.postgis.entity.Village;
import id.my.hendisantika.postgis.entity.WilayahLevel12;
import id.my.hendisantika.postgis.repository.SubDistrictRepository;
import id.my.hendisantika.postgis.repository.VillageRepository;
import id.my.hendisantika.postgis.repository.WilayahLevel12Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 10.35
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WilayahService {

    private final WilayahLevel12Repository wilayahRepository;
    private final SubDistrictRepository subDistrictRepository;
    private final VillageRepository villageRepository;

    public List<WilayahLevel12> getAll() {
        return wilayahRepository.findAll();
    }

    public List<WilayahLevel12> getAllProvinsi() {
        return wilayahRepository.findAllProvinsi();
    }

    public List<WilayahLevel12> getKabupatenByProvinsi(String provinsiKode) {
        return wilayahRepository.findKabupatenByProvinsi(provinsiKode);
    }

    public List<WilayahLevel12> getKecamatanByKabupaten(String kabupatenKode) {
        return wilayahRepository.findKecamatanByKabupaten(kabupatenKode);
    }

    public List<WilayahLevel12> getDesaByKecamatan(String kecamatanKode) {
        return wilayahRepository.findDesaByKecamatan(kecamatanKode);
    }

    public Optional<WilayahLevel12> getByKode(String kode) {
        return wilayahRepository.findById(kode);
    }

    public List<WilayahLevel12> search(String keyword) {
        return wilayahRepository.searchByNama(keyword);
    }

    public Optional<BoundaryData> getBoundaryData(String kode) {
        try {
            Optional<WilayahLevel12> wilayahOpt = getByKode(kode);
            if (wilayahOpt.isEmpty()) {
                return Optional.empty();
            }

            WilayahLevel12 wilayah = wilayahOpt.get();
            BoundaryData.BoundaryDataBuilder builder = BoundaryData.builder()
                    .kode(wilayah.getKode())
                    .nama(wilayah.getNama())
                    .level(wilayah.getLevel())
                    .lat(wilayah.getLat())
                    .lng(wilayah.getLng());

            // Try to get geometry from PostGIS tables based on kode length
            String geoJson = null;
            int kodeLength = kode.length();

            if (kodeLength == 8) {
                // Level 3 - Sub-district
                geoJson = subDistrictRepository.findGeometryAsGeoJSON(kode);
            } else if (kodeLength == 13) {
                // Level 4 - Village
                geoJson = villageRepository.findGeometryAsGeoJSON(kode);
            }

            // If we have geometry, add it
            if (geoJson != null && !geoJson.isEmpty()) {
                builder.coordinates(geoJson);
            } else if (wilayah.getPath() != null && !wilayah.getPath().isEmpty()) {
                // Fallback to path data if available
                builder.coordinates(wilayah.getPath());
            }

            return Optional.of(builder.build());

        } catch (Exception e) {
            log.error("Error getting boundary data for kode: {}", kode, e);
            return Optional.empty();
        }
    }
}
