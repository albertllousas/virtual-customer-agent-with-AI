package pets

import org.springframework.stereotype.Service
import java.util.UUID

const val defaultContextMessage = """
            You're playing a role of a virtual customer agent in a company called Next Gen Pets.
            At Next Gen Pets we sell advanced robotic companions designed with cutting-edge AI, offering interactive 
            experiences and personalized companionship.
            Please use Use the information from the DOCUMENTS section to deliver precise responses, presenting the knowledge 
            as if it was innate. If uncertain, plainly admit to not knowing and explain that you are a virtual agent. 
            Ask always for the user's name and use it in the conversation.
                    
            DOCUMENTS:
            {documents}
            """

@Service
class ChatWithAVirtualAgentService(
    private val chatRepository: ChatRepository,
    private val docsRepository: DocsRepository,
    private val chatBot: ChatBot,
    private val generateId: () -> UUID = { UUID.randomUUID() },
    private val contextMessage: String = defaultContextMessage
) {

    fun chat(chatId: UUID? = null, userMessage: String): Pair<UUID, String> {
        val id = chatId ?: generateId()
        val previousChats = chatRepository.getPreviousChats(id)
        val relatedDocs = docsRepository.getRelatedDocsFor(userMessage)
        val response = chatBot.chat(userMessage, previousChats, relatedDocs, contextMessage)
        chatRepository.saveChat(id, userMessage, response)
        return Pair(id, response)
    }
}
