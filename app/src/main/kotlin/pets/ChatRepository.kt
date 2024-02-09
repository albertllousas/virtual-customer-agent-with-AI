package pets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.TimeUnit.DAYS

private const val limitOfChatsToLoad = 50

@Repository
class ChatRepository(private val redis: RedisTemplate<String, ChatMessage>) {

    fun getPreviousChats(id: UUID): List<ChatMessage> {
        return redis.opsForList().range(id.toString(), 0, -1)?.takeLast(limitOfChatsToLoad) ?: emptyList()
    }

    fun saveChat(chatId: UUID, userMessage: String, response: String) {
        redis.opsForList().rightPush(chatId.toString(), ChatMessage(userMessage, response))
        redis.expire(chatId.toString(), 24, DAYS)
    }
}

data class ChatMessage(val question: String, val answer: String)

@Configuration
class RedisConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, ChatMessage> {
        val objectMapper = jacksonObjectMapper()
        val redisTemplate: RedisTemplate<String, ChatMessage> = RedisTemplate()
        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = object : RedisSerializer<ChatMessage> {
            override fun serialize(value: ChatMessage?): ByteArray? = value?.let { objectMapper.writeValueAsBytes(it) }
            override fun deserialize(bytes: ByteArray?): ChatMessage? = bytes?.let { objectMapper.readValue(it) }
        }
        return redisTemplate
    }
}
