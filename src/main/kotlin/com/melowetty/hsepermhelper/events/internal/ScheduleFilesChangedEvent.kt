package com.melowetty.hsepermhelper.events.internal

import com.melowetty.hsepermhelper.events.common.CustomEvent
import com.melowetty.hsepermhelper.events.common.EventType
import com.melowetty.hsepermhelper.models.ScheduleFile

class ScheduleFilesChangedEvent(
    newFiles: List<ScheduleFile>,
): CustomEvent<List<ScheduleFile>>(newFiles, EventType.EDITED)