package pets

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@Tag("integration")
@WebMvcTest(CustomerServiceRestResource::class)
class CustomerServiceRestResourceTest(
    @Autowired private val mockMvc: MockMvc
) {

    @MockkBean
    private lateinit var virtualAgent: ChatWithAVirtualAgentService

    @Test
    fun `should start a chat with a virtual agent`() {
        val id = UUID.randomUUID()
        every {
            virtualAgent.chat(chatId = null, userMessage = "Hello, I need help with my order")
        } returns Pair(id, "Hi! I'm a virtual agent. How can I help you?")

        val result = mockMvc.perform(
            post("/customer-service/chats")
                .contentType(APPLICATION_JSON)
                .content("""{"message": "Hello, I need help with my order"}""")
        )
        result
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """{"chatId": $id, "message": "Hi! I'm a virtual agent. How can I help you?"}"""
                )
            )
    }

    @Test
    fun `should continue a chat with a virtual agent`() {
        val id = UUID.randomUUID()
        every {
            virtualAgent.chat(chatId = id, userMessage = "I need to know the status of my order")
        } returns Pair(id, "Your order is on its way")

        val result = mockMvc.perform(
            put("/customer-service/chats/$id")
                .contentType(APPLICATION_JSON)
                .content("""{"message": "I need to know the status of my order"}""")
        )
        result
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """{"chatId": $id, "message": "Your order is on its way"}"""
                )
            )
    }
}
