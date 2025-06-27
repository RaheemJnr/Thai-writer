package com.rjnr.thaiwrter.data.models

import kotlinx.serialization.Serializable


@Serializable
data class ThaiCharacter(
    val id: Int,
    val character: String,
    val pronunciation: String,
    val strokes: List<String> = listOf(),           // NEW – ordered centre-line paths
    val difficulty: Int = 0,
    val category: String = ""            // consonant, vowel, tone mark, …
)

val THAI_CHARACTERS = listOf(
    ThaiCharacter(
        id = 0,
        character = "ก",
        pronunciation = "ko kai",
        category = "consonant",
        strokes = listOf("M41.9998 706.5L47.9998 496C60.3998 395.2 150.833 358.333 197.5 323C-60.5 257 -24.5004 166 80.4996 68.5C181.844 -25.605 375 -16.5 489.797 68.5C495.531 72 564 133 557 323V706.5")
    ),
    ThaiCharacter(
        id = 1,
        character = "ข",
        pronunciation = "kho khai",
        category = "consonant",
        strokes =  listOf("M0.5 225.374C94.719 170.977 212.979 172.347 237.307 259.372C259.167 337.568 184.907 457.533 86.6575 415.764C-13.9773 372.981 20.15 259.372 43.3268 184.09C95.7267 60.2397 198.007 45.1834 237.307 45.1834C353.695 38.8694 431.288 146.692 402.065 225.374C314.162 462.047 285.172 600.811 340.595 667.351C413.653 746.032 601.083 692.121 615.695 667.351C630.306 642.58 624.26 149.121 624.26 0.5")
    ),
    ThaiCharacter(
        2, "ฃ", "kho khuat",
        category = "consonant",
        strokes = listOf("M0.5 287.5C94.719 233.103 225.479 222.164 249.807 309.189C271.667 387.385 208.25 498.769 110 457C9.36514 414.217 18.2797 313.136 39 218C55.95 140.174 110 52 157.5 52C205 52 201.486 155.252 259.5 161C319.898 166.984 284 46 382.5 66C433.829 76.4221 416.5 249 365.5 378.317C309.623 520 303.343 653.358 339.5 678.5C415 731 555 748 615 655.5C633.489 626.996 626.833 201.667 632 1")
    ),
    ThaiCharacter(
        3,
        "ค",
        "kho khwai",
        category = "consonant",
        strokes = listOf("M140.5 401C150.5 411 174.5 443 183 452.5C293.501 576 384.5 491.5 409.5 429C427.817 383.206 367.5 252 270 287C165.5 314.5 75.5 684.5 75.5 684.5C-3.49995 577 -12.6823 250.334 14 173C43.4999 87.5 186.5 15.5 264.5 2.5C342.5 -10.5 512.5 68.5 547.5 121.5C562.028 143.5 606 309 597 401L578.5 729.5")
    ),
    ThaiCharacter(4, "ฅ", "kho khon", category = "consonant"),
    ThaiCharacter(5, "ฆ", "kho rakhang", category = "consonant"),
    ThaiCharacter(6, "ง", "ngo ngu", category = "consonant"),

    ThaiCharacter(7, "จ", "cho chan", category = "consonant"),
    ThaiCharacter(8, "ฉ", "cho ching", category = "consonant"),
    ThaiCharacter(9, "ช", "cho chang", category = "consonant"),
    ThaiCharacter(10, "ซ", "so so", category = "consonant"),
    ThaiCharacter(11, "ฌ", "cho choe", category = "consonant"),
    ThaiCharacter(12, "ญ", "yo ying", category = "consonant"),

    ThaiCharacter(13, "ฎ", "do chada", category = "consonant"),
    ThaiCharacter(14, "ฏ", "to patak", category = "consonant"),
    ThaiCharacter(15, "ฐ", "tho than", category = "consonant"),
    ThaiCharacter(16, "ฑ", "tho montho", category = "consonant"),
    ThaiCharacter(17, "ฒ", "tho phuthao", category = "consonant"),
    ThaiCharacter(18, "ณ", "no nen", category = "consonant"),

    ThaiCharacter(19, "ด", "do dek", category = "consonant"),
    ThaiCharacter(20, "ต", "to tao", category = "consonant"),
    ThaiCharacter(21, "ถ", "tho thung", category = "consonant"),
    ThaiCharacter(22, "ท", "tho thahan", category = "consonant"),
    ThaiCharacter(23, "ธ", "tho thong", category = "consonant"),
    ThaiCharacter(24, "น", "no nu", category = "consonant"),

    ThaiCharacter(25, "บ", "bo baimai", category = "consonant"),
    ThaiCharacter(26, "ป", "po pla", category = "consonant"),
    ThaiCharacter(27, "ผ", "pho phueng", category = "consonant"),
    ThaiCharacter(28, "ฝ", "fo fa", category = "consonant"),
    ThaiCharacter(29, "พ", "pho phan", category = "consonant"),
    ThaiCharacter(30, "ฟ", "fo fan", category = "consonant"),
    ThaiCharacter(31, "ภ", "pho samphao", category = "consonant"),

    ThaiCharacter(32, "ม", "mo ma", category = "consonant"),
    ThaiCharacter(33, "ย", "yo yak", category = "consonant"),
    ThaiCharacter(34, "ร", "ro ruea", category = "consonant"),
    ThaiCharacter(35, "ฤ", "ru", category = "consonant"),
    ThaiCharacter(36, "ล", "lo ling", category = "consonant"),
    ThaiCharacter(37, "ว", "wo waen", category = "consonant"),

    ThaiCharacter(38, "ศ", "so sala", category = "consonant"),
    ThaiCharacter(39, "ษ", "so ruesi", category = "consonant"),
    ThaiCharacter(40, "ส", "so suea", category = "consonant"),
    ThaiCharacter(41, "ห", "ho hip", category = "consonant"),
    ThaiCharacter(42, "ฬ", "lo chula", category = "consonant"),
    ThaiCharacter(43, "อ", "o ang", category = "consonant"),

    /* --- simple vowels written as full glyphs --- */
    ThaiCharacter(44, "อะ", "sara a", category = "vowel"),
    ThaiCharacter(45, "อา", "sara aa", category = "vowel"),
    ThaiCharacter(46, "อำ", "sara am", category = "vowel"),

    /* --- final consonant --- */
    ThaiCharacter(47, "ฮ", "ho nokhuk", category = "consonant"),

    /* --- punctuation / special marks --- */
    ThaiCharacter(48, "ฯ", "paiyannoi", category = "punctuation"),
    ThaiCharacter(49, "เ", "sara e", category = "vowel"),
    ThaiCharacter(50, "แ", "sara ae", category = "vowel"),
    ThaiCharacter(51, "โ", "sara o", category = "vowel"),
    ThaiCharacter(52, "ใ", "sara ai mai muan", category = "vowel"),
    ThaiCharacter(53, "ไ", "sara ai mai malai", category = "vowel"),
    ThaiCharacter(54, "ๆ", "mai yamok", category = "punctuation")
)
