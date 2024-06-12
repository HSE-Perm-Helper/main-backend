package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.model.File
import com.melowetty.hsepermhelper.model.FilesChanging
import com.melowetty.hsepermhelper.service.FilesCheckingChangesService
import org.springframework.stereotype.Service

@Service
class FilesCheckingChangesByHashcodeService: FilesCheckingChangesService {
    override fun getChanges(before: List<File>, after: List<File>): FilesChanging {
        val addedOrChanged: MutableList<File> = mutableListOf()
        val withoutChanges: MutableList<File> = mutableListOf()
        val deleted: MutableList<File> = mutableListOf()
        val beforeSet = before.toHashSet()
        val afterSet = after.toHashSet()
        afterSet.forEach { file: File ->
            if (beforeSet.contains(file)) {
                withoutChanges.add(file)
            } else {
                addedOrChanged.add(file)
            }
        }
        beforeSet.forEach { file: File ->
            if(!afterSet.contains(file)) deleted.add(file)
        }
        return FilesChanging(
            addedOrChanged = addedOrChanged,
            withoutChanges = withoutChanges,
            deleted = deleted
        )
    }

}