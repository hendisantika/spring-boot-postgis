package id.my.hendisantika.postgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgis
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 15/02/26
 * Time: 10.15
 * To change this template use File | Settings | File Templates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoundaryData {
    private String kode;
    private String nama;
    private String level;
    private Double lat;
    private Double lng;
    private String coordinates; // GeoJSON string or coordinate array
}
