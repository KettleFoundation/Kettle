package mc.kettle.kettleserver;

import java.util.logging.Logger;

public class Kettle {
	private static final String REMAP_NAME = "v1_12_R1";
	private static final String REMAP_PREFIX = "net/minecraft/server/";
	public static Logger logger;

	public static String getRemapName() {
	    return REMAP_NAME;
    }

    public static String getRemapPrefix() {
        return REMAP_PREFIX;
    }

    public static void main(String[] args) {
	    if (System.getProperty("log4j.configurationFile") == null) {
	        System.setProperty("log4j.configurationFile", "log4j2.xml");
        }


    }
}