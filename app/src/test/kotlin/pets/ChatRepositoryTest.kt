package pets

import com.redis.testcontainers.RedisContainer
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.testcontainers.utility.DockerImageName
import java.util.UUID

@Tag("integration")
class ChatRepositoryTest {

    private val redis = RedisContainer(DockerImageName.parse("redis:6.2.6"))
        .withExposedPorts(6379)
        .also { it.start() }

    private val redisConfig = RedisStandaloneConfiguration().apply {
        hostName = redis.host
        port = redis.getMappedPort(6379)
    }

    private val connectionFactory = JedisConnectionFactory(redisConfig).also { it.afterPropertiesSet() }

    private val redisTemplate = RedisConfig().redisTemplate(connectionFactory).also { it.afterPropertiesSet() }

    private val chatRepository = ChatRepository(redisTemplate)

    @Test
    fun `should save chats and retrieve them`() {
        val chatId = UUID.randomUUID()

        chatRepository.saveChat(chatId, "Hello", "Hi! How can I help you?")
        chatRepository.saveChat(chatId, "Nothing really", "Amazing!")

        val chats = chatRepository.getPreviousChats(chatId)

        chats shouldBe listOf(
            ChatMessage("Hello", "Hi! How can I help you?"), ChatMessage("Nothing really", "Amazing!")
        )
    }
}
