package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.entity.DataEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DataRepository: CrudRepository<DataEntity, String>