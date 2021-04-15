package net.matixmedia.utils;

import lombok.Getter;
import org.bukkit.DyeColor;

public enum DiverseColor {

    BLACK((short) 15, DyeColor.BLACK),
    BLUE((short) 11, DyeColor.BLUE),
    BROWN((short) 12, DyeColor.BROWN),
    CYAN((short) 9, DyeColor.CYAN),
    GRAY((short) 7, DyeColor.GRAY),
    GREEN((short) 13, DyeColor.GREEN),
    LIGHT_BLUE((short) 3, DyeColor.LIGHT_BLUE),
    LIME((short) 5, DyeColor.LIME),
    MAGENTA((short) 2, DyeColor.MAGENTA),
    ORANGE((short) 1, DyeColor.ORANGE),
    PINK((short) 6, DyeColor.PINK),
    PURPLE((short) 10, DyeColor.PURPLE),
    RED((short) 14, DyeColor.RED),
    SILVER((short) 8, DyeColor.SILVER),
    WHITE((short) 0, DyeColor.WHITE),
    YELLOW((short) 4, DyeColor.YELLOW);


    @Getter
    private short blockData;
    @Getter
    private DyeColor dyeColor;

    private DiverseColor(short blockData, DyeColor dyeColor) {
        this.blockData = blockData;
        this.dyeColor = dyeColor;
    }
}
