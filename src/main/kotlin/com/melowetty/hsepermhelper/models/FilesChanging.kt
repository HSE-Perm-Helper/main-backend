package com.melowetty.hsepermhelper.models

data class FilesChanging(
    val addedOrChanged: List<File>,
    val withoutChanges: List<File>,
    val deleted: List<File>,
)
