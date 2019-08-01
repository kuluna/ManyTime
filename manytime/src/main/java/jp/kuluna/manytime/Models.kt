package jp.kuluna.manytime

/**
 * NumberPadView の入力文字列
 */
enum class InputKey(val value: String) {
    NUM_0("0"),
    NUM_1("1"),
    NUM_2("2"),
    NUM_3("3"),
    NUM_4("4"),
    NUM_5("5"),
    NUM_6("6"),
    NUM_7("7"),
    NUM_8("8"),
    NUM_9("9"),
    OK("OK"),
    BACK("BACK");

    companion object {
        /**
         * 文字列を NumberPadView の入力文字列に変換します。変換できない場合は例外が発生します。
         *
         * @param input 変換する文字列
         * @return [InputKey]
         * @throws IllegalArgumentException 変換できない文字列が入力された
         */
        fun from(input: String): InputKey {
            return values().first { it.value == input }
        }
    }
}
