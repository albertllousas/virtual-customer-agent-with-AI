package pets

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.AbstractEmbeddingClient
import org.springframework.ai.embedding.Embedding
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.ByteArrayInputStream

@Tag("integration")
class DocsRepositoryTest {

    private val fakeEmbeddingClient = object : AbstractEmbeddingClient() {
        override fun call(request: EmbeddingRequest?): EmbeddingResponse =
            EmbeddingResponse(listOf(Embedding(listOf(1.toDouble()), 1)))

        override fun embed(document: Document?): MutableList<Double> = mutableListOf(1.toDouble())
    }

    private val vectorStore = SimpleVectorStore(fakeEmbeddingClient)

    private val docsRepository = DocsRepository(vectorStore)

    @Test
    fun `should add and retrieve documents`() {
        docsRepository.add(PathMatchingResourcePatternResolver().getResource("classpath:/context.txt"))

        val relatedDocs = docsRepository.getRelatedDocsFor("Hello, I need help with my order")

        relatedDocs shouldBe listOf("Context for the AI")
    }
}
