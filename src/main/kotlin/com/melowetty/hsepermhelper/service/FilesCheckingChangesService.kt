package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.domain.model.file.FilesChanging

interface FilesCheckingChangesService {
    fun getChanges(before: List<File>, after: List<File>): FilesChanging
}