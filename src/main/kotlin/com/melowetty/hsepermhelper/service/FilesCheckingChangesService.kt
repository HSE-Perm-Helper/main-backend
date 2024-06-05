package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.models.File
import com.melowetty.hsepermhelper.models.FilesChanging

interface FilesCheckingChangesService {
    fun getChanges(before: List<File>, after: List<File>): FilesChanging
}