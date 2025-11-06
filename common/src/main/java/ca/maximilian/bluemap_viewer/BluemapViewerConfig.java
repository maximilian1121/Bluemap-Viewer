package ca.maximilian.bluemap_viewer;

import java.util.ArrayList;
import java.util.List;

import eu.midnightdust.lib.config.MidnightConfig;

public class BluemapViewerConfig extends MidnightConfig {
    @Entry
    public static List<String> serverUrls = new ArrayList<>();

    @Entry
    public static boolean keepBrowserInMemory = true;
}
