package com.sync.sprout.configuration

import com.sync.sprout.support.annotation.ErrorCodeExample
import com.sync.sprout.support.web.Constants
import com.sync.sprout.support.web.MessageKey
import com.sync.sprout.support.web.ViewEntity
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus

@Configuration(proxyBeanMethods = false)
@Profile(value = ["local", "dev"])
@OpenAPIDefinition(
    servers = [Server(url = "/", description = "Default Server URL")],
    info = Info(title = "Sprout Service", version = "202505")
)
class OpenApiConfiguration(
    private val messageSource: MessageSource,
    private val titleSource: MessageSource,
) {

    @Bean
    fun api(): GroupedOpenApi {
        return GroupedOpenApi
            .builder()
            .pathsToExclude(Constants.Path.HEALTH)
            .addOperationCustomizer(operationCustomizer())
            .group("00.api")
            .build()
    }

    private fun operationCustomizer(): OperationCustomizer {
        return OperationCustomizer { operation, handlerMethod ->
            operation.responses?.forEach { responseCode, apiResponse ->
                if (HttpStatus.valueOf(responseCode.toInt()).is2xxSuccessful) customizeResponse(apiResponse = apiResponse)
                addErrorCodeExample(
                    apiResponse = apiResponse,
                    messageKeys = handlerMethod.getMethodAnnotation(ErrorCodeExample::class.java)!!.messageKeys
                )

                MessageKey.EXCEPTION.addMediaType(apiResponse.content)
            }
            operation
        }
    }

    private fun customizeResponse(
        apiResponse: ApiResponse,
    ) {
        val content = apiResponse.content ?: Content()
        val newContent = Content()
        content.forEach { (key, mediaType) ->
            val schema = mediaType.schema ?: return
            val viewEntitySchema = Schema<Any>().apply {
                type = "object"
                properties = mapOf(
                    "code" to Schema<String>().apply { type = "string" },
                    "data" to schema,
                )
            }
            newContent.addMediaType(key, MediaType().schema(viewEntitySchema))
        }
        apiResponse.content = newContent
    }

    private fun addErrorCodeExample(
        apiResponse: ApiResponse,
        messageKeys: Array<MessageKey>,
    ) {
        val content = apiResponse.content ?: Content()
        messageKeys.distinct().forEach { it.addMediaType(content = content) }
        apiResponse.content = content
    }

    private fun MessageKey.addMediaType(content: Content) {
        val title = titleSource.getMessage(key, null, "", LocaleContextHolder.getLocale())!!
        val viewEntitySchema = Schema<Error>().apply {
            description(Error::class.java.name)
            type = "object"
            properties = buildMap {
                put("code", Schema<String>().apply { type = "string" })
                if (title.isNotEmpty()) {
                    put("title", Schema<String>().apply { type = "string" })
                }
                put("message", Schema<String>().apply { type = "string" })
            }
            example = ViewEntity.Error(
                code = this@addMediaType.name,
                title = title.ifEmpty { null },
                message = messageSource.getMessage(key, null, LocaleContextHolder.getLocale())
            )
        }
        content.addMediaType(this@addMediaType.name, MediaType().schema(viewEntitySchema))
    }
}