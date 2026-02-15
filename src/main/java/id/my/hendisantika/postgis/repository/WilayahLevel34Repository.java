package id.my.hendisantika.postgis.repository;

import id.my.hendisantika.postgis.entity.WilayahLevel34;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 11.05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface WilayahLevel34Repository extends JpaRepository<WilayahLevel34, String> {

    @Query("SELECT w FROM WilayahLevel34 w WHERE w.parentKode = :kabupatenKode AND w.level = 3 ORDER BY w.kode")
    List<WilayahLevel34> findKecamatanByKabupaten(@Param("kabupatenKode") String kabupatenKode);

    @Query("SELECT w FROM WilayahLevel34 w WHERE w.parentKode = :kecamatanKode AND w.level = 4 ORDER BY w.kode")
    List<WilayahLevel34> findDesaByKecamatan(@Param("kecamatanKode") String kecamatanKode);
}
