package app.wisp.core.common.metadata

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URI

data class LinkMetadata(
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val price: Double? = null,
    val currency: String = "₹",
    val store: String? = null,
    val canonicalUrl: String? = null
)

object LinkMetadataExtractor {

    suspend fun extract(
        url: String,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO
    ): LinkMetadata = withContext(dispatcher) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return@withContext fallbackFromUrl(url)
        }

        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36")
                .timeout(6000)
                .followRedirects(true)
                .get()
            parseDocument(doc, url)
        } catch (_: Exception) {
            fallbackFromUrl(url)
        }
    }

    fun parseHtml(html: String, baseUri: String): LinkMetadata {
        val doc = Jsoup.parse(html, baseUri)
        return parseDocument(doc, baseUri)
    }

    private fun parseDocument(doc: Document, sourceUrl: String): LinkMetadata {
        val store = extractStore(sourceUrl)

        // 1. Open Graph Meta Tags
        val ogTitle = doc.selectFirst("meta[property=og:title]")?.attr("content")?.trim()
        val ogImage = doc.selectFirst("meta[property=og:image]")?.attr("content")?.trim()
        val ogDescription = doc.selectFirst("meta[property=og:description]")?.attr("content")?.trim()
        val ogPrice = doc.selectFirst("meta[property=og:price:amount]")?.attr("content")?.trim()
            ?: doc.selectFirst("meta[property=product:price:amount]")?.attr("content")?.trim()
        val ogCurrency = doc.selectFirst("meta[property=og:price:currency]")?.attr("content")?.trim()
            ?: doc.selectFirst("meta[property=product:price:currency]")?.attr("content")?.trim()

        // 2. Twitter Meta Tags
        val twitterTitle = doc.selectFirst("meta[name=twitter:title]")?.attr("content")?.trim()
        val twitterImage = doc.selectFirst("meta[name=twitter:image]")?.attr("content")?.trim()

        // 3. JSON-LD Product Extraction
        var jsonLdPrice: Double? = null
        var jsonLdCurrency: String? = null
        var jsonLdName: String? = null
        var jsonLdImage: String? = null

        val jsonLdScripts = doc.select("script[type=application/ld+json]")
        for (script in jsonLdScripts) {
            val jsonText = script.data()
            if (jsonText.contains("Product", ignoreCase = true) || jsonText.contains("price", ignoreCase = true)) {
                jsonLdPrice = extractPriceFromJson(jsonText)
                jsonLdCurrency = extractCurrencyFromJson(jsonText)
                jsonLdName = extractFieldFromJson(jsonText, "name")
                jsonLdImage = extractFieldFromJson(jsonText, "image")
                if (jsonLdPrice != null) break
            }
        }

        // Canonical URL
        val canonical = doc.selectFirst("link[rel=canonical]")?.attr("href")?.trim()
            ?: doc.selectFirst("meta[property=og:url]")?.attr("content")?.trim()

        // Fallback title hierarchy
        val effectiveTitle = listOfNotNull(ogTitle, twitterTitle, jsonLdName, doc.title().trim())
            .firstOrNull { it.isNotBlank() }
            ?: fallbackTitleFromUrl(sourceUrl)

        // Price hierarchy
        val effectivePrice = jsonLdPrice
            ?: ogPrice?.toDoubleOrNull()

        val effectiveCurrency = jsonLdCurrency
            ?: sanitizeCurrency(ogCurrency)
            ?: "₹"

        val effectiveImage = listOfNotNull(ogImage, twitterImage, jsonLdImage)
            .firstOrNull { it.isNotBlank() }

        return LinkMetadata(
            title = sanitizeTitle(effectiveTitle),
            description = ogDescription,
            imageUrl = effectiveImage,
            price = effectivePrice,
            currency = effectiveCurrency,
            store = store,
            canonicalUrl = canonical
        )
    }

    private fun extractPriceFromJson(json: String): Double? {
        val priceRegex = Regex(""""price"\s*:\s*"?([0-9]+(?:\.[0-9]+)?)"?""", RegexOption.IGNORE_CASE)
        val match = priceRegex.find(json)
        return match?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun extractCurrencyFromJson(json: String): String? {
        val currRegex = Regex(""""priceCurrency"\s*:\s*"([A-Za-z]{3})"""", RegexOption.IGNORE_CASE)
        val match = currRegex.find(json)
        return match?.groupValues?.get(1)?.let { sanitizeCurrency(it) }
    }

    private fun extractFieldFromJson(json: String, field: String): String? {
        val regex = Regex(""""$field"\s*:\s*"([^"]+)"""", RegexOption.IGNORE_CASE)
        return regex.find(json)?.groupValues?.get(1)?.trim()
    }

    private fun sanitizeCurrency(code: String?): String? {
        if (code.isNullOrBlank()) return null
        return when (code.uppercase()) {
            "INR" -> "₹"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            else -> code
        }
    }

    private fun sanitizeTitle(title: String): String {
        return title
            .replace(Regex("""\s*\|\s*.*$"""), "") // Remove " | Amazon.in" etc.
            .replace(Regex("""\s*-\s*Buy\s+.*$""", RegexOption.IGNORE_CASE), "")
            .trim()
    }

    fun extractStore(url: String): String? {
        if (url.isBlank()) return null
        val host = try {
            URI(url).host?.replace("www.", "") ?: ""
        } catch (_: Exception) {
            ""
        }
        return when {
            host.contains("amazon", ignoreCase = true) -> "Amazon"
            host.contains("flipkart", ignoreCase = true) -> "Flipkart"
            host.contains("myntra", ignoreCase = true) -> "Myntra"
            host.contains("ajio", ignoreCase = true) -> "Ajio"
            host.contains("nike", ignoreCase = true) -> "Nike"
            host.contains("zara", ignoreCase = true) -> "Zara"
            host.contains("apple", ignoreCase = true) -> "Apple"
            host.contains("ikea", ignoreCase = true) -> "IKEA"
            host.contains("etsy", ignoreCase = true) -> "Etsy"
            host.contains("ebay", ignoreCase = true) -> "eBay"
            host.isNotBlank() -> host.substringBefore(".")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            else -> null
        }
    }

    fun fallbackFromUrl(url: String): LinkMetadata {
        return LinkMetadata(
            title = fallbackTitleFromUrl(url),
            store = extractStore(url),
            canonicalUrl = url
        )
    }

    private fun fallbackTitleFromUrl(url: String): String {
        return try {
            val uri = URI(url)
            val path = uri.path?.trim('/') ?: ""
            if (path.isNotBlank()) {
                val lastSegment = path.substringAfterLast('/')
                    .replace("-", " ")
                    .replace("_", " ")
                    .take(60)
                if (lastSegment.isNotBlank()) {
                    return lastSegment.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }
            uri.host?.replace("www.", "") ?: "New Wish"
        } catch (_: Exception) {
            "New Wish"
        }
    }
}
