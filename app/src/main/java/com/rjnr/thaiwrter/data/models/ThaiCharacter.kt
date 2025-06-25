package com.rjnr.thaiwrter.data.models

import android.util.Log.d
import kotlinx.serialization.Serializable


@Serializable
data class ThaiCharacter(
    val id: Int,
    val character: String,
    val pronunciation: String,
    val svgPathData: String = "",
    val difficulty: Int = 0,
    val category: String = ""    // consonant, vowel, tone mark, etc.
)

val THAI_CHARACTERS = listOf(
    ThaiCharacter(
        id = 0,
        character = "ก",
        pronunciation = "ko kai",
        category = "consonant",
        svgPathData = "M9.00007 723.5C9.00007 469.5 93.3334 314 135.5 268C-2.89995 193.2 -10.5 139.5 9.00001 111.5C114.857 -40.5 469.5 -31 549 111.5V723.5"
        ),
    ThaiCharacter(
        id = 1,
        character = "ข",
        pronunciation = "kho khai",
        category = "consonant",
        svgPathData = "M9.00007 723.5C9.00007 469.5 93.3334 314 135.5 268C-2.89995 193.2 -10.5 139.5 9.00001 111.5C114.857 -40.5 469.5 -31 549 111.5V723.5"
    ),
    ThaiCharacter(
        2, "ฃ", "kho khuat",
        category = "consonant",
        svgPathData = "M9.00007 723.5C9.00007 469.5 93.3334 314 135.5 268C-2.89995 193.2 -10.5 139.5 9.00001 111.5C114.857 -40.5 469.5 -31 549 111.5V723.5"
    ),
    ThaiCharacter(
        3,
        "ค",
        "kho khwai",
        category = "consonant",
        svgPathData = "M9.00007 723.5C9.00007 469.5 93.3334 314 135.5 268C-2.89995 193.2 -10.5 139.5 9.00001 111.5C114.857 -40.5 469.5 -31 549 111.5V723.5"
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
