package pets

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/customer-service")
class CustomerServiceRestResource(
    private val virtualAgent: ChatWithAVirtualAgentService
) {

    @PostMapping("/chats")
    fun chat(@RequestBody request: ChatRequest): ChatResponse =
        virtualAgent.chat(userMessage = request.message).let { ChatResponse(chatId = it.first, message = it.second)}

    @PutMapping("/chats/{id}")
    fun chat(@PathVariable id: UUID, @RequestBody request: ChatRequest): ChatResponse =
        virtualAgent.chat(id, request.message).let { ChatResponse(chatId = it.first, message = it.second)}
}

data class ChatRequest(val message: String)

data class ChatResponse(val chatId: UUID, val message: String)
