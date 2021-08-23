package org.fordes.subview.entity.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.fordes.subview.entity.PO.InterfaceInfo;

/**
 * @author fordes on 2021/3/17
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoDTO extends InterfaceInfo {

    private Integer server_type;

    private Integer provider_id;
}
