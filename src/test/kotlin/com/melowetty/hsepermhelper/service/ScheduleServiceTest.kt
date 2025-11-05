package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.persistence.repository.ExcelScheduleRepository
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ScheduleServiceTest {
    @InjectMockKs
    private lateinit var scheduleService: ExcelScheduleService

    @MockK
    private lateinit var scheduleRepository: ExcelScheduleRepository

    @MockK
    private lateinit var oldUserService: OldUserService

    @MockK
    private lateinit var notificationService: NotificationService
}