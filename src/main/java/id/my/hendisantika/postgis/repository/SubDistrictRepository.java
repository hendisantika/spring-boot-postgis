package id.my.hendisantika.postgis.repository;

import id.my.hendisantika.postgis.entity.SubDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 10.25
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface SubDistrictRepository extends JpaRepository<SubDistrict, Integer> {

    Optional<SubDistrict> findByKode(String kode);

    @Query(value = "SELECT ST_AsGeoJSON(geom) FROM idn_admbnda_adm3_bps_20200401 WHERE kode = :kode LIMIT 1", nativeQuery = true)
    String findGeometryAsGeoJSON(@Param("kode") String kode);
}
