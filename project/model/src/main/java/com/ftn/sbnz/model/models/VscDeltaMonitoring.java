package com.ftn.sbnz.model.models;


import java.time.LocalDateTime;

import com.ftn.sbnz.model.enums.DeltaStatus;

public class VscDeltaMonitoring {

    private Driver driver;
    private LocalDateTime vscActivatedAt;
    private DeltaStatus deltaStatus;
    private boolean active;
}