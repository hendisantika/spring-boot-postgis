package id.my.hendisantika.postgis.repository;

import id.my.hendisantika.postgis.entity.Village;
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
 * Time: 10.30
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface VillageRepository extends JpaRepository<Village, Integer> {

    Optional<Village> findByKodeKd(String kodeKd);

    @Query(value = "SELECT ST_AsGeoJSON(geom) FROM all_villages_2023 WHERE kode_kd = :kode LIMIT 1", nativeQuery = true)
    String findGeometryAsGeoJSON(@Param("kode") String kode);
}
