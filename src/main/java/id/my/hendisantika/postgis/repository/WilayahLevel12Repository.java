package id.my.hendisantika.postgis.repository;

import id.my.hendisantika.postgis.entity.WilayahLevel12;
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
 * Time: 10.20
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface WilayahLevel12Repository extends JpaRepository<WilayahLevel12, String> {

    @Query("SELECT w FROM WilayahLevel12 w WHERE LENGTH(w.kode) = 2 ORDER BY w.kode")
    List<WilayahLevel12> findAllProvinsi();

    @Query("SELECT w FROM WilayahLevel12 w WHERE LENGTH(w.kode) = 5 AND w.parentKode = :provinsiKode ORDER BY w.kode")
    List<WilayahLevel12> findKabupatenByProvinsi(@Param("provinsiKode") String provinsiKode);

    @Query("SELECT w FROM WilayahLevel12 w WHERE LENGTH(w.kode) = 8 AND w.parentKode = :kabupatenKode ORDER BY w.kode")
    List<WilayahLevel12> findKecamatanByKabupaten(@Param("kabupatenKode") String kabupatenKode);

    @Query("SELECT w FROM WilayahLevel12 w WHERE LENGTH(w.kode) = 13 AND w.parentKode = :kecamatanKode ORDER BY w.kode")
    List<WilayahLevel12> findDesaByKecamatan(@Param("kecamatanKode") String kecamatanKode);

    @Query("SELECT w FROM WilayahLevel12 w WHERE LOWER(w.nama) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY LENGTH(w.kode), w.kode")
    List<WilayahLevel12> searchByNama(@Param("keyword") String keyword);
}
