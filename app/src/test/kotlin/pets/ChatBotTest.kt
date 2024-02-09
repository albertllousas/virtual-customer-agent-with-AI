package pets

import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.Generation
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.SystemPromptTemplate


@Tag("integration")
class ChatBotTest {

    class FakeChatClient : ChatClient {
        override fun call(prompt: Prompt?): ChatResponse =
            ChatResponse(listOf(Generation("Hello, how can I help you?")))
    }

    private val chatClient = spyk<FakeChatClient>()

    private val chatBot = ChatBot(chatClient)

    @Test
    fun `should chat with the bot`() {
        val result = chatBot.chat(
            userMessage = "Hello, I need help with my order",
            previousChats = listOf(ChatMessage("Hello", "Hi!")),
            relatedDocs = listOf("doc1", "doc2 "),
            contextMessage = "default-context-message"
        )
        result shouldBe "Hello, how can I help you?"
        verify {
            chatClient.call(
                Prompt(
                    listOf(
                        UserMessage("Hello"),
                        AssistantMessage("Hi!"),
                        SystemPromptTemplate("default-context-message").createMessage(mapOf("documents" to "doc1, doc2")),
                        UserMessage("Hello, I need help with my order")
                    )
                )
            )
        }
    }
}
