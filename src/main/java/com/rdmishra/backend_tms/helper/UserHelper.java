package com.rdmishra.backend_tms.helper;

import java.util.UUID;

public class UserHelper {

    public static UUID parseUUId(String uuid) {
        return UUID.fromString(uuid);
    }
}
