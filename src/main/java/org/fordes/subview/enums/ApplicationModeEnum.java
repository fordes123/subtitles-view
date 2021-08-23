package org.fordes.subview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationModeEnum {

    START(1000.00, 680.00, "/css/light.css", "/css/dark.css"),
    MAIN(1340.00,840.00, "/css/light.css", "/css/dark.css");


    private final double width;

    private final double height;

    private final String theme_light;

    private final String theme_dark;
}
