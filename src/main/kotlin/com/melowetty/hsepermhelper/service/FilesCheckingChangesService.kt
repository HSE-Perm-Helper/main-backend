package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.file.File
import com.melowetty.hsepermhelper.model.file.FilesChanging

interface FilesCheckingChangesService {
    fun getChanges(before: List<File>, after: List<File>): FilesChanging
}