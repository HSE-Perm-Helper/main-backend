package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.File
import com.melowetty.hsepermhelper.model.FilesChanging

interface FilesCheckingChangesService {
    fun getChanges(before: List<File>, after: List<File>): FilesChanging
}