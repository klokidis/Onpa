package com.example.onpa.utils

import java.util.Locale

class SttLanguagesProvider {
    companion object {
        val supportedSpeechRecognitionLanguages =
            listOf(//all the available languages of the google api
                "en-US", // English (US)
                "en-AU", // English (Australia)
                "en-CA", // English (Canada)
                "en-GH", // English (Ghana)
                "en-IN", // English (India)
                "en-IE", // English (Ireland)
                "en-KE", // English (Kenya)
                "en-NZ", // English (New Zealand)
                "en-NG", // English (Nigeria)
                "en-PH", // English (Philippines)
                "en-SG", // English (Singapore)
                "en-ZA", // English (South Africa)
                "en-TZ", // English (Tanzania)
                "en-GB", // English (UK)
                "af-ZA", // Afrikaans
                "am-ET", // Amharic
                "ar-DZ", // Arabic (Algeria)
                "ar-BH", // Arabic (Bahrain)
                "ar-EG", // Arabic (Egypt)
                "ar-IQ", // Arabic (Iraq)
                "ar-IL", // Arabic (Israel)
                "ar-JO", // Arabic (Jordan)
                "ar-KW", // Arabic (Kuwait)
                "ar-LB", // Arabic (Lebanon)
                "ar-MA", // Arabic (Morocco)
                "ar-OM", // Arabic (Oman)
                "ar-QA", // Arabic (Qatar)
                "ar-SA", // Arabic (Saudi Arabia)
                "ar-PS", // Arabic (Palestine)
                "ar-SY", // Arabic (Syria)
                "ar-TN", // Arabic (Tunisia)
                "ar-AE", // Arabic (UAE)
                "ar-YE", // Arabic (Yemen)
                "hy-AM", // Armenian
                "az-AZ", // Azerbaijani
                "eu-ES", // Basque
                "bn-BD", // Bengali (Bangladesh)
                "bn-IN", // Bengali (India)
                "bs-BA", // Bosnian
                "bg-BG", // Bulgarian
                "my-MM", // Burmese
                "ca-ES", // Catalan
                "zh-HK", // Chinese (Hong Kong)
                "zh-CN", // Chinese (Mandarin)
                "zh-TW", // Chinese (Taiwanese)
                "hr-HR", // Croatian
                "cs-CZ", // Czech
                "da-DK", // Danish
                "nl-BE", // Dutch (Belgium)
                "nl-NL", // Dutch (Netherlands)
                "et-EE", // Estonian
                "fil-PH", // Filipino
                "fi-FI", // Finnish
                "fr-BE", // French (Belgium)
                "fr-CA", // French (Canada)
                "fr-FR", // French (France)
                "fr-CH", // French (Switzerland)
                "gl-ES", // Galician
                "ka-GE", // Georgian
                "de-AT", // German (Austria)
                "de-DE", // German (Germany)
                "de-CH", // German (Switzerland)
                "el-GR", // Greek
                "gu-IN", // Gujarati
                "ha-NG", // Hausa
                "he-IL", // Hebrew
                "hi-IN", // Hindi
                "hu-HU", // Hungarian
                "is-IS", // Icelandic
                "ig-NG", // Igbo
                "id-ID", // Indonesian
                "ga-IE", // Irish
                "it-IT", // Italian (Italy)
                "it-CH", // Italian (Switzerland)
                "ja-JP", // Japanese
                "jv-ID", // Javanese
                "kn-IN", // Kannada
                "kk-KZ", // Kazakh
                "km-KH", // Khmer
                "rw-RW", // Kinyarwanda
                "ko-KR", // Korean
                "lo-LA", // Lao
                "lv-LV", // Latvian
                "lt-LT", // Lithuanian
                "lb-LU", // Luxembourgish
                "mk-MK", // Macedonian
                "ms-MY", // Malay (Malaysia)
                "ml-IN", // Malayalam
                "mt-MT", // Maltese
                "mi-NZ", // Māori
                "mr-IN", // Marathi
                "mn-MN", // Mongolian
                "ne-NP", // Nepali
                "nb-NO", // Norwegian Bokmål
                "nn-NO", // Norwegian Nynorsk
                "ps-AF", // Pashto
                "fa-IR", // Persian
                "pl-PL", // Polish
                "pt-BR", // Portuguese (Brazil)
                "pt-PT", // Portuguese (Portugal)
                "pa-IN", // Punjabi (India)
                "pa-PK", // Punjabi (Pakistan)
                "ro-RO", // Romanian
                "ru-RU", // Russian
                "sr-RS", // Serbian
                "si-LK", // Sinhala
                "sk-SK", // Slovak
                "sl-SI", // Slovenian
                "so-SO", // Somali
                "st-ZA", // Southern Sotho
                "es-AR", // Spanish (Argentina)
                "es-BO", // Spanish (Bolivia)
                "es-CL", // Spanish (Chile)
                "es-CO", // Spanish (Colombia)
                "es-CR", // Spanish (Costa Rica)
                "es-DO", // Spanish (Dominican Republic)
                "es-EC", // Spanish (Ecuador)
                "es-SV", // Spanish (El Salvador)
                "es-GQ", // Spanish (Equatorial Guinea)
                "es-GT", // Spanish (Guatemala)
                "es-HN", // Spanish (Honduras)
                "es-MX", // Spanish (Mexico)
                "es-NI", // Spanish (Nicaragua)
                "es-PA", // Spanish (Panama)
                "es-PY", // Spanish (Paraguay)
                "es-PE", // Spanish (Peru)
                "es-PR", // Spanish (Puerto Rico)
                "es-ES", // Spanish (Spain)
                "es-US", // Spanish (US)
                "es-UY", // Spanish (Uruguay)
                "es-VE", // Spanish (Venezuela)
                "sw-KE", // Swahili (Kenya)
                "sw-TZ", // Swahili (Tanzania)
                "sv-SE", // Swedish
                "ta-IN", // Tamil (India)
                "ta-MY", // Tamil (Malaysia)
                "ta-SG", // Tamil (Singapore)
                "ta-LK", // Tamil (Sri Lanka)
                "te-IN", // Telugu
                "th-TH", // Thai
                "tr-TR", // Turkish
                "uk-UA", // Ukrainian
                "ur-IN", // Urdu (India)
                "ur-PK", // Urdu (Pakistan)
                "uz-UZ", // Uzbek
                "vi-VN", // Vietnamese
                "cy-GB", // Welsh
                "xh-ZA", // Xhosa
                "yo-NG", // Yoruba
                "zu-ZA"  // Zulu
            )

        val displayLanguages = supportedSpeechRecognitionLanguages.map { code ->
            val locale = Locale.forLanguageTag(code)
            "${locale.displayLanguage} (${locale.country})"
        }// Shows: "English (US)", "Spanish (Spain)", etc.
    }
}