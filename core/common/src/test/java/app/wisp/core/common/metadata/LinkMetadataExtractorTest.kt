package app.wisp.core.common.metadata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LinkMetadataExtractorTest {

    @Test
    fun testParseHtmlWithOpenGraphTags() {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Sony WH-1000XM5 Noise Canceling Headphones | Amazon.in</title>
                <meta property="og:title" content="Sony WH-1000XM5 Noise Canceling Headphones" />
                <meta property="og:image" content="https://images.amazon.com/headphones.jpg" />
                <meta property="og:price:amount" content="24990.00" />
                <meta property="og:price:currency" content="INR" />
                <meta property="og:description" content="Industry leading noise cancellation." />
            </head>
            <body></body>
            </html>
        """.trimIndent()

        val metadata = LinkMetadataExtractor.parseHtml(html, "https://www.amazon.in/dp/B09XS7JWHH")

        assertEquals("Sony WH-1000XM5 Noise Canceling Headphones", metadata.title)
        assertEquals("https://images.amazon.com/headphones.jpg", metadata.imageUrl)
        assertEquals(24990.0, metadata.price ?: 0.0, 0.001)
        assertEquals("₹", metadata.currency)
        assertEquals("Amazon", metadata.store)
    }

    @Test
    fun testParseHtmlWithJsonLdProductSchema() {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Store Product</title>
                <script type="application/ld+json">
                {
                  "@context": "https://schema.org/",
                  "@type": "Product",
                  "name": "Nike Air Max 90",
                  "image": "https://nike.com/airmax90.jpg",
                  "offers": {
                    "@type": "Offer",
                    "priceCurrency": "USD",
                    "price": "129.99"
                  }
                }
                </script>
            </head>
            <body></body>
            </html>
        """.trimIndent()

        val metadata = LinkMetadataExtractor.parseHtml(html, "https://www.nike.com/shoes/airmax90")

        assertEquals("Nike Air Max 90", metadata.title)
        assertEquals("https://nike.com/airmax90.jpg", metadata.imageUrl)
        assertEquals(129.99, metadata.price ?: 0.0, 0.001)
        assertEquals("$", metadata.currency)
        assertEquals("Nike", metadata.store)
    }

    @Test
    fun testParseHtmlWithTwitterCardFallback() {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="twitter:title" content="NuPhy Air75 Keyboard" />
                <meta name="twitter:image" content="https://nuphy.com/air75.png" />
            </head>
            <body></body>
            </html>
        """.trimIndent()

        val metadata = LinkMetadataExtractor.parseHtml(html, "https://nuphy.com/products/air75")

        assertEquals("NuPhy Air75 Keyboard", metadata.title)
        assertEquals("https://nuphy.com/air75.png", metadata.imageUrl)
        assertEquals("Nuphy", metadata.store)
    }

    @Test
    fun testStoreDetection() {
        assertEquals("Amazon", LinkMetadataExtractor.extractStore("https://www.amazon.com/gp/product/123"))
        assertEquals("Flipkart", LinkMetadataExtractor.extractStore("https://www.flipkart.com/item/123"))
        assertEquals("Myntra", LinkMetadataExtractor.extractStore("https://myntra.com/tshirt"))
        assertEquals("Zara", LinkMetadataExtractor.extractStore("https://zara.com/jacket"))
        assertEquals("Apple", LinkMetadataExtractor.extractStore("https://apple.com/iphone-16"))
    }

    @Test
    fun testFallbackFromUrlWhenHtmlIsEmpty() {
        val metadata = LinkMetadataExtractor.fallbackFromUrl("https://www.ikea.com/in/en/p/poaeng-armchair-12345")
        assertNotNull(metadata.title)
        assertEquals("IKEA", metadata.store)
    }
}
