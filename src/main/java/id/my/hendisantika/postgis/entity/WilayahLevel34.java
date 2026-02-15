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
 * Time: 11.00
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wilayah_level_3_4")
public class WilayahLevel34 {

    @Id
    @Column(name = "kode", length = 20, nullable = false)
    private String kode;

    @Column(name = "nama", length = 100)
    private String nama;

    @Column(name = "parent_kode", length = 20)
    private String parentKode;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Column(name = "path", columnDefinition = "TEXT")
    private String path;

    @Column(name = "level")
    private Short level;

    public String getLevelName() {
        if (level == null) return "Unknown";
        return switch (level.intValue()) {
            case 3 -> "Kecamatan";
            case 4 -> "Desa/Kelurahan";
            default -> "Unknown";
        };
    }
}
