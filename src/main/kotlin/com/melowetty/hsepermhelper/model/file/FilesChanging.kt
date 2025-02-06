package com.melowetty.hsepermhelper.model.file

data class FilesChanging(
    val addedOrChanged: List<File> = listOf(),
    val withoutChanges: List<File> = listOf(),
    val deleted: List<File> = listOf(),
)
