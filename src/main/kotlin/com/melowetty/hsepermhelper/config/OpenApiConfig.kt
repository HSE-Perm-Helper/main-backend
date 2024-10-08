package com.melowetty.hsepermhelper.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server

@OpenAPIDefinition(
    info = Info(
        title = "HSE Perm Helper API",
        description = "Melowetty", version = "1.03.0",
        contact = Contact(
            name = "Denis Malinin",
            email = "melowetty@mail.ru",
            url = "https://github.com/Melowetty"
        )
    ),
    servers = [
        Server(
            url = "https://api.hse-perm-helper.ru",
            description = "Production server"
        ),
        Server(
            url = "localhost:8080/api",
            description = "Local server for development"
        ),
    ]
)
@SecurityScheme(
    name = "X-Secret-Key",
    type = SecuritySchemeType.APIKEY,
    description = "Секретный ключ",
    `in` = SecuritySchemeIn.HEADER,
    paramName = "X-Secret-Key"
)
class OpenApiConfig