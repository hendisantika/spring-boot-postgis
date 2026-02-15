package id.my.hendisantika.postgis.service;

import id.my.hendisantika.postgis.dto.BoundaryData;
import id.my.hendisantika.postgis.entity.WilayahLevel12;
import id.my.hendisantika.postgis.entity.WilayahLevel34;
import id.my.hendisantika.postgis.repository.SubDistrictRepository;
import id.my.hendisantika.postgis.repository.VillageRepository;
import id.my.hendisantika.postgis.repository.WilayahLevel12Repository;
import id.my.hendisantika.postgis.repository.WilayahLevel34Repository;
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
    private final WilayahLevel34Repository wilayahLevel34Repository;
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

    public List<WilayahLevel34> getKecamatanByKabupaten(String kabupatenKode) {
        return wilayahLevel34Repository.findKecamatanByKabupaten(kabupatenKode);
    }

    public List<WilayahLevel34> getDesaByKecamatan(String kecamatanKode) {
        return wilayahLevel34Repository.findDesaByKecamatan(kecamatanKode);
    }

    public Optional<WilayahLevel12> getByKode(String kode) {
        return wilayahRepository.findById(kode);
    }

    public Optional<WilayahLevel34> getLevel34ByKode(String kode) {
        return wilayahLevel34Repository.findById(kode);
    }

    public List<WilayahLevel12> search(String keyword) {
        return wilayahRepository.searchByNama(keyword);
    }

    public Optional<BoundaryData> getBoundaryData(String kode) {
        try {
            int kodeLength = kode.length();

            // Level 3 (kecamatan) or Level 4 (desa) - from wilayah_level_3_4
            if (kodeLength >= 8) {
                Optional<WilayahLevel34> w34 = getLevel34ByKode(kode);
                if (w34.isEmpty()) {
                    return Optional.empty();
                }

                WilayahLevel34 wilayah = w34.get();
                BoundaryData.BoundaryDataBuilder builder = BoundaryData.builder()
                        .kode(wilayah.getKode())
                        .nama(wilayah.getNama())
                        .level(wilayah.getLevelName())
                        .lat(wilayah.getLat())
                        .lng(wilayah.getLng());

                // Get geometry from PostGIS tables
                String geoJson = null;
                if (kodeLength == 8) {
                    geoJson = subDistrictRepository.findGeometryAsGeoJSON(kode);
                } else if (kodeLength == 13) {
                    geoJson = villageRepository.findGeometryAsGeoJSON(kode);
                }

                if (geoJson != null && !geoJson.isEmpty()) {
                    builder.coordinates(geoJson);
                } else if (wilayah.getPath() != null && !wilayah.getPath().isEmpty()) {
                    builder.coordinates(wilayah.getPath());
                }

                return Optional.of(builder.build());
            }

            // Level 1 (provinsi) or Level 2 (kabupaten) - from wilayah_level_1_2
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

            if (kodeLength == 5) {
                // Kabupaten: compute boundary from ST_Union of kecamatan geometries
                String geoJson = subDistrictRepository.findKabupatenBoundaryAsGeoJSON(kode);
                if (geoJson != null && !geoJson.isEmpty()) {
                    builder.coordinates(geoJson);
                } else if (wilayah.getPath() != null && !wilayah.getPath().isEmpty()) {
                    builder.coordinates(wilayah.getPath());
                }
            } else if (wilayah.getPath() != null && !wilayah.getPath().isEmpty()) {
                // Provinsi: use path field (ST_Union too expensive for hundreds of kecamatan)
                builder.coordinates(wilayah.getPath());
            }

            return Optional.of(builder.build());

        } catch (Exception e) {
            log.error("Error getting boundary data for kode: {}", kode, e);
            return Optional.empty();
        }
    }
}
