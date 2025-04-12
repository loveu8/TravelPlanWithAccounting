package com.travelPlanWithAccounting.service.uitl;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

public class UuidGeneratorUtils {

    private UuidGeneratorUtils() {
    }

    public static UUID generateUuid() {
        return generateUuidV7();
    }

    public static UUID generateUuidV7() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    public static boolean isValidUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
