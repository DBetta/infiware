package ke.co.infiware.exceptions

@FunctionalInterface
interface ExceptionIdMaker {
    fun make(t: Throwable?): String?
}