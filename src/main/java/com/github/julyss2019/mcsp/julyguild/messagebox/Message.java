package com.github.julyss2019.mcsp.julyguild.messagebox;

import java.util.UUID;

public interface Message {
    long getCreationTime();
    String getMessage();
    UUID getUuid();
}
