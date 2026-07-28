package de.invesdwin.util.swing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

/**
 * Can be used in main classes to auto detect HiDPI where Java is unable to do so by itself. Currently implements
 * fallbacks for Linux in gnome-shell.
 */
@Immutable
public final class SunJava2dUIScaleConfigurer {

    private SunJava2dUIScaleConfigurer() {}

    public static void configure() {
        // 1. Don't overwrite if manually set via CLI (-Dsun.java2d.uiScale=...)
        //CHECKSTYLE:OFF
        if (System.getProperty("sun.java2d.uiScale") != null) {
            //CHECKSTYLE:ON
            return;
        }

        final double scale = detectScale();
        if (scale > 1.0) {
            //CHECKSTYLE:OFF
            System.setProperty("sun.java2d.uiScale", String.valueOf((int) Math.round(scale)));
            //CHECKSTYLE:ON
        }
    }

    private static double detectScale() {
        // Probe 1: Check GDK_SCALE environment variable
        final String gdkScale = System.getenv("GDK_SCALE");
        if (gdkScale != null && !gdkScale.trim().isEmpty()) {
            try {
                return Double.parseDouble(gdkScale.trim());
            } catch (final NumberFormatException ignored) {
            }
        }

        // Probe 2: Check Xft.dpi via 'xrdb -query' (Works on X11 & XWayland)
        // Default DPI is 96 (100%). 192 DPI means 200% scale.
        final double xrdbScale = checkXrdbDpi();
        if (xrdbScale > 1.0) {
            return xrdbScale;
        }

        // Probe 3: Check ~/.config/monitors.xml (GNOME Mutter native config)
        final double monitorsXmlScale = checkMonitorsXml();
        if (monitorsXmlScale > 1.0) {
            return monitorsXmlScale;
        }

        return 1.0;
    }

    private static double checkXrdbDpi() {
        try {
            final Process process = new ProcessBuilder("xrdb", "-query").start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("xft.dpi")) {
                        final String[] parts = line.split(":");
                        if (parts.length >= 2) {
                            final double dpi = Double.parseDouble(parts[1].trim());
                            return dpi / 96.0; // 192 / 96 = 2.0
                        }
                    }
                }
            }
        } catch (final Exception ignored) {
        }
        return 1.0;
    }

    private static double checkMonitorsXml() {
        try {
            //CHECKSTYLE:OFF
            final File file = new File(System.getProperty("user.home"), ".config/monitors.xml");
            //CHECKSTYLE:ON
            if (file.exists()) {
                final String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                final Matcher matcher = Pattern.compile("<scale>([0-9.]+)</scale>").matcher(content);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1));
                }
            }
        } catch (final Exception ignored) {
        }
        return 1.0;
    }
}