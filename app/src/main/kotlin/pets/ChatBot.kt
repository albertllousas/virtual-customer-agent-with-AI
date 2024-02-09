package pets

import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.stereotype.Component

@Component
class ChatBot(private val chatClient: ChatClient) {
    fun chat(
        userMessage: String,
        previousChats: List<ChatMessage>,
        relatedDocs: List<String>,
        contextMessage: String
    ): String {
        val docsAsString = relatedDocs.joinToString(System.lineSeparator())
        val systemMessage = SystemPromptTemplate(contextMessage).createMessage(mapOf("documents" to docsAsString))
        val previousMessages = previousChats.flatMap { listOf(UserMessage(it.question), AssistantMessage(it.answer)) }
        val prompt = Prompt(previousMessages + listOf(systemMessage, UserMessage(userMessage)))
        val chatResponse = chatClient.call(prompt)
        return chatResponse.result.output.content
    }
}
