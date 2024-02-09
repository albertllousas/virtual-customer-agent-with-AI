package pets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.embedding.EmbeddingClient
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Repository
import java.lang.invoke.MethodHandles

@Configuration
class DocsConfig {
    @Bean
    fun vectorStore(embeddingClient: EmbeddingClient): VectorStore = SimpleVectorStore(embeddingClient)
}

@Repository
class DocsRepository(
    private val vectorStore: VectorStore,
    private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
) {

    fun getRelatedDocsFor(userMessage: String): List<String> =
        vectorStore.similaritySearch(userMessage).map { it.content }

    fun add(doc: Resource) {
        vectorStore.accept(TokenTextSplitter().apply(TextReader(doc).get()))
    }

    @EventListener(ApplicationReadyEvent::class)
    fun ingestFilesAfterStartup() {
        PathMatchingResourcePatternResolver().getResources("classpath*:docs/**/*")
            .forEach {
                logger.info("Ingesting file: ${it.filename}")
                add(it)
            }
    }
}
