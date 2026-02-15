package id.my.hendisantika.postgis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 10.00
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wilayah_level_1_2")
public class WilayahLevel12 {

    @Id
    @Column(name = "kode", length = 13, nullable = false)
    private String kode;

    @Column(name = "nama", length = 255)
    private String nama;

    @Column(name = "ibukota", length = 100)
    private String ibukota;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Column(name = "elv")
    private Float elv;

    @Column(name = "tz")
    private Short tz;

    @Column(name = "luas")
    private Double luas;

    @Column(name = "penduduk")
    private Double penduduk;

    @Column(name = "path", columnDefinition = "TEXT")
    private String path;

    @Column(name = "status")
    private Short status;

    public String getLevel() {
        if (kode == null) return "Unknown";
        int length = kode.length();
        if (length == 2) return "Provinsi";
        if (length == 5) return "Kabupaten/Kota";
        if (length == 8) return "Kecamatan";
        if (length == 13) return "Desa/Kelurahan";
        return "Unknown";
    }
}
