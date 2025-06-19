package com.dice.auth.core.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class AuthUtils {

    public static String getOriginalUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        return Optional.ofNullable(cookies).flatMap(cooks -> Arrays.stream(cooks)
                        .filter(cookie -> cookie.getName().equals(cookieName))
                        .findFirst())
                .map(Cookie::getValue);
    }

    /**
     * Normalizes IP addresses for better readability and consistency.
     * Converts IPv6 localhost to IPv4 localhost and handles other common cases.
     */
    public static String normalizeIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return ip;
        }
        
        // Convert IPv6 localhost to IPv4 localhost
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        
        // Handle other IPv6 localhost variations
        if (ip.startsWith("::ffff:127.0.0.1") || ip.startsWith("::ffff:0:0:0:1")) {
            return "127.0.0.1";
        }
        
        // Handle IPv4-mapped IPv6 addresses
        if (ip.startsWith("::ffff:")) {
            String ipv4Part = ip.substring(7); // Remove "::ffff:" prefix
            if (isValidIpAddress(ipv4Part)) {
                return ipv4Part;
            }
        }
        
        return ip;
    }

    /**
     * Extracts the real client IP address from HttpServletRequest.
     * Handles proxy scenarios by checking X-Forwarded-For, X-Real-IP, and other headers.
     * 
     * @param request The HTTP request
     * @return The client IP address, or "unknown" if not found
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        return normalizeIpAddress(getClientIpAddress(request, true));
    }

    /**
     * Extracts the real client IP address from HttpServletRequest.
     * Handles proxy scenarios by checking X-Forwarded-For, X-Real-IP, and other headers.
     * 
     * @param request The HTTP request
     * @param trustProxyHeaders Whether to trust proxy headers (set to false for internal networks without proxies)
     * @return The client IP address, or "unknown" if not found
     */
    public static String getClientIpAddress(HttpServletRequest request, boolean trustProxyHeaders) {
        if (trustProxyHeaders) {
            // Check X-Forwarded-For header first (most common proxy header)
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
                // X-Forwarded-For can contain multiple IPs: client, proxy1, proxy2, ...
                // The first IP is the original client
                String[] ips = xForwardedFor.split(",");
                String clientIp = ips[0].trim();
                if (isValidIpAddress(clientIp) && !isPrivateNetwork(clientIp)) {
                    return clientIp;
                }
            }

            // Check X-Real-IP header (used by some proxies)
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
                if (isValidIpAddress(xRealIp) && !isPrivateNetwork(xRealIp)) {
                    return xRealIp;
                }
            }

            // Check X-Client-IP header
            String xClientIp = request.getHeader("X-Client-IP");
            if (xClientIp != null && !xClientIp.isEmpty() && !"unknown".equalsIgnoreCase(xClientIp)) {
                if (isValidIpAddress(xClientIp) && !isPrivateNetwork(xClientIp)) {
                    return xClientIp;
                }
            }

            // Check CF-Connecting-IP header (Cloudflare)
            String cfConnectingIp = request.getHeader("CF-Connecting-IP");
            if (cfConnectingIp != null && !cfConnectingIp.isEmpty() && !"unknown".equalsIgnoreCase(cfConnectingIp)) {
                if (isValidIpAddress(cfConnectingIp) && !isPrivateNetwork(cfConnectingIp)) {
                    return cfConnectingIp;
                }
            }
        }

        // Fallback to getRemoteAddr()
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null && !remoteAddr.isEmpty() && !"unknown".equalsIgnoreCase(remoteAddr)) {
            return remoteAddr;
        }

        return "unknown";
    }

    /**
     * Checks if an IP address belongs to a private network.
     * Useful for filtering out internal network IPs when you want external client IPs.
     */
    public static boolean isPrivateNetwork(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        try {
            // IPv4 private ranges
            if (ip.matches("^10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
                return true; // 10.0.0.0/8
            }
            if (ip.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3}$")) {
                return true; // 172.16.0.0/12
            }
            if (ip.matches("^192\\.168\\.\\d{1,3}\\.\\d{1,3}$")) {
                return true; // 192.168.0.0/16
            }
            
            // Localhost
            if (ip.equals("127.0.0.1") || ip.equals("::1")) {
                return true;
            }

            // Docker internal networks (common ranges)
            if (ip.matches("^172\\.(1[7-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3}$")) {
                return true; // Docker default bridge network
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Basic validation for IP address format.
     * This is a simple check - for production, consider using a more robust validation library.
     */
    private static boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        // Basic IPv4 validation
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            try {
                for (String part : parts) {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255) {
                        return false;
                    }
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        // Basic IPv6 validation (simplified)
        if (ip.contains(":")) {
            return ip.matches("^[0-9a-fA-F:]+$");
        }
        
        return false;
    }
}
