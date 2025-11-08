package com.melowetty.hsepermhelper.aspect

import com.melowetty.hsepermhelper.annotation.TrackUserEvent
import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.service.UserEventService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ExecutorService

@Aspect
@Component
class UserEventTrackingAspect(
    private val userEventService: UserEventService,
    @Qualifier("add-user-events-executor-service")
    private val executorService: ExecutorService
) {
    @AfterReturning(
        pointcut = "@annotation(com.melowetty.hsepermhelper.annotation.TrackUserEvent)",
        returning = "result"
    )
    fun trackEvent(joinPoint: JoinPoint, result: Any?) {
        val userId = extractUserIdFromArgs(joinPoint.args) ?: return
        val eventType = extractEventType(joinPoint) ?: return

        executorService.submit {
            userEventService.addUserEvent(userId, eventType)
        }
    }

    private fun extractUserIdFromArgs(args: Array<Any>): UUID? {
        return args.find { it is UUID } as? UUID
    }

    private fun extractEventType(joinPoint: JoinPoint): UserEventType? {
        val method = joinPoint.signature as MethodSignature
        val annotation = method.method.getAnnotation(TrackUserEvent::class.java)
        return annotation?.value
    }
}