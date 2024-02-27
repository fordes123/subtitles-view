package org.fordes.subtitles.view.model.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.fordes.subtitles.view.model.PO.ServiceInterface;
import org.fordes.subtitles.view.model.PO.Version;

/**
 * @author fordes on 2022/4/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AvailableServiceInfo extends ServiceInterface {

    private Version versionInfo;

    @Override
    public String toString() {
        return getProvider().getDesc() + getType().getDesc();
    }
}
