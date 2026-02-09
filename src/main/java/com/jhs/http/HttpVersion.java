package com.jhs.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1", 1, 1),
    HTTP_1_0("HTTP/1.0", 1, 0);
    
    public final String LITTERAL;
    public final int MINOR;
    public final int MAJOR;

    HttpVersion(String litteral, int MAJOR, int MINOR) {
        this.LITTERAL = litteral;
        this.MAJOR = MAJOR;
        this.MINOR = MINOR;
    }

    // private static final Pattern httpVersionRegexPattern = Pattern.compile("^HTTP/(<?major>\\d+).(<?minor>\\d+)");
    private static final Pattern httpVersionRegexPattern = Pattern.compile("^HTTP/(?<major>\\d+)\\.(?<minor>\\d+)");

    public static HttpVersion getBestCompatibleVersion(String litteralversion) throws BadHttpVersionException {
        Matcher matcher = httpVersionRegexPattern.matcher(litteralversion);    
        if (!matcher.find() || matcher.groupCount() != 2 ) {
            throw new BadHttpVersionException();
        }
        int major = Integer.parseInt(matcher.group("major"));
        int minor = Integer.parseInt(matcher.group("minor"));

        HttpVersion tempBestCompatibleVersion = null;
        for (HttpVersion version : HttpVersion.values()) {
            if (version.LITTERAL.equals(litteralversion)) {
                return version;
            } else {
                if (version.MAJOR == major) {
                    if (version.MINOR < minor) { // garde la plus haute des versions < demandée
                        if (tempBestCompatibleVersion == null || 
                            version.MINOR > tempBestCompatibleVersion.MINOR) {
                            tempBestCompatibleVersion = version;
                        }
                    }
                }
            }
        }
        return tempBestCompatibleVersion;
    }
}