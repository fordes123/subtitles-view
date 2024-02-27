package org.fordes.subtitles.view.enums;

import lombok.AllArgsConstructor;

/**
 * 图标枚举
 *
 * @author fordes on 2022/1/23
 */
@AllArgsConstructor
public enum FontIcon {

    SCENE_CLOSE("\ue648"),
    SCENE_MINIMIZE("\ue634"),
    EXIT_FULL_SCREEN("\ue61f"),
    FULL_SCREEN("\ue628"),
    ITEM_START("\ue669"),
    ITEM_SEARCH("\uec6f"),
    ITEM_TOOL("\ue64a"),
    LOGO("\ue69f"),
    SETTING("\ue711"),
    CHOOSE_FILE("\ue64e"),

    ENGINE_DDZM("\ue63b"),
    ENGINE_ASSRT("\ue609"),
    ENGINE_ZMK("\ue623"),
    ENGINE("\ue60f"),

    PLACE_THE_LEFT("\uec70"),
    PLACE_THE_RIGHT("\ue61a"),

    SETTING_PREFERENCES("\ue63c"),
    SETTING_INTERFACE("\ue62d"),

    EDIT_BAR_SEARCH("\ue754"),
    EDIT_BAR_REPLACE("\ue674"),
    EDIT_BAR_JUMP("\ue695"),
    EDIT_BAR_FONT("\ue61d"),
    EDIT_BAR_HIDE("\ue60b"),
    EDIT_BAR_TIMELINE("\ue64f"),
    EDIT_BAR_CODE("\ue629"),

    EDIT_BAR_REF("\ue62c"),

    EDIT_BAR_OPTION("\ue86c"),

    EDIT_BAR_REPLACE_ITEM("\ue63e"),

    EDIT_BAR_REPLACE_ALL("\ue642"),

    SWITCH_OFF_LIGHT("\ue612"),
    SWITCH_ON_LIGHT("\ue611"),
    SWITCH_OFF_DARK("\ue613"),
    SWITCH_ON_DARK("\ue615"),

    EDIT_BAR_TRANSLATE("\ue6fb");

    private final String unicode;

    @Override
    public String toString() {
        return unicode;
    }
}
