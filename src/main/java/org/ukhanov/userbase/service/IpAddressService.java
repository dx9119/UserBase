package org.ukhanov.userbase.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IpAddressService {

    private static final Logger logger = LoggerFactory.getLogger(IpAddressService.class);

    public String getClientIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            logger.debug("Got IP from X-Forwarded-For: {}", forwarded);
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            logger.debug("Got IP from X-Real-IP: {}", realIp);
            return realIp;
        }
        String remoteAddr = request.getRemoteAddr();
        logger.debug("Using remoteAddr: {}", remoteAddr);
        return remoteAddr;
    }

    public String getProxyIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
