package id.my.hendisantika.postgis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 10.10
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "all_villages_2023")
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gid")
    private Integer gid;

    @Column(name = "geom", columnDefinition = "geometry(Geometry,4326)")
    private Geometry geom;

    @Column(name = "kode_kd", length = 50)
    private String kodeKd;

    @Column(name = "kode_kec", length = 50)
    private String kodeKec;

    @Column(name = "kode_kk", length = 50)
    private String kodeKk;

    @Column(name = "kode_prov", length = 50)
    private String kodeProv;

    @Column(name = "kel_desa", length = 50)
    private String kelDesa;

    @Column(name = "kecamatan", length = 50)
    private String kecamatan;

    @Column(name = "kab_kota", length = 50)
    private String kabKota;

    @Column(name = "provinsi", length = 50)
    private String provinsi;

    @Column(name = "jenis_kd", length = 254)
    private String jenisKd;
}
