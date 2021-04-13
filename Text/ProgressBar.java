package net.matixmedia.utils;

import org.bukkit.ChatColor;

import java.util.Collections;

public class ProgressBar {
    public static String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return String.join("", Collections.nCopies(progressBars, "" + completedColor + symbol)) +
                String.join("", Collections.nCopies(totalBars - progressBars, "" + notCompletedColor + symbol));
    }
}
