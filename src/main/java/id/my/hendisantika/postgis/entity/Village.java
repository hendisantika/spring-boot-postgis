package id.my.hendisantika.postgis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.math.BigDecimal;

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
@Table(name = "all_villages")
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gid")
    private Integer gid;

    @Column(name = "geom", columnDefinition = "geometry(Geometry,4326)")
    private Geometry geom;

    @Column(name = "shape_leng", precision = 18, scale = 11)
    private BigDecimal shapeLeng;

    @Column(name = "shape_area", precision = 18, scale = 11)
    private BigDecimal shapeArea;

    @Column(name = "adm4_en", length = 75)
    private String adm4En;

    @Column(name = "adm4_pcode", length = 25)
    private String adm4Pcode;

    @Column(name = "adm4_ref", length = 75)
    private String adm4Ref;

    @Column(name = "adm4alt1en", length = 75)
    private String adm4alt1en;

    @Column(name = "adm4alt2en", length = 75)
    private String adm4alt2en;

    @Column(name = "adm3_en", length = 75)
    private String adm3En;

    @Column(name = "adm3_pcode", length = 25)
    private String adm3Pcode;

    @Column(name = "adm2_en", length = 75)
    private String adm2En;

    @Column(name = "adm2_pcode", length = 25)
    private String adm2Pcode;

    @Column(name = "adm1_en", length = 75)
    private String adm1En;

    @Column(name = "adm1_pcode", length = 25)
    private String adm1Pcode;

    @Column(name = "adm0_en", length = 75)
    private String adm0En;

    @Column(name = "adm0_pcode", length = 25)
    private String adm0Pcode;

    @Column(name = "date", length = 10)
    private String date;

    @Column(name = "validon", length = 10)
    private String validon;

    @Column(name = "validto", length = 10)
    private String validto;

    @Column(name = "kode", length = 13)
    private String kode;
}
