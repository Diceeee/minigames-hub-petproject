package com.dice.auth.core.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class UserAgentParser {

    public static UserAgentParseResult parse(String userAgent) {
        String browser = extractBrowser(userAgent);
        String os = extractOs(userAgent);

        return new UserAgentParseResult(browser, os);
    }

    private static String extractBrowser(String userAgent) {
        if (userAgent.contains("Edg/")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("OPR/")) {
            return "Opera";
        } else if (userAgent.contains("Vivaldi/")) {
            return "Vivaldi";
        } else if (userAgent.contains("YaBrowser/")) {
            return "Yandex";
        } else if (userAgent.contains("SamsungBrowser/")) {
            return "Samsung Browser";
        } else if (userAgent.contains("UCBrowser/")) {
            return "UC Browser";
        } else if (userAgent.contains("QQBrowser/")) {
            return "QQ Browser";
        } else if (userAgent.contains("Maxthon/")) {
            return "Maxthon";
        } else if (userAgent.contains("PaleMoon/")) {
            return "PaleMoon";
        } else if (userAgent.contains("Brave/")) {
            return "Brave";
        } else if (userAgent.contains("Chrome/")) {
            return "Google Chrome";
        } else if (userAgent.contains("Safari/")) {
            return "Safari";
        } else if (userAgent.contains("Firefox/")) {
            return "Firefox";
        }

        log.info("Couldn't find browser in User-Agent: {}", userAgent);
        return "Unknown Browser";
    }

    private static String extractOs(String userAgent) {
        if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
            return "iOS";
        } else if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS X")) {
            return "Mac OS";
        } else if (userAgent.contains("CrOS")) {
            return "Chrome OS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("KaiOS")) {
            return "KaiOS";
        } else if (userAgent.contains("X11") || userAgent.contains("Unix")) {
            return "Unix";
        }

        log.info("Couldn't find OS in User-Agent: {}", userAgent);
        return "Unknown OS";
    }


    public record UserAgentParseResult(String browser, String os) {

    }
}
