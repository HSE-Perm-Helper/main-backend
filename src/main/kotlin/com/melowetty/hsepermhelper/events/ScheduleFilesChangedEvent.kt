package com.melowetty.hsepermhelper.events

import com.melowetty.hsepermhelper.models.ScheduleFile

class ScheduleFilesChangedEvent(
    newFiles: List<ScheduleFile>,
): CustomEvent<List<ScheduleFile>>(newFiles, EventType.EDITED)