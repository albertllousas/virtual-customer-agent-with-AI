package pets

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.UUID

class ChatWithAVirtualAgentServiceTest {

    private val chatBot = mockk<ChatBot>()

    private val docsRepository = mockk<DocsRepository>()

    private val chatRepository = mockk<ChatRepository>(relaxed = true)

    private val fixedId = UUID.randomUUID()

    private val chat = ChatWithAVirtualAgentService(
        chatRepository,
        docsRepository,
        chatBot,
        generateId = { fixedId },
        contextMessage = "default-context-message"
    )

    @Test
    fun `initiate a chat with a virtual agent`() {
        val userMessage = "Hello, could ypu help me with my order?"
        every { chatRepository.getPreviousChats(fixedId) } returns emptyList()
        val relatedDocs = listOf("doc1", "doc2")
        every { docsRepository.getRelatedDocsFor(userMessage) } returns relatedDocs
        every {
            chatBot.chat(userMessage, emptyList(), relatedDocs, "default-context-message")
        } returns "Sure, what do you need help with?"

        val result = chat.chat(fixedId, userMessage)

        result shouldBe Pair(fixedId, "Sure, what do you need help with?")
    }

    @Test
    fun `keep chatting with a virtual agent`() {
        val chatId = UUID.randomUUID()
        val userMessage = "Hello, could ypu help me with my order?"
        val previousListMsgs = listOf(ChatMessage("Hello", "Hi!"))
        every { chatRepository.getPreviousChats(chatId) } returns previousListMsgs
        val relatedDocs = listOf("doc1", "doc2")
        every { docsRepository.getRelatedDocsFor(userMessage) } returns relatedDocs
        every {
            chatBot.chat(userMessage, previousListMsgs, relatedDocs, "default-context-message")
        } returns "Sure, what do you need help with?"

        val result = chat.chat(chatId, userMessage)

        result shouldBe Pair(chatId, "Sure, what do you need help with?")
    }
}
